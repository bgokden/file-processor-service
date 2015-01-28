package com.fileprocessorservice.tasks;

import com.fileprocessorservice.model.Document;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

/**
 * Created by berkgokden on 1/28/15.
 */
public class FileProcessExecutionCallback implements ExecutionCallback<Document>, HazelcastInstanceAware {
    private String key;
    private String filename;
    protected transient HazelcastInstance hazelcastInstance;
    public FileProcessExecutionCallback() {
    }
    public FileProcessExecutionCallback(HazelcastInstance hazelcastInstance,String filename, String key) {
        this.hazelcastInstance = hazelcastInstance;
        this.filename = filename;
        this.key = key;
    }
    @Override
    public void onResponse(Document response) {
        response.setFilename(this.filename);
        hazelcastInstance.getMap("documents").put(key, response);
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}