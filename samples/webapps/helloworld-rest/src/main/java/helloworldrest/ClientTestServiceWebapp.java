/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package helloworldrest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Formatter;

/**
 *
 * To test, deploy the application as a webapp.
 * Then, run this file to access the REST web service by making HTTP GET/POST requests 
 * 
 */
public class ClientTestServiceWebapp {

    final static String UrlBase = "http://localhost:8080/helloworld-rest-webapp/HelloWorldService";

    final static class HttpResponse {
        Object content;
        int code;
        String message;
    }

    static HttpResponse makeHttpGetRequest(String method, String url, String contentType) throws Exception {
        HttpResponse response = new HttpResponse();
        URL urlAddress = new URL(url);
        HttpURLConnection huc = (HttpURLConnection)urlAddress.openConnection();
        huc.setRequestMethod(method);
        huc.setRequestProperty("Content-type", contentType);
        huc.connect();
        InputStreamReader isr = new InputStreamReader(huc.getInputStream());

        BufferedReader in = new BufferedReader(isr);
        String uline = in.readLine();
        response.content = uline;

        //        huc.disconnect();
        //        System.out.println("####  huc disconnected ###");

        return response;
    }

    static HttpResponse makeHttpRequest(String method, String url, String contentType, InputStream is) throws Exception {
        HttpResponse response = new HttpResponse();
        URL urlAddress = new URL(url);
        HttpURLConnection huc = (HttpURLConnection)urlAddress.openConnection();
        huc.setRequestMethod(method);
        if (null != is) {
            huc.setDoOutput(true);
            huc.setRequestProperty("Content-Type", contentType);
            OutputStream os = huc.getOutputStream();
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
        }
        InputStreamReader isr = new InputStreamReader(huc.getInputStream());
        BufferedReader in = new BufferedReader(isr);
        String uline = in.readLine();
        response.content = uline;
        return response;
    }

    static HttpResponse makeHttpGetRequest(String method, String url, String contentType, String content)
        throws Exception {
        return makeHttpRequest(method, url, contentType, new ByteArrayInputStream(content.getBytes("UTF-8")));
    }

    static HttpResponse makeHttpRequest(String method, String url) throws Exception {
        return makeHttpRequest(method, url, null, (InputStream)null);
    }

    public static void main(String[] args) {
        try {

            HttpResponse response;

            System.out.println("Getting the name *BEFORE* setting it:");
            response = makeHttpGetRequest("GET", UrlBase + "/helloworld/getname", "text/plain");
            System.out.println(new Formatter().format("---- Received String:\n%s", response.content.toString()));

            System.out.println("Setting the name:");
            String newText = "Morpheus";
            InputStream inputStream = new ByteArrayInputStream(newText.getBytes());
            response = makeHttpRequest("PUT", UrlBase + "/helloworld/setname", "text/plain", inputStream);

            System.out.println("Getting the name *AFTER* setting it:");
            response = makeHttpGetRequest("GET", UrlBase + "/helloworld/getname", "text/plain");
            System.out.println(new Formatter().format("---- Received String:\n%s", response.content.toString()));

            System.out.println("POST Operation:");
            response = makeHttpGetRequest("POST", UrlBase + "/helloworld/postoperation/prateek", "text/plain");
            //System.out.println(new Formatter().format("---- Received String:\n%s", response.content.toString()));

            System.out.println("Getting the name *AFTER* the POST operation:");
            response = makeHttpGetRequest("GET", UrlBase + "/helloworld/getname", "text/plain");
            System.out.println(new Formatter().format("---- Received String:\n%s", response.content.toString()));
        } catch (Exception e) {
            System.out.println("TEST FAILED! :-(");
            e.printStackTrace(System.out);
        }
    }
}
