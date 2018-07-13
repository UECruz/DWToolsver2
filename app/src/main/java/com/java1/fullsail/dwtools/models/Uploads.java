package com.java1.fullsail.dwtools.models;

public class Uploads {
    public String name;
    public String url;


    public Uploads() {
    }

    public Uploads(String url){
        this.url = url;
    }

    public Uploads(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
