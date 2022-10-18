package com.chatbot.delivery.notification;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class FileResourcesUtils {

    public static void main(String[] args) throws IOException, URISyntaxException {

        FileResourcesUtils app = new FileResourcesUtils();

        //String fileName = "database.properties";
        String fileName = "message_template.json";

        System.out.println(app.readFile(fileName));

    }

    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.
    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    

    // print input stream
    public String readFile(String fileName) {
    	
        System.out.println("getResourceAsStream : " + fileName);
        InputStream is = getFileFromResourceAsStream(fileName);

    	StringBuilder fileContents = new StringBuilder();
        try (InputStreamReader streamReader =
                    new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
            	fileContents.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents.toString();
    }
}