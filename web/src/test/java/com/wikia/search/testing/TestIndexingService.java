package com.wikia.search.testing;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.List;

public class TestIndexingService {
    private final SolrServer solrServer;

    public TestIndexingService(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public void addDocument(int wikiId, int articleId, int ns, String title, String _abstract, String imageUrl, int views, int backlinks, List<String> redirects) throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id", String.format("%d_%d", wikiId, articleId));
        document.setField("wikiId_i", wikiId);
        document.setField("pageId_i", articleId);
        document.setField("namespace_i", ns);
        document.setField("abstract_en", _abstract);
        document.setField("thumbnail_url", imageUrl);
        document.setField("views_i", views);
        document.setField("backlinks_i", backlinks);

        document.setField("title_prefix_suffix", title);
        document.setField("title_simple", title);
        document.setField("title_ngram", title);

        document.setField("redirects_prefix_suffix_mv", redirects);
        document.setField("redirects_simple_mv", redirects);
        document.setField("redirects_ngram_mv", redirects);

        document.setField("testing_artifact_b", true);

        solrServer.add(document);
    }

    public void commit() throws IOException, SolrServerException {
        solrServer.commit(true, true);
    }
}
