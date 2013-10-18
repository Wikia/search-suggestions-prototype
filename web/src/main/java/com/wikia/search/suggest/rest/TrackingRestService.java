package com.wikia.search.suggest.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("track")
public class TrackingRestService {
    private static Logger logger = LoggerFactory.getLogger(TrackingRestService.class);

    @GET
    @Path("/suggest-click")
    @Produces("application/json")
    public Response track(@QueryParam("wikiId") Integer wikiId,
                          @QueryParam("pageId") Integer clickedPageId,
                          @QueryParam("bid") String beaconId,
                          @QueryParam("pvid") String pageViewId ) {
        logger.info(String.format("%d/%d/%s/%s", wikiId, clickedPageId, beaconId, pageViewId));
        return Response.ok().entity("ok").build();
    }
}
