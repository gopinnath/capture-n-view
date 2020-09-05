package ind.gopinnath.twitter.trenzz.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import ind.gopinnath.twitter.trenzz.HourlySummary;
import ind.gopinnath.twitter.trenzz.TrendResponse;
import ind.gopinnath.twitter.trenzz.util.DateUtil;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class TaskScheduler {

    private static final Logger LOGGER = Logger.getLogger(TaskScheduler.class.getName()); 
	
    @ConfigProperty(name = "twitter.consumerKey")
    private String consumerKey;
	
    @ConfigProperty(name = "twitter.secret")
    private String secret;
    
    @ConfigProperty(name = "twitter.woeid")
    private String woeid;

	@Inject 
	private MongoClient mongoClient;
	
    @Inject
    @RestClient
    private TwitterService service;

    
    @Scheduled(cron = "0 1 * ? * *")
    public void queryAndCaptureTrend() {
    	AuthToken token = service.authenticate("client_credentials",generateBasicAuthValue());
    	Trends trends = service.fetchTrends(woeid,getBearerHeaderValue(token))[0];
    	persistTrends(trends);
    }

    @Scheduled(cron="0 5 1 ? * *")
    public void purgeOldTrends() {
		LOGGER.info(" records purged.");
    }

    
    private void persistTrends(Trends trends) {
		LocalDateTime tweetHour = DateUtil.buildTweetHour();
		HourlySummary hourlySummary = buildHourlySummary(tweetHour,trends.getTrends());
        Jsonb jsonb = JsonbBuilder.create();
        String description = jsonb.toJson(hourlySummary);
		Document document = new Document()
                .append("name", DateUtil.getEpochValue(tweetHour))
                .append("description", description);
        getCollection().insertOne(document);
		LOGGER.info("Trend Info Loaded For Hour " + tweetHour);
	}

    private String generateBasicAuthValue()	{
    	StringBuilder finalString = new StringBuilder();
    	String authString = consumerKey + ":" + secret;
    	String encodedString = Base64.getEncoder()
				.encodeToString(authString.getBytes());
    	finalString.append("Basic ").append(encodedString);
    	return finalString.toString();
    }
    
    private String getBearerHeaderValue(AuthToken token)	{
    	StringBuilder finalString = new StringBuilder();
    	finalString.append("Bearer ").append(token.getAccess_token()); 
    	return finalString.toString();
    }
    
    private MongoCollection<Document> getCollection(){
        return mongoClient.getDatabase("twittertrend").getCollection("trends");
    }
    
	private HourlySummary buildHourlySummary(LocalDateTime hour,Trend[] trenzz)	{
		HourlySummary hourlySummary = new HourlySummary();
		hourlySummary.setHour(hour);
		for(int i = 0;i < 10;i++)	{
			buildTrenzz(trenzz[i],i+1,hourlySummary);
		}		
		return hourlySummary;
	}
	
	private void buildTrenzz(Trend twitterTrend,int sequence,HourlySummary hourlySummary)	{
		TrendResponse trend = new TrendResponse();
		trend.setName(twitterTrend.getName());
		trend.setTrendSequence(sequence);
		trend.setTweetVolume(twitterTrend.getTweet_volume());
		hourlySummary.getTrends().add(trend);
	}
}
