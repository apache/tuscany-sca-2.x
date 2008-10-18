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

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.Link;
import com.google.gdata.data.ParseSource;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;

class GDataBindingListenerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GDataBindingListenerServlet.class.getName());
    private RuntimeWire wire;
    private Invoker getFeedInvoker;
    private Invoker queryInvoker;
    private Invoker getInvoker;
    private Invoker postInvoker;
    private Invoker postMediaInvoker;
    private Invoker putInvoker;
    private Invoker putMediaInvoker;
    private Invoker deleteInvoker;
    private MessageFactory messageFactory;
    private String title;
    private Mediator mediator;
//    private DataType<?> itemClassType;
//    private DataType<?> itemXMLType;
//    private boolean supportsFeedEntries;

    GDataBindingListenerServlet(RuntimeWire wire, MessageFactory messageFactory, Mediator mediator, String title) {
        this.wire = wire;
        this.messageFactory = messageFactory;
        this.mediator = mediator;
        this.title = title;

        // Get the invokers for the supported operations
        //Operation getOperation = null;
        for (InvocationChain invocationChain : this.wire.getInvocationChains()) {
            invocationChain.setAllowsPassByReference(true);
            Operation operation = invocationChain.getTargetOperation();
            String operationName = operation.getName();
            if (operationName.equals("getFeed")) {
                getFeedInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("query")) {
                queryInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("get")) {
                getInvoker = invocationChain.getHeadInvoker();
                //getOperation = operation;
            } else if (operationName.equals("put")) {
                putInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("putMedia")) {
                putMediaInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("post")) {
                postInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("postMedia")) {
                postMediaInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("delete")) {
                deleteInvoker = invocationChain.getHeadInvoker();
            }
        }

//        // Determine the collection item type
//        itemXMLType = new DataTypeImpl<Class<?>>(String.class.getName(), String.class, String.class);
//        Class<?> itemClass = getOperation.getOutputType().getPhysical();
//        if (itemClass == BaseEntry.class) {
//            supportsFeedEntries = true;
//        }
//        DataType<XMLType> outputType = getOperation.getOutputType();
//        QName qname = outputType.getLogical().getElementName();
//        qname = new QName(qname.getNamespaceURI(), itemClass.getSimpleName());
//        itemClassType = new DataTypeImpl<XMLType>("java:complexType", itemClass, new XMLType(qname, null));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int servletPathLength = request.getContextPath().length() + request.getServletPath().length();

        String path = URLDecoder.decode(request.getRequestURI().substring(servletPathLength), "UTF-8");

        logger.fine("get " + request.getRequestURI());

        if (path != null && path.equals("/atomsvc")) {
            //FIX-ME - Doing nothing
        } else if (path == null || path.length() == 0 || path.equals("/")) {

            // Return a feed containing the entries in the collection
            BaseFeed feed = null;

            Message requestMessage = messageFactory.createMessage();
            Message responseMessage;
            if (request.getQueryString() != null) {
                requestMessage.setBody(new Object[]{request.getQueryString()});
                responseMessage = queryInvoker.invoke(requestMessage);
            } else {
                responseMessage = getFeedInvoker.invoke(requestMessage);
            }
            if (responseMessage.isFault()) {
                throw new ServletException((Throwable) responseMessage.getBody());
            }

            // The service implementation supports feed entries, invoke its getFeed operation
            feed = (BaseFeed) responseMessage.getBody();

            if (feed != null) {

                // Write the Atom feed
                response.setContentType("application/atom+xml; charset=utf-8");
                try {

                    XmlWriter writer = new XmlWriter(response.getWriter());
                    feed.generateAtom(writer, new ExtensionProfile());
                    writer.flush();
                    writer.close();

                } catch (IOException ioe) {
                    throw new ServletException(ioe);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } else if (path.startsWith("/")) {
            // Return a specific entry in the collection
            BaseEntry feedEntry;

            // Invoke the get operation on the service implementation
            Message requestMessage = messageFactory.createMessage();
            String id = path.substring(1);
            requestMessage.setBody(new Object[]{id});
            Message responseMessage = getInvoker.invoke(requestMessage);
            if (responseMessage.isFault()) {
                throw new ServletException((Throwable) responseMessage.getBody());
            }

            // The service implementation returns a feed entry 
            feedEntry = responseMessage.getBody();

            // Write the Atom entry
            if (feedEntry != null) {
                response.setContentType("application/atom+xml; charset=utf-8");
                try {
                    XmlWriter writer = new XmlWriter(response.getWriter());
                    feedEntry.generateAtom(writer, new ExtensionProfile());
                    writer.flush();
                    writer.close();
                } catch (IOException ioe) {
                    throw new ServletException(ioe);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            // Path doesn't match any known pattern
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // FIX-ME: Authenticate the user

//        String user = processAuthorizationHeader(request);
//        if (user == null) {
//            unauthorized(response);
//            return;
//        }

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        if (path == null || path.length() == 0 || path.equals("/")) {

            BaseEntry createdFeedEntry = null;

            // Create a new Atom entry
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                BaseEntry feedEntry;
                try {
                    ParseSource parser = new ParseSource(request.getReader());
                    feedEntry = BaseEntry.readEntry(parser);

                } catch (ServiceException ex) {
                    throw new ServletException(ex);
                }

                // The service implementation supports feed entries, pass the entry to it
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[]{feedEntry});
                Message responseMessage = postInvoker.invoke(requestMessage);

                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable) responseMessage.getBody());
                }

                createdFeedEntry = responseMessage.getBody();

            } else if (contentType != null) {

                // Create a new media entry

                // Get incoming headers
                String reqTitle = request.getHeader("Title");
                String slug = request.getHeader("Slug");

                // Let the component implementation create the media entry
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[]{reqTitle, slug, contentType, request.getInputStream()});
                Message responseMessage = postMediaInvoker.invoke(requestMessage);

                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable) responseMessage.getBody());
                }
                createdFeedEntry = responseMessage.getBody();
            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }

            // A new entry was created successfully
            if (createdFeedEntry != null) {

                // Set location of the created entry in the Location header
                Link link = createdFeedEntry.getSelfLink();
                if (link != null) {
                    response.addHeader("Location", link.getHref().toString());
                }

                // Write the created Atom entry
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/atom+xml; charset=utf-8");
                try {

                    XmlWriter writer = new XmlWriter(response.getWriter());
                    createdFeedEntry.generateAtom(writer, new ExtensionProfile());
                    writer.flush();
                    writer.close();

                } catch (IOException ioe) {
                    throw new ServletException(ioe);
                }

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // FIX-ME: Authenticate the user

//        String user = processAuthorizationHeader(request);
//        if (user == null) {
//            unauthorized(response);
//            return;
//        }

        // Get the request path
        String path = request.getRequestURI().substring(request.getServletPath().length());

        BaseEntry createdFeedEntry = null;

        if (path != null && path.startsWith("/")) {
            String id = path.substring(1);

            // Update an Atom entry
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                BaseEntry feedEntry;
                try {
                    ParseSource parser = new ParseSource(request.getReader());
                    feedEntry = BaseEntry.readEntry(parser);

                } catch (ServiceException ex) {
                    throw new ServletException(ex);
                }

                // Let the component implementation create it

                // The service implementation supports feed entries, pass the entry to it
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[]{id, feedEntry});
                Message responseMessage = putInvoker.invoke(requestMessage);

                if (responseMessage.isFault()) {
                    Object body = responseMessage.getBody();
                    if (body.getClass().getName().endsWith(".NotFoundException")) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        throw new ServletException((Throwable) responseMessage.getBody());
                    }
                }

                createdFeedEntry = responseMessage.getBody();

            } else if (contentType != null) {

                // Updated a media entry

                // Let the component implementation create the media entry
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[]{id, contentType, request.getInputStream()});
                Message responseMessage = putMediaInvoker.invoke(requestMessage);
                Object body = responseMessage.getBody();
                if (responseMessage.isFault()) {
                    if (body.getClass().getName().endsWith(".NotFoundException")) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        throw new ServletException((Throwable) responseMessage.getBody());
                    }
                }

                createdFeedEntry = responseMessage.getBody();

            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }

            // A new entry was created successfully
            if (createdFeedEntry != null) {

                // Set location of the created entry in the Location header
                Link link = createdFeedEntry.getSelfLink();
                if (link != null) {
                    response.addHeader("Location", link.getHref().toString());
                }

                // Write the created Atom entry
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/atom+xml; charset=utf-8");
                try {

                    XmlWriter writer = new XmlWriter(response.getWriter());
                    createdFeedEntry.generateAtom(writer, new ExtensionProfile());
                    writer.flush();
                    writer.close();

                } catch (IOException ioe) {
                    throw new ServletException(ioe);
                }

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // FIX-ME: Authenticate the user
//        String user = processAuthorizationHeader(request);
//        if (user == null) {
//            unauthorized(response);
//            return;
//        }

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        String id;
        if (path != null && path.startsWith("/")) {
            id = path.substring(1);
        } else {
            id = "";
        }

        // Delete a specific entry from the collection
        Message requestMessage = messageFactory.createMessage();
        requestMessage.setBody(new Object[]{id});
        Message responseMessage = deleteInvoker.invoke(requestMessage);
        if (responseMessage.isFault()) {
            Object body = responseMessage.getBody();
            if (body.getClass().getName().endsWith(".NotFoundException")) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                throw new ServletException((Throwable) responseMessage.getBody());
            }
        }
    }
}
