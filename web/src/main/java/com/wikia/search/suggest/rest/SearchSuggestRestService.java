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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("wiki")
public class SearchSuggestRestService {
    private static Logger logger = LoggerFactory.getLogger(SearchSuggestRestService.class);

    @Autowired(required = true)
    @Qualifier("searchSuggestService")
    private SearchService searchSuggestService;

    @GET
    @Path("{wikiId}/suggest/{input}")
    @Produces("application/json")
    public Response suggest(@PathParam("wikiId") int wikiId,@PathParam("input") String input) {
        try {
            List<Suggestion> suggestions = searchSuggestService.search(wikiId, input);
            return Response.ok().entity(suggestions).build();
        } catch (SearchException e) {
            logger.error("Error while trying to get suggestions.");
            return Response.serverError().entity("Server Error. Try again later.").build();
        }
    }
}
