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

import java.io.IOException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Query;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import java.net.URL;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.ResourceNotFoundException;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;

/**
 * Invoker for the Atom binding.
 * 
 * @version $Rev$ $Date$
 */
class GDataBindingInvoker implements Invoker, DataExchangeSemantics {

    Operation operation;
    String uri;
    GoogleService service;

    GDataBindingInvoker(Operation operation, String uri, GoogleService service) {
        this.operation = operation;
        this.uri = uri;
        this.service = service;
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

        public GetInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
        }

        @Override
        public Message invoke(Message msg) {

            try {
                String id = (String) ((Object[]) msg.getBody())[0];

                BaseEntry searchedEntry = service.getEntry(new URL(id), Entry.class);

                msg.setBody(searchedEntry);

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
    public static class PostInvoker extends GDataBindingInvoker {

        public PostInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
        }

        @Override
        public Message invoke(Message msg) {

            try {

                BaseEntry entry = (BaseEntry) ((Object[]) msg.getBody())[0];
                BaseEntry returnedEntry = service.insert(new URL(uri), entry);

                msg.setBody(returnedEntry);

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
    public static class PutInvoker extends GDataBindingInvoker {

        public PutInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
        }

        @Override
        public Message invoke(Message msg) {
            try {

                Object[] args = (Object[]) msg.getBody();
                String id = (String) args[0];
                BaseEntry entry = (BaseEntry) args[1];

                BaseEntry updatedEntry = service.update(new URL(id), entry);

                msg.setBody(updatedEntry);

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
     * Delete operation invoker
     */
    public static class DeleteInvoker extends GDataBindingInvoker {

        public DeleteInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
        }

        @Override
        public Message invoke(Message msg) {
            try {
                String id = (String) ((Object[]) msg.getBody())[0];
                service.delete(new URL(id));

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
    public static class GetAllInvoker extends GDataBindingInvoker {

        public GetAllInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
        }

        @Override
        public Message invoke(Message msg) {

            try {

                Feed feed = service.getFeed(new URL(uri), Feed.class);

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
    public static class QueryInvoker extends GDataBindingInvoker {

        public QueryInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
        }

        @Override
        public Message invoke(Message msg) {
            try {

                String strQuery = (String) ((Object[]) msg.getBody())[0];

                Query query = new Query(new URL(uri));
                query.setFullTextQuery(strQuery);
                Feed feed = service.query(query, Feed.class);
                msg.setBody(feed);

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
     * PostMedia operation invoker
     */
    public static class PostMediaInvoker extends GDataBindingInvoker {

        public PostMediaInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
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

        public PutMediaInvoker(Operation operation, String uri, GoogleService service) {
            super(operation, uri, service);
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
