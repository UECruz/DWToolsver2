package com.java1.fullsail.dwtools.models;

import java.io.Serializable;

public class BasicModel implements Serializable{

    private String result;

    public BasicModel(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
