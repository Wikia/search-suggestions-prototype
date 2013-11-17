package com.wikia.search.testing;

import java.util.List;

public class PageDocument {
    String title;
    String _abstract;
    String imageUrl;
    int wikiId;
    int articleId;
    int ns;
    int views;
    int backlinks;
    List<String> redirects;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWikiId() {
        return wikiId;
    }

    public void setWikiId(int wikiId) {
        this.wikiId = wikiId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getNs() {
        return ns;
    }

    public void setNs(int ns) {
        this.ns = ns;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getBacklinks() {
        return backlinks;
    }

    public void setBacklinks(int backlinks) {
        this.backlinks = backlinks;
    }

    public List<String> getRedirects() {
        return redirects;
    }

    public void setRedirects(List<String> redirects) {
        this.redirects = redirects;
    }
}
