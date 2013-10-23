package com.wikia.search.suggest.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;

public class Suggestion {

    @Field("pageId_i")
    private Integer id;
    @Field("title_ngram")
    private String title;
    @Field("pageUrl_url")
    private String path;
    @Field("redirects_ngram_mv")
    private List<String> redirects = new ArrayList<>();
    @Field("thumbnail_url")
    private String thumbnail;
    @Field("abstract_en") // TODO make it language agnostic
    private String summary;

    public long getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getRedirects() {
        return redirects;
    }

    public void setRedirects(List<String> redirects) {
        this.redirects = redirects;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
