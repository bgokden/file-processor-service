package com.fileprocessorservice;

import com.fileprocessorservice.model.Document;
import com.fileprocessorservice.tasks.FileProcessExecutionCallback;
import com.fileprocessorservice.tasks.FileProcessTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.ContentHandler;

import java.io.*;
import java.util.UUID;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import com.sun.jersey.core.header.FormDataContentDisposition;
//import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class FileService {

    private @Autowired
    HazelcastInstance hazelcastInstance;

    @GET
    @Path("/{uuid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    public Document getFile(@PathParam("uuid") String uuid ) {
        Document document = null;
        document = (Document) hazelcastInstance.getMap("documents").get(uuid);
        if (document == null) {
            return null;
        }
        return document;
    }

    @GET
    @Path("/")
    public Response getFormPage() {
        String output = "<html>\n" +
                "<body>\n" +
                "\t<h1>Upload A Pdf File</h1>\n" +
                " \n" +
                "\t<form action=\"/file\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                " \n" +
                "\t   <p>\n" +
                "\t\tSelect a file : <input type=\"file\" name=\"file\" size=\"45\" />\n" +
                "\t   </p>\n" +
                " \n" +
                "\t   <input type=\"submit\" value=\"Upload It\" />\n" +
                "\t</form>\n" +
                " \n" +
                "</body>\n" +
                "</html>";

        return Response.status(200).entity(output).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        Document document = new Document();

        IExecutorService executorService = hazelcastInstance.getExecutorService("default");

        // save it
        byte[] fileContent = writeToFile(uploadedInputStream);

        String key = UUID.randomUUID().toString();
        executorService.submit(new FileProcessTask(fileContent)
                , new FileProcessExecutionCallback(hazelcastInstance, fileDetail.getFileName(), key)
        );

        //hazelcastInstance.getMap("documents").put(document.getFilename(), document);

        String output = "File uploaded with key: "+key;

        return Response.status(200).entity(output).build();

    }

    // save uploaded file to new location
    private byte[] writeToFile(InputStream uploadedInputStream) {
        byte[] result = null;
        try {
            ByteArrayOutputStream out = out = new ByteArrayOutputStream();
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            result = out.toByteArray();
            out.flush();
            out.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return result;
    }

}