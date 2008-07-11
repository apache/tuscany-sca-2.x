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
package org.apache.tuscany.sca.binding.gdata.provider;

import static org.apache.tuscany.sca.binding.gdata.provider.GdataBindingUtil.entry;
import static org.apache.tuscany.sca.binding.gdata.provider.GdataBindingUtil.feedEntry;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.tuscany.sca.binding.gdata.collection.NotFoundException;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.ParseSource;
import com.google.gdata.data.TextContent;

/**
 * Invoker for the GData binding.
 * 
 */
class GdataBindingInvoker implements Invoker, DataExchangeSemantics {

    private static final Factory abderaFactory = Abdera.getNewFactory();
    private static final Parser abderaParser = Abdera.getNewParser();

    Operation operation;
    String uri;
    HttpClient httpClient;
    String authorizationHeader;
    GdataReferenceBindingProvider provider;

    private static final com.google.gdata.client.GoogleService googleService =
        new com.google.gdata.client.GoogleService("cl", "exampleCo-exampleApp-1");

    GdataBindingInvoker(Operation operation,
                        String uri,
                        HttpClient httpClient,
                        String authorizationHeader,
                        GdataReferenceBindingProvider bindingProvider) {
        this.operation = operation;
        this.uri = uri;
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;
        this.provider = bindingProvider;
        // this.googleService = new com.google.gdata.client.GoogleService("cl",
        // "exampleCo-exampleApp-1");

        // //System.out.println("GdataBindingInvoker constuctor reached!: " +
        // operation);
    }

    public Message invoke(Message msg) {
        // Shouldn't get here, as the only supported methods are
        // defined in the ResourceCollection interface, and implemented
        // by specific invoker subclasses
        throw new UnsupportedOperationException(operation.getName());
    }

