package com.talend.components.source.details;

import java.io.Serializable;

import java.util.Collection;
import java.util.Set;

// this is the pojo which will be used to represent your data
public class ComponentDetailsREJECTOutput implements Serializable {

    private String id;
    private String message;
    private int httpError;

    public String getId() {
        return id;
    }

    public ComponentDetailsREJECTOutput setId(String id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ComponentDetailsREJECTOutput setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getHttpError() {
        return httpError;
    }

    public ComponentDetailsREJECTOutput setHttpError(int httpError) {
        this.httpError = httpError;
        return this;
    }
}