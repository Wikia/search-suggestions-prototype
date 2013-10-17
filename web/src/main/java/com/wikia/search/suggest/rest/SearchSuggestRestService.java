package com.wikia.search.suggest.rest;

import com.wikia.search.suggest.model.Suggestion;
import com.wikia.search.suggest.service.SearchException;
import com.wikia.search.suggest.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("search-suggest")
public class SearchSuggestRestService {
    private static Logger logger = LoggerFactory.getLogger(SearchSuggestRestService.class);

    @Autowired(required = true)
    @Qualifier("suffixSearchSuggestService")
    private SearchService searchSuggestService;

    @GET
    @Path("/")
    @Produces("application/json")
    public Response suggest(@QueryParam("wikiId") Integer wikiId,
                            @QueryParam("q") String queryString,
                            @QueryParam("bid") String beaconId,
                            @QueryParam("pvid") String pageViewId ) {
        if ( wikiId == null ) {
            return Response.serverError().entity("You need to specify wikiId.").build();
        }
        if ( queryString == null ) {
            return Response.ok().entity(new ArrayList()).build();
        }

        try {
            List<Suggestion> suggestions = searchSuggestService.search(wikiId, queryString);
            return Response.ok().entity(suggestions).build();
        } catch (SearchException e) {
            logger.error("Error while trying to get suggestions.");
            return Response.serverError().entity("Server Error. Try again later.").build();
        }
    }
}
