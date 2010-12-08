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
package org.apache.tuscany.sca.binding.atom.provider;

import static org.apache.tuscany.sca.binding.atom.provider.AtomBindingUtil.entry;
import static org.apache.tuscany.sca.binding.atom.provider.AtomBindingUtil.feedEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Invoker for the Atom binding.
 * 
 * @version $Rev$ $Date$
 */
class AtomBindingInvoker implements Invoker {

    private static final Factory abderaFactory = Abdera.getNewFactory();
    private static final Parser abderaParser = Abdera.getNewParser();

    Operation operation;
    String uri;
    HttpClient httpClient;
    String authorizationHeader;
    AtomReferenceBindingProvider provider;

    AtomBindingInvoker(Operation operation,
                       String uri,
                       org.apache.http.client.HttpClient httpClient,
                       String authorizationHeader,
                       AtomReferenceBindingProvider bindingProvider) {
        this.operation = operation;
        this.uri = uri;
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;
        this.provider = bindingProvider;
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
    public static class GetInvoker extends AtomBindingInvoker {

        public GetInvoker(Operation operation,
                          String uri,
                          org.apache.http.client.HttpClient httpClient,
                          String authorizationHeader,
                          AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // Get an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP GET
            HttpGet getMethod = new HttpGet(uri + "/" + id);
            if (authorizationHeader != null) {
                getMethod.setHeader("Authorization", authorizationHeader);
            }
            boolean parsing = false;
            HttpResponse response = null;
            try {
                response = httpClient.execute(getMethod);
                int status = response.getStatusLine().getStatusCode();

                // Read the Atom entry
                if (status == 200) {
                    InputStream content = response.getEntity().getContent();
                    Document<org.apache.abdera.model.Entry> doc = abderaParser.parse(content);
                    content.close();
                    parsing = true;
                    org.apache.abdera.model.Entry feedEntry = doc.getRoot();

                    if (provider.supportsFeedEntries()) {

                        // Return the Atom entry
                        msg.setBody(feedEntry);

                    } else {

                        // Convert the feed entry to a data entry and return the data item
                        Entry<Object, Object> entry =
                            entry(feedEntry,
                                  provider.getItemClassType(),
                                  provider.getItemXMLType(),
                                  provider.getMediator());
                        msg.setBody(entry.getData());
                    }

                } else if (status == 404) {
                    if (provider.supportsFeedEntries())
                        msg.setFaultBody(new NotFoundException());
                    else
                        msg.setFaultBody(new org.apache.tuscany.sca.data.collection.NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {

                // Release the connection unless the Abdera parser is
                // parsing the response, in this case it will release it
                release(getMethod, response);

            }

            return msg;
        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends AtomBindingInvoker {

        public PostInvoker(Operation operation,
                           String uri,
                           HttpClient httpClient,
                           String authorizationHeader,
                           AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // Post an entry
            Object[] args = (Object[])msg.getBody();
            org.apache.abdera.model.Entry feedEntry;
            if (provider.supportsFeedEntries()) {

                // Expect an Atom entry
                feedEntry = (org.apache.abdera.model.Entry)args[0];
            } else {

                // Expect a key and data item
                Entry<Object, Object> entry = new Entry<Object, Object>(args[0], args[1]);
                feedEntry =
                    feedEntry(entry,
                              provider.getItemClassType(),
                              provider.getItemXMLType(),
                              provider.getMediator(),
                              abderaFactory);
            }

            // Send an HTTP POST
            HttpPost postMethod = new HttpPost(uri);
            if (authorizationHeader != null) {
                postMethod.setHeader("Authorization", authorizationHeader);
            }
            boolean parsing = false;
            HttpResponse response = null;
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                feedEntry.writeTo(writer);
                // postMethod.setHeader("Content-type", "application/atom+xml; charset=utf-8");
                postMethod.setHeader("Content-type", "application/atom+xml;type=entry");
                postMethod.setEntity(new StringEntity(writer.toString()));

                response = httpClient.execute(postMethod);
                int status = response.getStatusLine().getStatusCode();

                // Read the Atom entry
                if (status == 200 || status == 201) {
                    InputStream content = postMethod.getEntity().getContent();
                    Document<org.apache.abdera.model.Entry> doc = abderaParser.parse(content);
                    content.close();
                    parsing = true;
                    org.apache.abdera.model.Entry createdEntry = doc.getRoot();

                    // Returns the created Atom entry ID
                    if (provider.supportsFeedEntries()) {

                        // Returns the created entry
                        msg.setBody(createdEntry);

                    } else {

                        // Returns the id of the created entry 
                        msg.setBody(createdEntry.getId().toString());
                    }

                } else if (status == 404) {
                    if (provider.supportsFeedEntries())
                        msg.setFaultBody(new NotFoundException());
                    else
                        msg.setFaultBody(new org.apache.tuscany.sca.data.collection.NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {

                // Release the connection unless the Abdera parser is
                // parsing the response, in this case it will release it
                release(postMethod, response);

            }

            return msg;
        }
    }

    /**
     * Put operation invoker
     */
    public static class PutInvoker extends AtomBindingInvoker {

        public PutInvoker(Operation operation,
                          String uri,
                          HttpClient httpClient,
                          String authorizationHeader,
                          AtomReferenceBindingProvider bindingProvider) {
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
            HttpPut putMethod = new HttpPut(uri + "/" + id);
            if (authorizationHeader != null) {
                putMethod.setHeader("Authorization", authorizationHeader);
            }

            HttpResponse response = null;
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                feedEntry.writeTo(writer);
                putMethod.setHeader("Content-type", "application/atom+xml; charset=utf-8");
                putMethod.setEntity(new StringEntity(writer.toString()));

                response = httpClient.execute(putMethod);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 201 || status == 412) {

                    msg.setBody(null);

                } else if (status == 404) {
                    if (provider.supportsFeedEntries())
                        msg.setFaultBody(new NotFoundException());
                    else
                        msg.setFaultBody(new org.apache.tuscany.sca.data.collection.NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                release(putMethod, response);
            }

            return msg;
        }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends AtomBindingInvoker {

        public DeleteInvoker(Operation operation,
                             String uri,
                             HttpClient httpClient,
                             String authorizationHeader,
                             AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // Delete an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP DELETE
            HttpDelete deleteMethod = new HttpDelete(uri + "/" + id);
            if (authorizationHeader != null) {
                deleteMethod.setHeader("Authorization", authorizationHeader);
            }
            HttpResponse response = null;
            try {
                response = httpClient.execute(deleteMethod);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    msg.setBody(null);

                } else if (status == 404) {
                    if (provider.supportsFeedEntries())
                        msg.setFaultBody(new NotFoundException());
                    else
                        msg.setFaultBody(new org.apache.tuscany.sca.data.collection.NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                release(deleteMethod, response);
            }

            return msg;
        }
    }

    /**
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends AtomBindingInvoker {

        public GetAllInvoker(Operation operation,
                             String uri,
                             HttpClient httpClient,
                             String authorizationHeader,
                             AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // Get a feed

            // Send an HTTP GET
            HttpGet getMethod = new HttpGet(uri);
            if (authorizationHeader != null) {
                getMethod.setHeader("Authorization", authorizationHeader);
            }
            boolean parsing = false;
            HttpResponse response = null;
            try {
                response = httpClient.execute(getMethod);
                int status = response.getStatusLine().getStatusCode();
                // AtomBindingInvoker.printResponseHeader( getMethod );

                // Read the Atom feed
                if (status == 200) {
                    InputStream content = response.getEntity().getContent();
                    Document<Feed> doc = abderaParser.parse(content);
                    content.close();
                    parsing = true;

                    Feed feed = null;
                    try {
                        feed = doc.getRoot();
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid feed format :" + uri);
                    }

                    if (provider.supportsFeedEntries()) {

                        // Returns the Atom feed
                        msg.setBody(feed);

                    } else {

                        // Returns an array of data entries
                        List<Entry<Object, Object>> entries = new ArrayList<Entry<Object, Object>>();
                        for (org.apache.abdera.model.Entry feedEntry : feed.getEntries()) {
                            Entry<Object, Object> entry =
                                entry(feedEntry,
                                      provider.getItemClassType(),
                                      provider.getItemXMLType(),
                                      provider.getMediator());
                            entries.add(entry);
                        }
                        msg.setBody(entries.toArray(new Entry[entries.size()]));
                    }

                } else if (status == 404) {
                    if (provider.supportsFeedEntries()) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new org.apache.tuscany.sca.data.collection.NotFoundException());
                    }
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {

                // Release the connection unless the Abdera parser is
                // parsing the response, in this case it will release it
                release(getMethod, response);

            }

            return msg;
        }
    }

    /**
     * Query operation invoker
     */
    public static class QueryInvoker extends AtomBindingInvoker {

        public QueryInvoker(Operation operation,
                            String uri,
                            HttpClient httpClient,
                            String authorizationHeader,
                            AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // Get a feed from a query
            String queryString = (String)((Object[])msg.getBody())[0];

            // Send an HTTP GET
            HttpGet getMethod = new HttpGet(uri + "?" + queryString);
            if (authorizationHeader != null) {
                getMethod.setHeader("Authorization", authorizationHeader);
            }
            // getMethod.setQueryString(queryString);
            boolean parsing = false;
            HttpResponse response = null;
            try {
                response = httpClient.execute(getMethod);
                int status = response.getStatusLine().getStatusCode();

                // Read the Atom feed
                if (status == 200) {
                    InputStream content = response.getEntity().getContent();
                    Document<Feed> doc = abderaParser.parse(content);
                    content.close();
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
                                entry(feedEntry,
                                      provider.getItemClassType(),
                                      provider.getItemXMLType(),
                                      provider.getMediator());
                            entries.add(entry);
                        }
                        msg.setBody(entries.toArray(new Entry[entries.size()]));
                    }

                } else if (status == 404) {
                    if (provider.supportsFeedEntries())
                        msg.setFaultBody(new NotFoundException());
                    else
                        msg.setFaultBody(new org.apache.tuscany.sca.data.collection.NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {

                // Release the connection unless the Abdera parser is
                // parsing the response, in this case it will release it
                release(getMethod, response);

            }

            return msg;
        }
    }

    private static void release(HttpRequestBase request, HttpResponse response) {

        if (response != null) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                    if (request != null) {
                        request.abort();
                    }
                }
            }
        }
    }

    /**
     * PostMedia operation invoker
     */
    public static class PostMediaInvoker extends AtomBindingInvoker {

        public PostMediaInvoker(Operation operation,
                                String uri,
                                HttpClient httpClient,
                                String authorizationHeader,
                                AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // PostInvoker can detect media by content type (non-Feed, non-Entry)
            return super.invoke(msg);
        }
    }

    /**
     * PutMedia operation invoker
     */
    public static class PutMediaInvoker extends AtomBindingInvoker {

        public PutMediaInvoker(Operation operation,
                               String uri,
                               HttpClient httpClient,
                               String authorizationHeader,
                               AtomReferenceBindingProvider bindingProvider) {
            super(operation, uri, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {
            // PutInvoker can detect media by content type (non-Feed, non-Entry)
            return super.invoke(msg);
        }
    }

}
