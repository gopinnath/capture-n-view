package ind.gopinnath.twitter.trenzz;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import ind.gopinnath.twitter.trenzz.util.DateUtil;

@ApplicationScoped
@Path("/api")
public class Resource {
	
	@Inject 
	private MongoClient mongoClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/trenzz")
    public Summaries summarize() {
		Summaries summaries = new Summaries();
        List<HourlySummary> hourlyList = new ArrayList<>();
        //Long beginingOfWeek = DateUtil.getEpochValue(DateUtil.getBeginingOfWeekHour());
        Long before48Hours = DateUtil.getEpochValue(DateUtil.getHourBefore48Hours());
        MongoCursor<Document> cursor = getCollection()
        		.find(Filters.gte("name",before48Hours))
        		.sort(Sorts.ascending("name"))
        		.iterator();
        Jsonb jsonb = JsonbBuilder.create();
        try {
            while (cursor.hasNext()) {
                hourlyList.add(jsonb.fromJson(cursor.next().getString("description"),HourlySummary.class));
            }
        } finally {
            cursor.close();
        }
        summaries.setHourlySummaries(hourlyList);
		return summaries;
	}
	

    private MongoCollection<Document> getCollection(){
        return mongoClient.getDatabase("twittertrend").getCollection("trends");
    }
}