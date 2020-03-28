package de.moduliertersingvogel.mqttcache.endpoints;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.moduliertersingvogel.mqttcache.model.CacheEntry;

@Path("/data/")
public class DataEndpoint {
	
	@Inject @Named("mqttcache")
	List<CacheEntry> mqttcache;

	@GET
	@Produces("application/json")
	public Response doGet() {
		return Response.ok(mqttcache).build();
	}
}