package com.fileprocessorservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by berkgokden on 1/26/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document implements Serializable{
    private String filename;
    private String mimeType;
    private Map<String, Object> dataMap;

    public Document(){
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
