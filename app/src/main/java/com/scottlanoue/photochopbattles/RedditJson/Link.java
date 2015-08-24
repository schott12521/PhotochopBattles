package com.scottlanoue.photochopbattles.RedditJson;

import java.io.Serializable;

/**
 * Created by schot_000 on 8/8/2015.
 */

public class Link implements Serializable {

    private String title;
    private String url;
    private String permaLink;
    private String domain;
    private int score;

    public Link(String title, String url, String permaLink, int score, String domain) {
        this.title = title;
        this.url = url;
        this.permaLink = permaLink;
        this.score = score;
        this.domain = domain;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public int getScore() {
        return score;
    }

    public String getDomain() { return domain; }

    public String toString() {
        return title + " " + url + " " + permaLink + " " + score;
    }
}