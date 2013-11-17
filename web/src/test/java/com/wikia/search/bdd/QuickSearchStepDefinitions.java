package com.wikia.search.bdd;

import com.google.common.collect.Lists;
import com.wikia.search.testing.PageDocument;
import com.wikia.search.testing.PageDocumentBuilder;
import com.wikia.search.testing.TestIndexingService;
import com.wikia.search.testing.TestingSolrServerFactory;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.solr.client.solrj.SolrServerException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

public class QuickSearchStepDefinitions {
    private String endpointUrl;
    private Response response;
    private String responseString;
    private int wikiId = new Random().nextInt(1000) + 1000;


    @Given("^default endpoint$")
    public void defaultEndpoint() {
        endpointUrl = System.getProperty("service.url") + "api/search-suggest";
    }

    @Given("^Call of Duty dataset")
    public void callOfDutyDataset() throws IOException, SolrServerException {
        TestIndexingService indexingService = new TestingSolrServerFactory().getIndexingService();
        indexingService.clear();
        indexingService.addDocument(wikiId, 1, 0, "John Price", "...", "http://img.com/img/img.jpg", 100, 100, Lists.newArrayList("John", "Price"));
        indexingService.addDocument(wikiId, 2, 0, "Spider-Man", "...", "http://img.com/img/img.jpg", 100, 100, Lists.newArrayList("Spider"));
        indexingService.addDocument(wikiId, 6, 0, "John (\"Soap\") MacTavish", "Captain John \"Soap\" MacTavish was the Scottish[4] main protagonist", "http://img.com/jm.jpg", 500, 100, Lists.newArrayList("Soap", "MacTavish"));
        indexingService.addDocument(wikiId, 8, 0, "John McCain", "...", "http://img.com/jm.jpg", 10, 100, new ArrayList<String>());
        indexingService.addDocument(123, 13, 0, "John Price", "...", "http://img.com/img.jpg", 100000, 100, Lists.newArrayList("John", "Price"));
        indexingService.commit();
    }

    @Given("^following dataset")
    public void followingDataset(DataTable data) throws IOException, SolrServerException {
        TestIndexingService indexingService = new TestingSolrServerFactory().getIndexingService();
        indexingService.clear();

        for ( Map<String,String> row: data.asMaps()) {
            PageDocument document = PageDocumentBuilder.fromMap(row)
                    .setWikiId(wikiId)
                    .build();
            indexingService.addDocument(document);
        }

        indexingService.commit();
    }

    @Then("^I query for \"([^\"]+)\"$")
    public void iQueryFor(String phrase) throws Throwable {
        WebClient client = WebClient.create(endpointUrl + "?wikiId=" + wikiId + "&q=" + phrase);
        response = client.accept("application/json").get();
        responseString = IOUtils.toString((InputStream) response.getEntity());
    }

    @Then("^I want to get \"([^\"]+)\" as first result$")
    public void iWantToGetAsFirstResult(String title) throws IOException {
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        with(responseString).assertThat("$[0].title", equalTo(title));
    }

    @Then("^I want to get empty result set$")
    public void I_want_to_get_empty_result_set() throws Throwable {
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        with(responseString).assertThat("$", hasSize(0));
    }

    @Then("^I want to see exactly (\\d+) results?$")
    public void I_want_to_see_exactly_results(int results) throws Throwable {
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        with(responseString).assertThat("$", hasSize(results));
    }
}
