package com.fileprocessorservice.tasks;

import com.fileprocessorservice.model.Document;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.pdfparser.PdfParserService;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by berkgokden on 1/27/15.
 */
public class FileProcessTask implements Callable<Document>, Serializable {

    byte[] content;
    public FileProcessTask() {
    }

    public FileProcessTask(byte[] content) {
        this.content = content;
    }


    @Override
    public Document call() throws Exception {
        Map<String, Object> dataMap = PdfParserService.parse(this.content);
        Document document = new Document();
        document.setDataMap(dataMap);
        return document;
    }


}
