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
package org.apache.tuscany.sca.binding.feed.provider;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.tuscany.sca.feed.NotFoundException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Invoker for the Atom binding.
 */
public class AtomBindingInvoker implements Invoker {

    Operation operation;
    String uri;
    HttpClient httpClient;
    String authorizationHeader;

    public AtomBindingInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
        this.operation = operation;
        this.uri = uri;
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;
    }

    public Message invoke(Message msg) {
        // Shouldn't get here, as the only supported methods are
        // defined in the ResourceCollection interface, and implemented
        // by specific invoker subclasses
        throw new UnsupportedOperationException(operation.getName());
    }

    /**
     * Get operation invoker
     * 
     * @version $Rev$ $Date$
     */
    public static class GetInvoker extends AtomBindingInvoker {

        public GetInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Get an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri + "/" + id);
            getMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200) {
                    Entry entry =
                        AtomEntryUtil.readEntry("atom_1.0", new InputStreamReader(getMethod.getResponseBodyAsStream()));
                    msg.setBody(entry);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                getMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * Post operation invoker
     * 
     * @version $Rev$ $Date$
     */
    public static class PostInvoker extends AtomBindingInvoker {

        public PostInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Post an entry
            Entry entry = (Entry)((Object[])msg.getBody())[0];

            // Send an HTTP POST
            PostMethod postMethod = new PostMethod(uri);
            postMethod.setRequestHeader("Authorization", authorizationHeader);
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                AtomEntryUtil.writeEntry(entry, "atom_1.0", writer);
                postMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                postMethod.setRequestEntity(new StringRequestEntity(writer.toString()));

                httpClient.executeMethod(postMethod);
                int status = postMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200 || status == 201) {
                    Entry createdEntry =
                        AtomEntryUtil
                            .readEntry("atom_1.0", new InputStreamReader(postMethod.getResponseBodyAsStream()));
                    msg.setBody(createdEntry);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                postMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * Put operation invoker
     * 
     * @version $Rev$ $Date$
     */
    public static class PutInvoker extends AtomBindingInvoker {

        public PutInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Put an entry
            Object[] args = (Object[])msg.getBody();
            String id = (String)args[0];
            Entry entry = (Entry)args[1];

            // Send an HTTP PUT
            PutMethod putMethod = new PutMethod(uri + "/" + id);
            putMethod.setRequestHeader("Authorization", authorizationHeader);
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                AtomEntryUtil.writeEntry(entry, "atom_1.0", writer);
                putMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                putMethod.setRequestEntity(new StringRequestEntity(writer.toString()));

                httpClient.executeMethod(putMethod);
                int status = putMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200 || status == 201) {
                    try {
                        Entry updatedEntry =
                            AtomEntryUtil.readEntry("atom_1.0", new InputStreamReader(putMethod
                                .getResponseBodyAsStream()));
                        msg.setBody(updatedEntry);
                    } catch (Exception e) {
                        // Returning the updated entry is optional
                    }

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
     * 
     * @version $Rev$ $Date$
     */
    public static class DeleteInvoker extends AtomBindingInvoker {

        public DeleteInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
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

                // Read the Atom entry
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
     * GetCollection operation invoker
     * 
     * @version $Rev$ $Date$
     */
    public static class GetCollectionInvoker extends AtomBindingInvoker {

        public GetCollectionInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Get a feed

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri);
            getMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                // Read the Atom feed
                if (status == 200) {
                    WireFeedInput input = new WireFeedInput();
                    Feed feed = (Feed)input.build(new XmlReader(getMethod.getResponseBodyAsStream()));
                    msg.setBody(feed);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                getMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * PostMedia operation invoker
     * 
     * @version $Rev$ $Date$
     */
    public static class PostMediaInvoker extends AtomBindingInvoker {

        public PostMediaInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * PutMedia operation invoker
     * 
     * @version $Rev$ $Date$
     */
    public static class PutMediaInvoker extends AtomBindingInvoker {

        public PutMediaInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

}
