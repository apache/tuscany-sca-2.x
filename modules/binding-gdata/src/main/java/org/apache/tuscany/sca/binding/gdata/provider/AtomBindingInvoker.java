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
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import java.net.URL;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.ResourceNotFoundException;

/**
 * Invoker for the Atom binding.
 * 
 * @version $Rev$ $Date$
 */
class AtomBindingInvoker implements Invoker {

    Operation operation;
    String uri;
    GoogleService myService;

    AtomBindingInvoker(Operation operation, String uri, GoogleService myService) {
        this.operation = operation;
        this.uri = uri;
        this.myService = myService;
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

        public GetInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends AtomBindingInvoker {

        public PostInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);

        }
    }

    /**
     * Put operation invoker
     */
    public static class PutInvoker extends AtomBindingInvoker {

        public PutInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends AtomBindingInvoker {

        public DeleteInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends AtomBindingInvoker {

        public GetAllInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {

            try {
                //FIXME - Get credentials automatically
                myService.setUserCredentials("gsocstudent2008@gmail.com", "gsoc2008");

                Feed feed = myService.getFeed(new URL(uri), Feed.class);

                //FIXME - Only for tests
                System.out.println("Feed content - " + feed.getUpdated().toString() + ":\n");
                for (Entry e : feed.getEntries()) {
                    System.out.println("# " + e.getTitle().getPlainText());
                }

                msg.setBody(feed);

            } catch (ResourceNotFoundException ex) {
                msg.setFaultBody(new ResourceNotFoundException("Invalid Resource at " + uri));
            } catch (ServiceException ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            } catch (Exception ex) {
                msg.setFaultBody(new ServiceRuntimeException(ex));
            }

            return msg;
        }
    }

    /**
     * Query operation invoker
     */
    public static class QueryInvoker extends AtomBindingInvoker {

        public QueryInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * PostMedia operation invoker
     */
    public static class PostMediaInvoker extends AtomBindingInvoker {

        public PostMediaInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
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
    public static class PutMediaInvoker extends AtomBindingInvoker {

        public PutMediaInvoker(Operation operation, String uri, GoogleService myService) {
            super(operation, uri, myService);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }
}
