package com.wikia.search.bdd;

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
