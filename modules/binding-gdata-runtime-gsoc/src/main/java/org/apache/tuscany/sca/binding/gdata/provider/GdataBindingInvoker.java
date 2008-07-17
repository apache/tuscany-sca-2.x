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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.tuscany.sca.binding.gdata.collection.NotFoundException;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

import com.google.gdata.client.GoogleService;
import com.google.gdata.data.Feed;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.ServiceException;

/**
 * Invoker for the GData binding.
 */
class GdataBindingInvoker implements Invoker, DataExchangeSemantics {

    Operation operation;
    String uri;
    HttpClient httpClient;
    String authorizationHeader;
    GdataReferenceBindingProvider provider;
    GoogleService googleService;

    GdataBindingInvoker(Operation operation,
                        String uri,
                        GoogleService googleService,
                        HttpClient httpClient,
                        String authorizationHeader,
                        GdataReferenceBindingProvider bindingProvider) {
        this.operation = operation;
        this.uri = uri;
        this.googleService = googleService;
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
    public static class GetInvoker extends GdataBindingInvoker {
        public GetInvoker(Operation operation,
                          String uri,
                          GoogleService googleService,
                          HttpClient httpClient,
                          String authorizationHeader,
                          GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
        }

        @SuppressWarnings("finally")
        @Override
        public Message invoke(Message msg) {

            // Get the entry id from the message body
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri + "/" + id);
            getMethod.setRequestHeader("Authorization", authorizationHeader);

            try {
                URL entryURL = new URL(uri + "/" + id);
                com.google.gdata.data.Entry feedEntry = googleService.getEntry(entryURL, com.google.gdata.data.Entry.class);
                msg.setBody(feedEntry);
            } catch (IOException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } catch (ServiceException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                return msg;
            }

        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends GdataBindingInvoker {

        public PostInvoker(Operation operation,
                           String uri,
                           GoogleService googleService,
                           HttpClient httpClient,
                           String authorizationHeader,
                           GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
        }

        @SuppressWarnings("finally")
        @Override
        public Message invoke(Message msg) {

            System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- invoke method reached");
            // Post an entry
            Object[] args = (Object[])msg.getBody();
            com.google.gdata.data.Entry feedEntry = null;

            if (provider.supportsFeedEntries()) {

                // Expect an Atom entry

                System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- supportsFeedEntries: " + provider
                    .supportsFeedEntries());
                feedEntry = (com.google.gdata.data.Entry)args[0];

                System.out.println("[Debug Info]GdataBindingInvoker.PostInvoker --- feedEntry title: " + feedEntry
                    .getTitle().getPlainText());

            } else {
                // Expect a key and data item
                Entry<Object, Object> entry = new Entry<Object, Object>(args[0], args[1]);

                // FIXME: this needs to be examinated more....
                feedEntry =
                    feedEntry(entry, provider.getItemClassType(), provider.getItemXMLType(), provider.getMediator());
            }


            try {
               
                com.google.gdata.data.Entry createdEntry = googleService.insert(new URL(uri), feedEntry);

                msg.setBody(createdEntry);

            } catch (IOException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } catch (ServiceException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                return msg;
            }
        }
    }

    
    
    /**
     * Put operation invoker
     */
    public static class PutInvoker extends GdataBindingInvoker {

        public PutInvoker(Operation operation,
                          String uri,
                          GoogleService googleService,
                          HttpClient httpClient,
                          String authorizationHeader,
                          GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
        }

        @SuppressWarnings("finally")
        @Override
        public Message invoke(Message msg) {

            // Put an entry
            Object[] args = (Object[])msg.getBody();
            String id;
            com.google.gdata.data.Entry feedEntry = null;
            if (provider.supportsFeedEntries()) {
                // Expect a key and GData entry
                id = (String)args[0];
                feedEntry = (com.google.gdata.data.Entry)args[1];
            } else {

                // Expect a key and data item
                id = (String)args[0];
                Entry<Object, Object> entry = new Entry<Object, Object>(id, args[1]);

                // FIXME: The following statement needs to be modified
                // accordingly
                /*
                 * feedEntry = feedEntry(entry, provider.getItemClassType(),
                 * provider.getItemXMLType(), provider.getMediator(),
                 * abderaFactory);
                 */
            }

            // Send an HTTP PUT <Localhost>
            PutMethod putMethod = new PutMethod(uri + "/" + id);
            putMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                URL entryURL = new URL(uri + "/" + id);
                com.google.gdata.data.Entry updatedEntry = googleService.update(entryURL, feedEntry);

                msg.setBody(updatedEntry);

            } catch (IOException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } catch (ServiceException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                return msg;
            }    }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends GdataBindingInvoker {

        public DeleteInvoker(Operation operation,
                             String uri,
                             GoogleService googleService,
                             HttpClient httpClient,
                             String authorizationHeader,
                             GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
        }

        @SuppressWarnings("finally")
        @Override
        public Message invoke(Message msg) {

            // Delete an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP DELETE(Localhost)
            DeleteMethod deleteMethod = new DeleteMethod(uri + "/" + id);
            deleteMethod.setRequestHeader("Authorization", authorizationHeader);

            try {
                    URL entryURL = new URL(uri + "/" + id);
                    googleService.delete(entryURL);

                } catch (IOException ex) {
                    msg.setFaultBody(new ServiceRuntimeException(ex));
                } catch (ServiceException ex) {
                    msg.setFaultBody(new ServiceRuntimeException(ex));
                } finally {
                    return msg;
                }
            }

    }

   
    /**
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends GdataBindingInvoker {

        public GetAllInvoker(Operation operation,
                             String uri,
                             GoogleService googleService,
                             HttpClient httpClient,
                             String authorizationHeader,
                             GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
        }

        @Override
        public Message invoke(Message msg) {

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri);
            getMethod.setRequestHeader("Authorization", authorizationHeader);

            System.out.println("[Debug Info] GdataBindingInvoker.GetAllInvoker.invoke---feedURL: " + uri);

            boolean parsing = false;

            try {

                Feed feed = googleService.getFeed(new URL(uri), Feed.class);

                msg.setBody(feed);

            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + uri));
            } catch (ServiceException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                return msg;
            }

        }
    }

    /**
     * Query operation invoker
     */
    public static class QueryInvoker extends GdataBindingInvoker {

        public QueryInvoker(Operation operation,
                            String uri,
                            GoogleService googleService,
                            HttpClient httpClient,
                            String authorizationHeader,
                            GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
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

                    URL feedURL = new URL(getMethod.getURI().toString());

                    System.out.println("GdataBindingInvoker.GetInvoker.invoke---feedURL: " + feedURL);

                    com.google.gdata.data.Feed feed = googleService.getFeed(feedURL, com.google.gdata.data.Feed.class);

                    System.out.println("GetAllInvoker class:   I am good here 04");

                    System.out.println("feed title: " + feed.getTitle().getPlainText());

                    System.out.println("provider.supportsFeedEntries()" + provider.supportsFeedEntries());

                    if (provider.supportsFeedEntries()) {

                        // Returns the Atom feed
                        msg.setBody(feed);

                    } else {

                        // Returns an array of data entries

                        // FIXME: This part needs to be fixed while working on
                        // the query operation
                        List<Entry<Object, Object>> entries = new ArrayList<Entry<Object, Object>>();
                        for (com.google.gdata.data.Entry feedEntry : feed.getEntries()) {
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
                                GoogleService googleService,
                                HttpClient httpClient,
                                String authorizationHeader,
                                GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
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
                               GoogleService googleService,
                               HttpClient httpClient,
                               String authorizationHeader,
                               GdataReferenceBindingProvider bindingProvider) {
            super(operation, uri, googleService, httpClient, authorizationHeader, bindingProvider);
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
