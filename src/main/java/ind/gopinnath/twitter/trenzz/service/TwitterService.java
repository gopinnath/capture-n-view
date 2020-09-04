package ind.gopinnath.twitter.trenzz.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/")
@RegisterRestClient
public interface TwitterService {

    @POST
    @Path("oauth2/token")
    @Produces("application/json")
    public AuthToken authenticate(@FormParam String grant_type,
    		@HeaderParam("Authorization") String basicAuth);
    
    @POST
    @Path("oauth2/invalidate_token")
    @Produces("application/json")
    public String invalidateToken(@FormParam String access_token,
    		@HeaderParam("Authorization") String invalidateString);
    
    @GET
    @Path("1.1/trends/place.json")
    @Produces("application/json")
    public Trends[] fetchTrends(@QueryParam String id,
    		@HeaderParam("Authorization") String bearerToken);

}
