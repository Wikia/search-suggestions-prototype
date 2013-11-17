package com.wikia.search.testing;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class PageDocumentBuilder {
    private PageDocument pageDocument = new PageDocument();
    private boolean sealed = false;

    public static PageDocumentBuilder start() {
        return new PageDocumentBuilder();
    }

    public static PageDocumentBuilder fromMap( Map<String, String> map ) {
        PageDocumentBuilder builder = start();

        for ( Map.Entry<String,String> entry: map.entrySet() ) {
            if (entry.getKey().equals("title")) {
                builder.setTitle(entry.getValue());
            } else if (entry.getKey().equals("abstract")) {
                builder.setAbstract(entry.getValue());
            } else if (entry.getKey().equals("articleId")) {
                builder.setArticleId(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("wikiId")) {
                builder.setWikiId(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("imageUrl")) {
                builder.setImageUrl(entry.getValue());
            } else if (entry.getKey().equals("ns")) {
                builder.setNs(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("views")) {
                builder.setViews(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("backlinks")) {
                builder.setBacklinks(Integer.parseInt(entry.getValue()));
            } else if (entry.getKey().equals("redirects")) {
                builder.setRedirects(Lists.newArrayList(entry.getValue().split(",")));
            } else {
                throw new IllegalArgumentException("Map contains illegal key: " + entry.getKey());
            }
        }
        return builder;
    }

    public PageDocument build() {
        sealed = true;
        return pageDocument;
    }

    public PageDocumentBuilder setTitle(String title) {
        throwIfCantModify();
        pageDocument.setTitle(title);
        return this;
    }

    public PageDocumentBuilder setAbstract(String _abstract) {
        throwIfCantModify();
        pageDocument.setAbstract(_abstract);
        return this;
    }

    public PageDocumentBuilder setImageUrl(String imageUrl) {
        throwIfCantModify();
        pageDocument.setImageUrl(imageUrl);
        return this;
    }

    public PageDocumentBuilder setWikiId(int wikiId) {
        pageDocument.setWikiId(wikiId);
        return this;
    }

    public PageDocumentBuilder setArticleId(int articleId) {
        throwIfCantModify();
        pageDocument.setArticleId(articleId);
        return this;
    }

    public PageDocumentBuilder setNs(int ns) {
        throwIfCantModify();
        pageDocument.setNs(ns);
        return this;
    }

    public PageDocumentBuilder setViews(int views) {
        throwIfCantModify();
        pageDocument.setViews(views);
        return this;
    }

    public PageDocumentBuilder setBacklinks(int backlinks) {
        throwIfCantModify();
        pageDocument.setBacklinks(backlinks);
        return this;
    }

    public PageDocumentBuilder setRedirects(List<String> redirects) {
        throwIfCantModify();
        pageDocument.setRedirects(redirects);
        return this;
    }

    private void throwIfCantModify() {
        if ( sealed ) {
            throw new IllegalStateException(
                    "Trying modify state of PageDocumentBuilder but object is already sealed. " +
                    "Cannot call setter after build method." );
        }
    }
}
