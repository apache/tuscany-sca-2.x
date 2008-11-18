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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Query;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.Entry;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.Feed;
import com.google.gdata.data.ParseSource;
import com.google.gdata.util.AuthenticationException;
import java.net.URL;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.common.xml.XmlWriter;
import java.io.StringWriter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.tuscany.sca.binding.gdata.GDataBinding;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;

/**
 * Invoker for the Atom binding.
 * 
 * @version $Rev$ $Date$
 */
class GDataBindingInvoker implements Invoker, DataExchangeSemantics {

    Operation operation;
    GDataBinding binding;
    HttpClient httpClient;
    String authorizationHeader;
    GoogleService service;

    GDataBindingInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
        this.operation = operation;
        this.binding = binding;
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;

        //Create the GoogleService
        if (!binding.getServiceType().equals("sca")) {
            this.service = new GoogleService(binding.getServiceType(), "");

            try {
                service.setUserCredentials(binding.getUsername(), binding.getPassword());
            } catch (AuthenticationException ex) {
                //FIXME - promote the exception
                Logger.getLogger(GDataReferenceBindingProvider.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.service.setConnectTimeout(60000);
        }
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
    public static class GetInvoker extends GDataBindingInvoker {

        public GetInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            BaseEntry entry;
            GetMethod getMethod = null;
            boolean parsing = false;

            String id = (String) ((Object[]) msg.getBody())[0];

            try {
                // serviceType == "sca" - Send an HTTP GET
                if (service == null) {
                    getMethod = new GetMethod(binding.getURI() + "/" + id);
                    getMethod.setRequestHeader("Authorization", authorizationHeader);

                    httpClient.executeMethod(getMethod);
                    int status = getMethod.getStatusCode();

                    // Read the Atom feed
                    if (status == 200) {

                        parsing = true;
                        
                        ParseSource parser = new ParseSource(getMethod.getResponseBodyAsStream());
                        entry = BaseEntry.readEntry(parser);

                        msg.setBody(entry);

                    } else if (status == 404) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                    }

                } // serviceType != "sca" - Use GoogleService
                else {
                    entry = service.getEntry(new URL(id), Entry.class);
                    msg.setBody(entry);
                }
            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + binding.getURI()));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                if (service == null && !parsing) {
                    getMethod.releaseConnection();
                }
                return msg;
            }

        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends GDataBindingInvoker {

        public PostInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            BaseEntry entry = (BaseEntry) ((Object[]) msg.getBody())[0];
            BaseEntry returnedEntry;

            PostMethod postMethod = null;
            boolean parsing = false;

            try {
                // serviceType == "sca" - Send an HTTP POST
                if (service == null) {
                    postMethod = new PostMethod(binding.getURI());
                    postMethod.setRequestHeader("Authorization", authorizationHeader);

                    // Write the Atom entry
                    StringWriter strWriter = new StringWriter();
                    XmlWriter writer = new XmlWriter(strWriter);
                    entry.generateAtom(writer, new ExtensionProfile());
                    writer.flush();
                    writer.close();

                    postMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                    postMethod.setRequestEntity(new StringRequestEntity(strWriter.toString()));

                    httpClient.executeMethod(postMethod);
                    int status = postMethod.getStatusCode();

                    // Read the Atom feed
                    if (status == 200 || status == 201) {

                        parsing = true;

                        ParseSource parser = new ParseSource(postMethod.getResponseBodyAsStream());
                        returnedEntry = BaseEntry.readEntry(parser);

                        msg.setBody(returnedEntry);

                    } else if (status == 404) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                    }

                } // serviceType != "sca" - Use GoogleService
                else {
                    returnedEntry = service.insert(new URL(binding.getURI()), entry);
                    msg.setBody(returnedEntry);
                }
            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + binding.getURI()));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                if (service == null && !parsing) {
                    postMethod.releaseConnection();
                }
                return msg;
            }

        }
    }

    /**
     * Put operation invoker
     */
    public static class PutInvoker extends GDataBindingInvoker {

        public PutInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            BaseEntry updatedEntry;
            String id = (String) ((Object[]) msg.getBody())[0];
            BaseEntry entry = (BaseEntry) ((Object[]) msg.getBody())[1];

            PutMethod putMethod = null;
            boolean parsing = false;

            try {
                // serviceType == "sca" - Send an HTTP PUT
                if (service == null) {
                    putMethod = new PutMethod(binding.getURI() + "/" + id);
                    putMethod.setRequestHeader("Authorization", authorizationHeader);

                    // Write the Atom entry
                    StringWriter strWriter = new StringWriter();
                    XmlWriter writer = new XmlWriter(strWriter);
                    entry.generateAtom(writer, new ExtensionProfile());
                    writer.flush();
                    writer.close();

                    putMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                    putMethod.setRequestEntity(new StringRequestEntity(strWriter.toString()));

                    httpClient.executeMethod(putMethod);
                    int status = putMethod.getStatusCode();

                    // Read the Atom feed
                    if (status == 200 || status == 201) {

                        parsing = true;

                        ParseSource parser = new ParseSource(putMethod.getResponseBodyAsStream());
                        updatedEntry = BaseEntry.readEntry(parser);

                        msg.setBody(updatedEntry);

                    } else if (status == 404) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                    }

                } // serviceType != "sca" - Use GoogleService
                else {
                    updatedEntry = service.update(new URL(id), entry);
                    msg.setBody(updatedEntry);
                }
            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + binding.getURI()));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                if (service == null && !parsing) {
                    putMethod.releaseConnection();
                }
                return msg;
            }
        }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends GDataBindingInvoker {

        public DeleteInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            DeleteMethod deleteMethod = null;

            String id = (String) ((Object[]) msg.getBody())[0];

            try {
                // serviceType == "sca" - Send an HTTP DELETE
                if (service == null) {
                    deleteMethod = new DeleteMethod(binding.getURI() + "/" + id);
                    deleteMethod.setRequestHeader("Authorization", authorizationHeader);

                    httpClient.executeMethod(deleteMethod);
                    int status = deleteMethod.getStatusCode();

                    // Read the Atom feed
                    if (status == 200) {
                        msg.setBody(null);

                    } else if (status == 404) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                    }

                } // serviceType != "sca" - Use GoogleService
                else {
                    service.delete(new URL(id));
                    msg.setBody(null);
                }
            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + binding.getURI()));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                if (service == null) {
                    deleteMethod.releaseConnection();
                }
                return msg;
            }

        }
    }

    /**
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends GDataBindingInvoker {

        public GetAllInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            BaseFeed feed;
            GetMethod getMethod = null;
            boolean parsing = false;

            try {
                // serviceType == "sca" - Send an HTTP GET
                if (service == null) {
                    getMethod = new GetMethod(binding.getURI());
                    getMethod.setRequestHeader("Authorization", authorizationHeader);

                    httpClient.executeMethod(getMethod);
                    int status = getMethod.getStatusCode();

                    // Read the Atom feed
                    if (status == 200) {

                        parsing = true;

                        ParseSource parser = new ParseSource(getMethod.getResponseBodyAsStream());
                        feed = BaseFeed.readFeed(parser);

                        msg.setBody(feed);

                    } else if (status == 404) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                    }

                } // serviceType != "sca" - Use GoogleService
                else {
                    feed = service.getFeed(new URL(binding.getURI()), Feed.class);
                    msg.setBody(feed);
                }
            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + binding.getURI()));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                if (service == null && !parsing) {
                    getMethod.releaseConnection();
                }
                return msg;
            }
        }
    }

    /**
     * Query operation invoker
     */
    public static class QueryInvoker extends GDataBindingInvoker {

        public QueryInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            
            BaseFeed feed;
            GetMethod getMethod = null;
            boolean parsing = false;

            String queryString = (String) ((Object[]) msg.getBody())[0];

            try {
                // serviceType == "sca" - Send an HTTP GET
                if (service == null) {
                    getMethod = new GetMethod(binding.getURI());
                    getMethod.setRequestHeader("Authorization", authorizationHeader);
                    getMethod.setQueryString(queryString);

                    httpClient.executeMethod(getMethod);
                    int status = getMethod.getStatusCode();

                    // Read the Atom feed
                    if (status == 200) {

                        parsing = true;

                        ParseSource parser = new ParseSource(getMethod.getResponseBodyAsStream());
                        feed = BaseFeed.readFeed(parser);

                        msg.setBody(feed);

                    } else if (status == 404) {
                        msg.setFaultBody(new NotFoundException());
                    } else {
                        msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                    }

                } // serviceType != "sca" - Use GoogleService
                else {
                    Query query = new Query(new URL(binding.getURI()));
                    query.setFullTextQuery(queryString);
                    feed = service.query(query, Feed.class);
                    msg.setBody(feed);
                }
            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + binding.getURI()));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } finally {
                if (service == null && !parsing) {
                    getMethod.releaseConnection();
                }
                return msg;
            }
        }
    }

    /**
     * PostMedia operation invoker
     */
    public static class PostMediaInvoker extends GDataBindingInvoker {

        public PostMediaInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
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
    public static class PutMediaInvoker extends GDataBindingInvoker {

        public PutMediaInvoker(Operation operation, GDataBinding binding, HttpClient httpClient, String authorizationHeader) {
            super(operation, binding, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    public boolean allowsPassByReference() {
        return true;
    }
}