    /**
     * Get operation invoker
     */
    public static class GetInvoker extends GdataBindingInvoker {
        public GetInvoker(Operation operation,
                          String uri,
                          HttpClient httpClient,
                          String authorizationHeader,
                          GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {

            System.out.println("GdataBindingInvoker.GetInvoker.invoke is reached!");

            // Get an entry
            String id = (String)((Object[])msg.getBody())[0];

            System.out.println("GdataBindingInvoker.GetInvoker.invoke---id: " + id);

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri + "/" + id);
            getMethod.setRequestHeader("Authorization", authorizationHeader);

            try {
                System.out.println("GdataBindingInvoker.GetInvoker.invoke---feedURL: " + getMethod.getURI().toString());
            } catch (URIException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            boolean parsing = false;
            try {
                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                System.out.println("GdataBindingInvoker.GetInvoker.invoke---I am good here00");
                System.out.println("GdataBindingInvoker.GetInvoker.invoke---status: " + status);

                // Read the Atom entry
                if (status == 200) {

                    URL feedURL = new URL(getMethod.getURI().toString());

                    System.out.println("GdataBindingInvoker.GetInvoker.invoke---feedURL: " + feedURL);

                    com.google.gdata.data.Entry entry =
                        googleService.getEntry(feedURL, com.google.gdata.data.Entry.class);

                    System.out.println("GetInvoker class:   I am good here 04");

                    System.out.println("entry title: " + entry.getTitle().getPlainText());

                    System.out.println("GdataBindingInvoker.GetInvoker.invoke---entry");

                    // Document<Feed> doc = abderaParser.parse(new
                    // InputStreamReader(getMethod.getResponseBodyAsStream()));
                    // parsing = true;
                    // Feed feed = doc.getRoot();

                    // System.out.println("getMethod.getResponseBodyAsString()"
                    // + getMethod.getResponseBodyAsString());

                    // feed = (com.google.gdata.data.Feed)
                    // responseMessage.getBody();

                    System.out.println("provider.supportsFeedEntries()" + provider.supportsFeedEntries());

                    if (provider.supportsFeedEntries()) {

                        // Return the Atom entry
                        msg.setBody(entry);

                    } else {

                        // Convert the feed entry to a data entry and return the
                        // data item
                        // Entry<Object, Object> entry = entry(feedEntry,
                        // provider.getItemClassType(),
                        // provider.getItemXMLType(), provider.getMediator());
                        // msg.setBody(entry.getData());
                    }

                    // To-change
                    // This is read the entries from the response body
                    // Change it into the corresponding Gdata parse and populate
                    // the message with entries.

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                if (!parsing) {
                    // Release the connection unless the Abdera parser is
                    // parsing the response, in this case it will release it
                    getMethod.releaseConnection();
                }
            }

            return msg;
        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends GdataBindingInvoker {

        public PostInvoker(Operation operation,
                           String uri,
                           HttpClient httpClient,
                           String authorizationHeader,
                           GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);

            System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- Constructor method reached");
        }

        @Override
        public Message invoke(Message msg) {

            System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- invoke method reached");
            // Post an entry
            Object[] args = (Object[])msg.getBody();
            com.google.gdata.data.Entry feedEntry = null;

            if (provider.supportsFeedEntries()) {

                // Expect an Atom entry

                // To-change
                // Expect a Gdata entry
                // change it into com.google.gdata.data.Entry

                System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- supportsFeedEntries: " + provider
                    .supportsFeedEntries());
                feedEntry = (com.google.gdata.data.Entry)args[0];

                System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- feedEntry title: " + feedEntry
                    .getTitle().getPlainText());

            } else {
                // Expect a key and data item
                Entry<Object, Object> entry = new Entry<Object, Object>(args[0], args[1]);
                // FIXME: this needs to be fixed
                // feedEntry = feedEntry(entry, provider.getItemClassType(),
                // provider.getItemXMLType(), provider.getMediator(),
                // abderaFactory);
            }

            // Send an HTTP POST
            PostMethod postMethod = new PostMethod(uri);

            System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- uri: " + uri);

            postMethod.setRequestHeader("Authorization", authorizationHeader);
            boolean parsing = false;
            try {
                // Write the Atom entry
                // StringWriter writer = new StringWriter();
                // feedEntry.writeTo(writer);

                StringWriter writer = new StringWriter();
                com.google.gdata.util.common.xml.XmlWriter w = new com.google.gdata.util.common.xml.XmlWriter(writer);
                feedEntry.generateAtom(w, new ExtensionProfile());
                w.flush();

                postMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                postMethod.setRequestEntity(new StringRequestEntity(writer.toString()));

                httpClient.executeMethod(postMethod);
                int status = postMethod.getStatusCode();

                System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- status code: " + status);

                // Read the Atom entry
                if (status == 200 || status == 201) {

                    /*
                     * Document<org.apache.abdera.model.Entry> doc =
                     * abderaParser.parse(new
                     * InputStreamReader(postMethod.getResponseBodyAsStream()));
                     * parsing = true; org.apache.abdera.model.Entry
                     * createdEntry = doc.getRoot();
                     */

                    ParseSource source = new ParseSource(new InputStreamReader(postMethod.getResponseBodyAsStream()));
                    com.google.gdata.data.Entry createdEntry =
                        com.google.gdata.data.Entry.readEntry(source, com.google.gdata.data.Entry.class, null);

                    System.out.println("parsed createdentry title: " + createdEntry.getTitle().getPlainText());
                    System.out.println("parsed createdentry content: " + ((TextContent)createdEntry.getContent())
                        .getContent().getPlainText());
                    System.out.println("parsed createdentry updated: " + createdEntry.getUpdated().toString());

                    /*
                     * Document<org.apache.abdera.model.Entry> doc =
                     * abderaParser.parse(new
                     * InputStreamReader(postMethod.getResponseBodyAsStream()));
                     * parsing = true; org.apache.abdera.model.Entry
                     * createdEntry = doc.getRoot();
                     */

                    // Returns the created Atom entry ID
                    if (provider.supportsFeedEntries()) {
                        // Returns the created entry
                        msg.setBody(createdEntry);
                    } else {
                        // Returns the id of the created entry
                        msg.setBody(createdEntry.getId().toString());
                    }

                    // To-change
                    // Get the status back and parse the updated entry
                    // Need to change it into the corresponding Gdata Entry
                    // class

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                if (!parsing) {
                    // Release the connection unless the Abdera parser is
                    // parsing the response, in this case it will release it
                    postMethod.releaseConnection();
                }
            }

            return msg;
        }
    }

    // To-change
    // For the following classes: PutInvoker, DeleteInvoker, GetAllInvoker,
    // PostMediaInvoker and PutMediaInvoker
    // We just need to modify the corresponding parser and entry methods for
    // Gdata
    // The key point is the data conversion from item and gdata entry
    // and parsing and writing entries in message
    //

    /**
     * Put operation invoker
     */
    public static class PutInvoker extends GdataBindingInvoker {

        public PutInvoker(Operation operation,
                          String uri,
                          HttpClient httpClient,
                          String authorizationHeader,
                          GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {

            // Put an entry
            Object[] args = (Object[])msg.getBody();
            String id;
            org.apache.abdera.model.Entry feedEntry;
            if (provider.supportsFeedEntries()) {

                // Expect a key and Atom entry
                id = (String)args[0];
                feedEntry = (org.apache.abdera.model.Entry)args[1];
            } else {

                // Expect a key and data item
                id = (String)args[0];
                Entry<Object, Object> entry = new Entry<Object, Object>(id, args[1]);
                feedEntry =
                    feedEntry(entry,
                              provider.getItemClassType(),
                              provider.getItemXMLType(),
                              provider.getMediator(),
                              abderaFactory);
            }

            // Send an HTTP PUT
            PutMethod putMethod = new PutMethod(uri + "/" + id);
            putMethod.setRequestHeader("Authorization", authorizationHeader);
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                feedEntry.writeTo(writer);
                putMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                putMethod.setRequestEntity(new StringRequestEntity(writer.toString()));

                httpClient.executeMethod(putMethod);
                int status = putMethod.getStatusCode();
                if (status == 200 || status == 201) {

                    msg.setBody(null);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                putMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends GdataBindingInvoker {

        public DeleteInvoker(Operation operation,
                             String uri,
                             HttpClient httpClient,
                             String authorizationHeader,
                             GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {

            // Delete an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP DELETE
            DeleteMethod deleteMethod = new DeleteMethod(uri + "/" + id);
            deleteMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                httpClient.executeMethod(deleteMethod);
                int status = deleteMethod.getStatusCode();
                if (status == 200) {
                    msg.setBody(null);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                deleteMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends GdataBindingInvoker {

        public GetAllInvoker(Operation operation,
                             String uri,
                             HttpClient httpClient,
                             String authorizationHeader,
                             GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {

            // Get a feed
            System.out.println("GetAllInvoker class:   I am good here 00");

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri);
            getMethod.setRequestHeader("Authorization", authorizationHeader);

            System.out.println("GetAllInvoker class:   I am good here 01");

            try {
                System.out.println("GdataBindingInvoker.GetAllInvoker.invoke---feedURL: " + getMethod.getURI()
                    .toString());
            } catch (URIException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            boolean parsing = false;
            try {

                System.out.println("GetAllInvoker class:   I am good here 02");

                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                System.out.println("GetAllInvoker class:   I am good here 03");

                // Read the Atom feed
                if (status == 200) {

                    URL feedURL = new URL(getMethod.getURI().toString());

                    System.out.println("GdataBindingInvoker.GetInvoker.invoke---feedURL: " + feedURL);

                    /*
                     * //Changed by Haibo System.out.println("feedURL.toString: " +
                     * feedURL.toString()); InputStreamReader stringReader= new
                     * InputStreamReader(getMethod.getResponseBodyAsStream());
                     * StringBuffer buffer = new StringBuffer(); Reader in = new
                     * BufferedReader(stringReader); XhtmlTextConstruct
                     * construct = new XhtmlTextConstruct();
                     * com.google.gdata.data.XhtmlTextConstruct.AtomHandler
                     * rootHandler = construct.new AtomHandler(); XmlParser
                     * parser = new XmlParser();
                     * parser.parse(in,rootHandler,"http://www.w3.org/2005/Atom","feed");
                     * System.out.println(construct.getXhtml().getBlob());
                     */

                    com.google.gdata.data.Feed feed = googleService.getFeed(feedURL, com.google.gdata.data.Feed.class);

                    System.out.println("GetAllInvoker class:   I am good here 04");

                    System.out.println("feed title: " + feed.getTitle().getPlainText());

                    // Document<Feed> doc = abderaParser.parse(new
                    // InputStreamReader(getMethod.getResponseBodyAsStream()));
                    // parsing = true;
                    // Feed feed = doc.getRoot();

                    // System.out.println("getMethod.getResponseBodyAsString()"
                    // + getMethod.getResponseBodyAsString());

                    // feed = (com.google.gdata.data.Feed)
                    // responseMessage.getBody();

                    System.out.println("provider.supportsFeedEntries()" + provider.supportsFeedEntries());

                    if (provider.supportsFeedEntries()) {

                        System.out.println("GetAllInvoker class:   I am good here 05");

                        // Returns the Atom feed
                        msg.setBody(feed);

                        System.out.println("msg: " + msg.toString());

                    } else {

                        /*
                         * // Returns an array of data entries List<Entry<Object,
                         * Object>> entries = new ArrayList<Entry<Object,Object>>();
                         * for (org.apache.abdera.model.Entry feedEntry:
                         * feed.getEntries()) { Entry<Object, Object> entry =
                         * entry(feedEntry, provider.getItemClassType(),
                         * provider.getItemXMLType(), provider.getMediator());
                         * entries.add(entry); } msg.setBody(entries.toArray(new
                         * Entry[entries.size()]));
                         */
                    }

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                if (!parsing) {
                    // Release the connection unless the Abdera parser is
                    // parsing the response, in this case it will release it
                    getMethod.releaseConnection();
                }
            }

            return msg;
        }
    }

    /**
     * Query operation invoker
     */
    public static class QueryInvoker extends GdataBindingInvoker {

        public QueryInvoker(Operation operation,
                            String uri,
                            HttpClient httpClient,
                            String authorizationHeader,
                            GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {

            // Get a feed from a query
            String queryString = (String)((Object[])msg.getBody())[0];

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri);
            getMethod.setRequestHeader("Authorization", authorizationHeader);
            getMethod.setQueryString(queryString);
            boolean parsing = false;
            try {
                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                // Read the Atom feed
                if (status == 200) {
                    Document<Feed> doc = abderaParser.parse(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                    parsing = true;
                    Feed feed = doc.getRoot();

                    if (provider.supportsFeedEntries()) {

                        // Returns the Atom feed
                        msg.setBody(feed);

                    } else {

                        // Returns an array of data entries
                        List<Entry<Object, Object>> entries = new ArrayList<Entry<Object, Object>>();
                        for (org.apache.abdera.model.Entry feedEntry : feed.getEntries()) {
                            Entry<Object, Object> entry =
                                entry(feedEntry, provider.getItemClassType(), provider.getItemXMLType(), provider
                                    .getMediator());
                            entries.add(entry);
                        }
                        msg.setBody(entries.toArray(new Entry[entries.size()]));
                    }

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                if (!parsing) {
                    // Release the connection unless the Abdera parser is
                    // parsing the response, in this case it will release it
                    getMethod.releaseConnection();
                }
            }

            return msg;
        }
    }

    /**
     * PostMedia operation invoker
     */
    public static class PostMediaInvoker extends GdataBindingInvoker {

        public PostMediaInvoker(Operation operation,
                                String uri,
                                HttpClient httpClient,
                                String authorizationHeader,
                                GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * PutMedia operation invoker
     */
    public static class PutMediaInvoker extends GdataBindingInvoker {

        public PutMediaInvoker(Operation operation,
                               String uri,
                               HttpClient httpClient,
                               String authorizationHeader,
                               GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    public boolean allowsPassByReference() {
        // TODO Auto-generated method stub
        return true;
    }

}
