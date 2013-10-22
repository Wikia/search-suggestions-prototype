package com.wikia.search.testing;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public class TransientSolrFactory {
    private static Logger logger = LoggerFactory.getLogger(TransientSolrFactory.class);

    public SolrServer get() throws IOException, SolrServerException {
        Path tmpDir = buildTmpHome();
        CoreContainer coreContainer = new CoreContainer( tmpDir.toAbsolutePath().toString() );
        coreContainer.load();
        EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "suggest");
        server.ping();
        return server;
    }


    private Path buildTmpHome() throws IOException {
        Path tmpDir = getTmpDir();
        logger.info("Building tmp solr home in:" + tmpDir.toAbsolutePath().toString());
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] rootResource = resourcePatternResolver.getResources("classpath:solr-home/");
        Resource[] resources = resourcePatternResolver.getResources("classpath:solr-home/**");

        for( Resource resource: rootResource ) {
            FileUtils.copyDirectory( resource.getFile(), tmpDir.toFile() );
        }
        return tmpDir;
    }

    private Path getTmpDir() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if ( tmpDir == null ) {
            tmpDir = "/tmp/";
        }
        Path solrTmpDir = Files.createDirectories(Paths.get(tmpDir, "solr_" + new Random().nextInt(1000000)));
        return solrTmpDir;
    }
}
