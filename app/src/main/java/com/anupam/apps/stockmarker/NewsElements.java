package com.anupam.apps.stockmarker;

/**
 * Created by anupamish on 11/21/17.
 */

public class NewsElements {
    private String title, author, link, pubdate;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getLink() {
        return link;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public NewsElements(String title, String author, String link, String pubdate) {
        this.title = title;
        this.author = author;
        this.link = link;
        this.pubdate = pubdate;
    }

    @Override
    public String toString() {
        return "NewsElements{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", link='" + link + '\'' +
                ", pubdate='" + pubdate + '\'' +
                '}';
    }
}
