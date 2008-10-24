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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.abdera.parser.ParseException;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;

import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.ParseSource;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.util.ServiceException;

/**
 * A resource collection binding listener, implemented as a Servlet and
 * registered in a Servlet host provided by the SCA hosting runtime.
 * 
 * @version $Rev$ $Date$
 */
class GdataBindingListenerServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(GdataBindingListenerServlet.class.getName());
    private static final long serialVersionUID = 1L;
     
    private RuntimeWire wire;
    private Invoker getFeedInvoker;
    private Invoker getAllInvoker;
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
    private DataType<?> itemClassType;
    private DataType<?> itemXMLType;
    private boolean supportsFeedEntries;

    /**
     * Constructs a new binding listener.
     * 
     * @param wire
     * @param messageFactory
     * @param feedType
     */
    GdataBindingListenerServlet(RuntimeWire wire, MessageFactory messageFactory, Mediator mediator, String title) {
        this.wire = wire;
        this.messageFactory = messageFactory;
        this.mediator = mediator;
        this.title = title;

        // Get the invokers for the supported operations
        Operation getOperation = null;
        for (InvocationChain invocationChain : this.wire.getInvocationChains()) {
            invocationChain.setAllowsPassByReference(true);
            Operation operation = invocationChain.getTargetOperation();
            String operationName = operation.getName();
           
            if (operationName.equals("getFeed")) {

                //System.out.println("[Debug Info]GdataBindingListenerServlet constructor --- operation: getFeed");
                getFeedInvoker = invocationChain.getHeadInvoker();

            } else if (operationName.equals("getAll")) {

                getAllInvoker = invocationChain.getHeadInvoker();

            } else if (operationName.equals("query")) {
                
                queryInvoker = invocationChain.getHeadInvoker();
                
            } else if (operationName.equals("get")) {
                
                //System.out.println("[Debug Info]GdataBindingListenerServlet Constructor --- opeartion: get");
                getInvoker = invocationChain.getHeadInvoker();
                getOperation = operation;
                
            } else if (operationName.equals("put")) {
                
                putInvoker = invocationChain.getHeadInvoker();
                
            } else if (operationName.equals("putMedia")) {
                
                putMediaInvoker = invocationChain.getHeadInvoker();
                
            } else if (operationName.equals("post")) {

                //System.out.println("[Debug Info]GdataBindingListenerServlet Constructor --- opeartion: post");
                postInvoker = invocationChain.getHeadInvoker();

            } else if (operationName.equals("postMedia")) {
                postMediaInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("delete")) {
                deleteInvoker = invocationChain.getHeadInvoker();
            }
        }

        //System.out.println("[Debug Info]GdataBindingListenerServlet constructor --- I am good here 00");

        // Determine the collection item type
        itemXMLType = new DataTypeImpl<Class<?>>(String.class.getName(), String.class, String.class);
        Class<?> itemClass = getOperation.getOutputType().getPhysical();
       
        if (itemClass == com.google.gdata.data.Entry.class) {
            supportsFeedEntries = true;
        }
        DataType<XMLType> outputType = getOperation.getOutputType();
        QName qname = outputType.getLogical().getElementName();
        qname = new QName(qname.getNamespaceURI(), itemClass.getSimpleName());
        itemClassType = new DataTypeImpl<XMLType>("java:complexType", itemClass, new XMLType(qname, null));

        System.out.println("[Debug Info]GdataBindingListenerServlet --- initilized!");
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // No authentication required for a get request
        System.out.println("[Debug Info]GdataBindingListenerServlet doGet() --- I am good here 00");

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        System.out.println("[Debug Info]GdataBindingListenerServlet doGet() --- request.getRequestURI():  " + request
            .getRequestURI());
        System.out.println("[Debug Info]GdataBindingListenerServlet doGet()--- path: " + path);

        // FIXME: Log this get http request, commented out for testing
        logger.fine("get " + request.getRequestURI());

        // Handle an Atom request
        if (path != null && path.equals("/atomsvc")) {

            //FIXME: This needs to be fixed, for /atomsvc
            
            /*            
            System.out.println("GdataBindingListenerServlet doGet(): I am good here brach 01");
            // Return the Atom service document
            response.setContentType("application/atomsvc+xml; charset=utf-8");
            Service service = abderaFactory.newService();
            // service.setText("service");

            Workspace workspace = abderaFactory.newWorkspace();
            workspace.setTitle("resource");

            String href = request.getRequestURL().toString();
            href = href.substring(0, href.length() - "/atomsvc".length());

            Collection collection = workspace.addCollection("collection", "atom/feed");
            collection.setTitle("entries");
            collection.setAttributeValue("href", href);
            collection.setAccept("entry");
            collection.addCategories().setFixed(false);

            workspace.addCollection(collection);

            service.addWorkspace(workspace);

            // FIXME add prettyPrint support
            try {
                service.getDocument().writeTo(response.getOutputStream());
            } catch (IOException ioe) {
                throw new ServletException(ioe);
            }
            */

        } else if (path == null || path.length() == 0 || path.equals("/")) {

            // get HTTP request asking for a feed

            System.out.println("[Debug Info]GdataBindingListenerServlet doGet() --- I am good here brach 02");

            // Return a feed containing the entries in the collection
            com.google.gdata.data.Feed feed = null;

            if (supportsFeedEntries) {

                System.out.println("[Debug Info]GdataBindingListenerServlet doGet() --- supportsFeedEntries: " + supportsFeedEntries);

                // The service implementation supports feed entries, invoke its
                // getFeed operation
                Message requestMessage = messageFactory.createMessage();
                Message responseMessage;
                if (request.getQueryString() != null) {
                    System.out.println("getQueryString != null");
                    requestMessage.setBody(new Object[] {request.getQueryString()});
                    responseMessage = queryInvoker.invoke(requestMessage);
                } else {
                    System.out.println("getQueryString == null");
                    responseMessage = getFeedInvoker.invoke(requestMessage);
                }
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }

                //System.out.println("response msg class:" + responseMessage.getBody().getClass());

                feed = (com.google.gdata.data.Feed)responseMessage.getBody();

                System.out.println("feed title: " + feed.getTitle().getPlainText());

            } else {

                System.out.println("GdataBindingListenerServlet doGet(): do not supportsFeedEntries");

                // The service implementation does not support feed entries,
                // invoke its getAll operation to get the data item collection,
                // then create
                // feed entries from the items
                Message requestMessage = messageFactory.createMessage();
                Message responseMessage;
                if (request.getQueryString() != null) {
                    requestMessage.setBody(new Object[] {request.getQueryString()});
                    responseMessage = queryInvoker.invoke(requestMessage);
                } else {
                    responseMessage = getAllInvoker.invoke(requestMessage);

                    //System.out
                    //    .println("GdataBindingListner.doGet(): get msg from getAllInvoker.invoke()" + responseMessage
                    //        .getBody().toString());

                }
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                Entry<Object, Object>[] collection = (Entry<Object, Object>[])responseMessage.getBody();
                if (collection != null) {

                    // Create the feed
                    feed = new com.google.gdata.data.Feed();

                    // Set the feed title
                    if (title != null) {
                        feed.setTitle(new PlainTextConstruct(title));
                    } else {
                        feed.setTitle(new PlainTextConstruct("Feed title"));
                    }

                    // Add entries to the feed
                    ArrayList<com.google.gdata.data.Entry> entries = new ArrayList<com.google.gdata.data.Entry>();
                    for (Entry<Object, Object> entry : collection) {
                        com.google.gdata.data.Entry feedEntry = feedEntry(entry, itemClassType, itemXMLType, mediator);
                        entries.add(feedEntry);
                    }
                    feed.setEntries(entries);

                }
            }
            if (feed != null) {

                // //System.out.println("feed(from the http response)is not
                // null");

                // Write a GData feed using Atom representation
                response.setContentType("application/atom+xml; charset=utf-8");

                // Generate the corresponding Atom representation of the feed
                StringWriter stringWriter = new StringWriter();
                com.google.gdata.util.common.xml.XmlWriter w =
                    new com.google.gdata.util.common.xml.XmlWriter(stringWriter);
                feed.generateAtom(w, new ExtensionProfile());
                w.flush();

                // Write the Atom representation(XML) into Http response content
                OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
                PrintWriter out = new PrintWriter(response.getOutputStream());
                out.println(stringWriter.toString());
                out.close();

                System.out.println("Feed content in plain text:" + stringWriter.toString());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } else if (path.startsWith("/")) {

            // get HTTP request asking for an entry

            // Return a specific entry in the collection
            com.google.gdata.data.Entry feedEntry = null;

            // Invoke the get operation on the service implementation
            Message requestMessage = messageFactory.createMessage();
            String id = path.substring(1);
            requestMessage.setBody(new Object[] {id});
            Message responseMessage = getInvoker.invoke(requestMessage);
            if (responseMessage.isFault()) {
                throw new ServletException((Throwable)responseMessage.getBody());
            }

            if (supportsFeedEntries) {
                // The service implementation returns a feed entry
                feedEntry = (com.google.gdata.data.Entry)responseMessage.getBody();

                System.out.println("entry title: " + feedEntry.getTitle().getPlainText());

            } else {
                // The service implementation only returns a data item, create
                // an entry
                // from it
                Entry<Object, Object> entry = new Entry<Object, Object>(id, responseMessage.getBody());
                // FIXME The line below needs to be fixed
                // feedEntry = feedEntry(entry, itemClassType, itemXMLType,
                // mediator, abderaFactory);
            }

            // Write the Gdata entry
            if (feedEntry != null) {

                // Write a GData entry using Atom representation
                response.setContentType("application/atom+xml; charset=utf-8");

                // Generate the corresponding Atom representation of the feed
                StringWriter stringWriter = new StringWriter();
                com.google.gdata.util.common.xml.XmlWriter w =
                    new com.google.gdata.util.common.xml.XmlWriter(stringWriter);
                feedEntry.generateAtom(w, new ExtensionProfile());
                w.flush();

                // Write the Atom representation(XML) into Http response content
                OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
                PrintWriter out = new PrintWriter(response.getOutputStream());
                out.println(stringWriter.toString());
                out.close();

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            // Path doesn't match any known pattern
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {

        System.out.println("[Debug Info]GdataBindingListenerServlet doPost() --- reached");

        // Authenticate the user
        String user = processAuthorizationHeader(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        System.out.println("[Debug Info]GdataBindingListenerServlet path --- " + path);

        if (path == null || path.length() == 0 || path.equals("/")) {
            // Create a new Gdata entry
            com.google.gdata.data.Entry createdFeedEntry = null;
            String contentType = request.getContentType();

            if (contentType != null && contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                com.google.gdata.data.Entry feedEntry = null;
                try {
                    ParseSource source = new ParseSource(request.getReader());
                    feedEntry = com.google.gdata.data.Entry.readEntry(source, com.google.gdata.data.Entry.class, null);
                } catch (ParseException pe) {
                    throw new ServletException(pe);
                } catch (com.google.gdata.util.ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ServiceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Let the component implementation create it
                if (supportsFeedEntries) {

                    // The service implementation supports feed entries, pass
                    // the entry to it
                    Message requestMessage = messageFactory.createMessage();

                    requestMessage.setBody(new Object[] {feedEntry});

                    Message responseMessage = postInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    createdFeedEntry = responseMessage.getBody();
                } else {

                    // The service implementation does not support feed entries,
                    // pass the data item to it
                    Message requestMessage = messageFactory.createMessage();
                    Entry<Object, Object> entry = entry(feedEntry, itemClassType, itemXMLType, mediator);
                    requestMessage.setBody(new Object[] {entry.getKey(), entry.getData()});
                    Message responseMessage = postInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    entry.setKey(responseMessage.getBody());

                    createdFeedEntry = feedEntry(entry, itemClassType, itemXMLType, mediator);
                }

            } else if (contentType != null) {

                // Create a new media entry

                // Get incoming headers
                String title = request.getHeader("Title");
                String slug = request.getHeader("Slug");

                // Let the component implementation create the media entry
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[] {title, slug, contentType, request.getInputStream()});
                Message responseMessage = postMediaInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                createdFeedEntry = responseMessage.getBody();
            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }

            // A new entry was created successfully
            if (createdFeedEntry != null) {

                // Set location of the created entry in the Location header
                // Link link = createdFeedEntry.getSelfLink();
                // if (link != null) {
                // response.addHeader("Location", link.getHref().toString());
                // }

                // Write the created Atom entry
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/atom+xml; charset=utf-8");
                try {

                    // Generate the corresponding Atom representation of the
                    // feed
                    StringWriter stringWriter = new StringWriter();
                    com.google.gdata.util.common.xml.XmlWriter w =
                        new com.google.gdata.util.common.xml.XmlWriter(stringWriter);
                    createdFeedEntry.generateAtom(w, new ExtensionProfile());
                    w.flush();

                    // Write the Atom representation(XML) into Http response
                    // content
                    OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
                    PrintWriter out = new PrintWriter(response.getOutputStream());
                    out.println(stringWriter.toString());
                    out.close();

                } catch (ParseException pe) {
                    throw new ServletException(pe);
                }

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Writer getWriter(HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        return writer;
    }

    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Authenticate the user
        String user = processAuthorizationHeader(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        // Get the request path
        String path = request.getRequestURI().substring(request.getServletPath().length());

        System.out.println("[Debug Info] localServlet doPut --- path: " + path);
        
        if (path != null && path.startsWith("/")) {
            String id = path.substring(1);

            // Update an Atom entry
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                com.google.gdata.data.Entry feedEntry = null;
                try {
                    ParseSource source = new ParseSource(request.getReader());
                    feedEntry = com.google.gdata.data.Entry.readEntry(source, com.google.gdata.data.Entry.class, null);
                
                    System.out.println("[Debug Info] localServlet doPut --- feedEntry title: " + feedEntry.getTitle().getPlainText());
                } catch (ParseException pe) {
                    throw new ServletException(pe);
                } catch (com.google.gdata.util.ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ServiceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                

                // Let the component implementation create it
                if (supportsFeedEntries) {

                    System.out.println("[Debug Info] localServlet doPut --- supportsFeedEntries: " + supportsFeedEntries);
                    
                    // The service implementation supports feed entries, pass
                    // the entry to it
                    Message requestMessage = messageFactory.createMessage();
                    requestMessage.setBody(new Object[] {id, feedEntry});
                    Message responseMessage = putInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        Object body = responseMessage.getBody();
                        if (body.getClass().getName().endsWith(".NotFoundException")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        } else {
                            throw new ServletException((Throwable)responseMessage.getBody());
                        }
                    }
                } else {

                    // The service implementation does not support feed entries,
                    // pass the data item to it
                    Message requestMessage = messageFactory.createMessage();
                    Entry<Object, Object> entry = entry(feedEntry, itemClassType, itemXMLType, mediator);
                    requestMessage.setBody(new Object[] {entry.getKey(), entry.getData()});
                    Message responseMessage = putInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        Object body = responseMessage.getBody();
                        if (body.getClass().getName().endsWith(".NotFoundException")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        } else {
                            throw new ServletException((Throwable)responseMessage.getBody());
                        }
                    }
                }
                
                // Write the Gdata entry
                if (feedEntry != null) {

                    // Write a GData entry using Atom representation
                    response.setContentType("application/atom+xml; charset=utf-8");

                    // Generate the corresponding Atom representation of the feed
                    StringWriter stringWriter = new StringWriter();
                    com.google.gdata.util.common.xml.XmlWriter w =
                        new com.google.gdata.util.common.xml.XmlWriter(stringWriter);
                    feedEntry.generateAtom(w, new ExtensionProfile());
                    w.flush();

                    // Write the Atom representation(XML) into Http response content
                    OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
                    PrintWriter out = new PrintWriter(response.getOutputStream());
                    out.println(stringWriter.toString());
                    out.close();

                }

            } else if (contentType != null) {

                // Updated a media entry

                // Let the component implementation create the media entry
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[] {id, contentType, request.getInputStream()});
                Message responseMessage = putMediaInvoker.invoke(requestMessage);
                Object body = responseMessage.getBody();
                if (responseMessage.isFault()) {
                    if (body.getClass().getName().endsWith(".NotFoundException")) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    
    

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {

        // Authenticate the user
        String user = processAuthorizationHeader(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

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
        requestMessage.setBody(new Object[] {id});
        Message responseMessage = deleteInvoker.invoke(requestMessage);
        if (responseMessage.isFault()) {
            Object body = responseMessage.getBody();
            if (body.getClass().getName().endsWith(".NotFoundException")) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                throw new ServletException((Throwable)responseMessage.getBody());
            }
        }
    }

    
    
    /**
     * Process the authorization header
     * 
     * @param request
     * @return
     * @throws ServletException
     */
    private String processAuthorizationHeader(HttpServletRequest request) throws ServletException {

        // FIXME temporarily disabling this as it doesn't work with all browsers
        if (true)
            return "admin";

        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                StringTokenizer tokens = new StringTokenizer(authorization);
                if (tokens.hasMoreTokens()) {
                    String basic = tokens.nextToken();
                    if (basic.equalsIgnoreCase("Basic")) {
                        String credentials = tokens.nextToken();
                        String userAndPassword = new String(Base64.decodeBase64(credentials.getBytes()));
                        int colon = userAndPassword.indexOf(":");
                        if (colon != -1) {
                            String user = userAndPassword.substring(0, colon);
                            String password = userAndPassword.substring(colon + 1);

                            // Authenticate the User.
                            if (authenticate(user, password)) {
                                return user;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        return null;
    }

    /**
     * Authenticate a user.
     * 
     * @param user
     * @param password
     * @return
     */
    private boolean authenticate(String user, String password) {
        // TODO Handle this using SCA security policies
        return ("admin".equals(user) && "admin".equals(password));
    }

    /**
     * Reject an unauthorized request.
     * 
     * @param response
     */
    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate", "BASIC realm=\"Tuscany\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
