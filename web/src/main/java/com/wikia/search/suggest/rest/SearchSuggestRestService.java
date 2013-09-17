package com.wikia.search.suggest.rest;
/**
 * Author: Artur Dwornik
 * Date: 10.09.13
 * Time: 23:48
 */

import com.wikia.search.suggest.service.SearchSuggestService;
import com.wikia.search.suggest.model.Suggestion;
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
    private SearchSuggestService searchSuggestService;

    @GET
    @Path("{wikiId}/suggest/{input}")
    @Produces("application/json")
    public Response suggest(@PathParam("wikiId") int wikiId,@PathParam("input") String input) {
        List<Suggestion> suggestions = searchSuggestService.search(wikiId, input);
        return Response.ok().entity(suggestions).build();
    }
}
