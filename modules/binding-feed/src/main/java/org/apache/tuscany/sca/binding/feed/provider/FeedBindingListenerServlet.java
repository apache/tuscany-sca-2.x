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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.sca.data.collection.Item;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.WireFeedOutput;

/**
 * A resource collection binding listener, implemented as a Servlet and
 * registered in a Servlet host provided by the SCA hosting runtime.
 *
 * @version $Rev$ $Date$
 */
class FeedBindingListenerServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FeedBindingListenerServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private static final Namespace APP_NS = Namespace.getNamespace("app", "http://purl.org/atom/app#");
    private static final Namespace ATOM_NS = Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom");

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
    private String feedType;
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
    FeedBindingListenerServlet(RuntimeWire wire, MessageFactory messageFactory, Mediator mediator, String feedType) {
        this.wire = wire;
        this.messageFactory = messageFactory;
        this.mediator = mediator;
        this.feedType = feedType;

        // Get the invokers for the supported operations
        Operation getOperation = null;
        for (InvocationChain invocationChain : this.wire.getInvocationChains()) {
            invocationChain.setAllowsPassByReference(true);
            Operation operation = invocationChain.getTargetOperation();
            String operationName = operation.getName();
            if (operationName.equals("getFeed")) {
                getFeedInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("getAll")) {
                getAllInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("query")) {
                queryInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("get")) {
                getInvoker = invocationChain.getHeadInvoker();
                getOperation = operation;
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

        // Determine the collection item type
        itemXMLType = new DataTypeImpl<Class<?>>(String.class.getName(), String.class, String.class);
        Class<?> itemClass = getOperation.getOutputType().getPhysical();
        if (itemClass == Entry.class) {
            supportsFeedEntries = true;
        }
        DataType<XMLType> outputType = getOperation.getOutputType();
        QName qname = outputType.getLogical().getElementName();
        qname = new QName(qname.getNamespaceURI(), itemClass.getSimpleName());
        itemClassType = new DataTypeImpl<XMLType>("java:complexType", itemClass, new XMLType(qname, null));
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // No authentication required for a get request

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        // The feedType parameter is used to override what type of feed is going
        // to be produced
        String requestFeedType = request.getParameter("feedType");
        if (requestFeedType == null)
            requestFeedType = feedType;

        logger.info(">>> FeedEndPointServlet (" + requestFeedType + ") " + request.getRequestURI());

        // Handle an Atom request
        if (requestFeedType.startsWith("atom_")) {

            if (path != null && path.equals("/atomsvc")) {

                // Return the Atom service document
                response.setContentType("application/atomsvc+xml; charset=utf-8");
                Document document = new Document();
                Element service = new Element("service", APP_NS);
                document.setRootElement(service);

                Element workspace = new Element("workspace", APP_NS);
                Element title = new Element("title", ATOM_NS);
                title.setText("resource");
                workspace.addContent(title);
                service.addContent(workspace);

                Element collection = new Element("collection", APP_NS);
                String href = request.getRequestURL().toString();
                href = href.substring(0, href.length() - "/atomsvc".length());
                collection.setAttribute("href", href);
                Element collectionTitle = new Element("title", ATOM_NS);
                collectionTitle.setText("entries");
                collection.addContent(collectionTitle);
                workspace.addContent(collection);

                XMLOutputter outputter = new XMLOutputter();
                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                outputter.setFormat(format);
                outputter.output(document, getWriter(response));

            } else if (path == null || path.length() == 0 || path.equals("/")) {

                // Return a feed containing the entries in the collection
                Feed feed = null;
                if (supportsFeedEntries) {

                    // The service implementation supports feed entries, invoke its getFeed operation
                    Message requestMessage = messageFactory.createMessage();
                    Message responseMessage = getFeedInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    feed = (Feed)responseMessage.getBody();
                    
                } else {

                    // The service implementation does not support feed entries,
                    // invoke its getAll operation to get the data item collection, then create
                    // feed entries from the items
                    Message requestMessage = messageFactory.createMessage();
                    Message responseMessage;
                    if (request.getQueryString() != null) {
                        requestMessage.setBody(new Object[] {request.getQueryString()});
                        responseMessage = queryInvoker.invoke(requestMessage);
                    } else {
                        responseMessage = getAllInvoker.invoke(requestMessage);
                    }
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    org.apache.tuscany.sca.data.collection.Entry<Object, Object>[] collection =
                        (org.apache.tuscany.sca.data.collection.Entry<Object, Object>[])responseMessage.getBody();
                    if (collection != null) {
                        // Create the feed
                        feed = new Feed();
                        feed.setTitle("Feed");
                        for (org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry: collection) {
                            Entry feedEntry = createFeedEntry(entry);
                            feed.getEntries().add(feedEntry);
                        }
                    }
                }
                if (feed != null) {
                    
                    // Write the Atom feed
                    response.setContentType("application/atom+xml; charset=utf-8");
                    feed.setFeedType(requestFeedType);
                    WireFeedOutput feedOutput = new WireFeedOutput();
                    try {
                        feedOutput.output(feed, getWriter(response));
                    } catch (FeedException e) {
                        throw new ServletException(e);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                
            } else if (path.startsWith("/")) {

                // Return a specific entry in the collection
                Entry feedEntry;

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
                    feedEntry = responseMessage.getBody();
                    
                } else {
                    
                    // The service implementation only returns a data item, create an entry
                    // from it
                    feedEntry = createFeedEntry(new org.apache.tuscany.sca.data.collection.Entry<Object, Object>(id, responseMessage.getBody()));
                }

                // Write the Atom entry
                if (feedEntry != null) {
                    response.setContentType("application/atom+xml; charset=utf-8");
                    try {
                        AtomFeedEntryUtil.writeFeedEntry(feedEntry, feedType, getWriter(response));
                    } catch (FeedException e) {
                        throw new ServletException(e);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            } else {

                // Path doesn't match any known pattern
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {

            // Handle an RSS request
            if (path == null || path.length() == 0 || path.equals("/")) {

                // Return an RSS feed containing the entries in the collection
                Feed feed = null;
                if (supportsFeedEntries) {

                    // The service implementation supports feed entries, invoke its getFeed operation
                    Message requestMessage = messageFactory.createMessage();
                    Message responseMessage = getFeedInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    feed = (Feed)responseMessage.getBody();
                    
                } else {

                    // The service implementation does not support feed entries, invoke its
                    // getAll operation to get the data item collection. then create feed entries
                    // from the data items
                    Message requestMessage = messageFactory.createMessage();
                    Message responseMessage;
                    if (request.getQueryString() != null) {
                        requestMessage.setBody(new Object[] {request.getQueryString()});
                        responseMessage = queryInvoker.invoke(requestMessage);
                    } else {
                        responseMessage = getAllInvoker.invoke(requestMessage);
                    }
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    org.apache.tuscany.sca.data.collection.Entry<Object, Object>[] collection =
                        (org.apache.tuscany.sca.data.collection.Entry<Object, Object>[])responseMessage.getBody();
                    if (collection != null) {
                        // Create the feed
                        feed = new Feed();
                        feed.setTitle("Feed");
                        for (org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry: collection) {
                            Entry feedEntry = createFeedEntry(entry);
                            feed.getEntries().add(feedEntry);
                        }
                    }
                }

                // Convert to an RSS feed
                if (feed != null) {
                    response.setContentType("application/rss+xml; charset=utf-8");
                    feed.setFeedType("atom_1.0");
                    SyndFeed syndFeed = new SyndFeedImpl(feed);
                    syndFeed.setFeedType(requestFeedType);
                    syndFeed.setLink(path);
                    SyndFeedOutput syndOutput = new SyndFeedOutput();
                    try {
                        syndOutput.output(syndFeed, getWriter(response));
                    } catch (FeedException e) {
                        throw new ServletException(e);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    /**
     * Create an Atom entry from a data collection entry.
     * @param entry 
     * @return
     */
    private Entry createFeedEntry(org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry) {
        Object key = entry.getKey();
        Object data = entry.getData();
        if (data instanceof Item) {
            Item item = (Item)data;
            
            Entry feedEntry = new Entry();
            feedEntry.setId(key.toString());
            feedEntry.setTitle(item.getTitle());
    
            String value = item.getContents();
            if (value != null) {
                Content content = new Content();
                content.setType("text/xml");
                content.setValue(value);
                List<Content> contents = new ArrayList<Content>();
                contents.add(content);
                feedEntry.setContents(contents);
            }
    
            String href = item.getLink();
            if (href == null) {
                href = key.toString();
            }
            Link link = new Link();
            link.setRel("edit");
            link.setHref(href);
            feedEntry.getOtherLinks().add(link);
            link = new Link();
            link.setRel("alternate");
            link.setHref(href);
            feedEntry.getAlternateLinks().add(link);
    
            Date date = item.getDate();
            if (date == null) {
                date = new Date();
            }
            feedEntry.setCreated(date);
            return feedEntry;
            
        } else if (data != null) {
            Entry feedEntry = new Entry();
            feedEntry.setId(key.toString());
            feedEntry.setTitle("item");
    
            // Convert the item to XML
            String value = mediator.mediate(data, itemClassType, itemXMLType, null).toString();
            
            Content content = new Content();
            content.setType("text/xml");
            content.setValue(value);
            List<Content> contents = new ArrayList<Content>();
            contents.add(content);
            feedEntry.setContents(contents);
    
            Link link = new Link();
            link.setRel("edit");
            link.setHref(key.toString());
            feedEntry.getOtherLinks().add(link);
            link = new Link();
            link.setRel("alternate");
            link.setHref(key.toString());
            feedEntry.getAlternateLinks().add(link);
    
            feedEntry.setCreated(new Date());
            return feedEntry;
        } else {
            return null;
        }
    }

    /**
     * Create a data collection entry from an Atom entry.
     * @param feedEntry
     * @return
     */
    private org.apache.tuscany.sca.data.collection.Entry<Object, Object> createEntry(Entry feedEntry) {
        if (feedEntry != null) {
            if (itemClassType.getPhysical() == Item.class) {
                String key = feedEntry.getId();
                
                Item item = new Item();
                item.setTitle(feedEntry.getTitle());
                
                List<?> contents = feedEntry.getContents();
                if (!contents.isEmpty()) {
                    Content content = (Content)contents.get(0);
                    String value = content.getValue();
                    item.setContents(value);
                }
                
                for (Object l : feedEntry.getOtherLinks()) {
                    Link link = (Link)l;
                    if (link.getRel() == null || "edit".equals(link.getRel())) {
                        String href = link.getHref();
                        if (href.startsWith("null/")) {
                            href = href.substring(5);
                        }
                        item.setLink(href);
                        break;
                    }
                }
                
                item.setDate(feedEntry.getCreated());
                
                return new org.apache.tuscany.sca.data.collection.Entry<Object, Object>(key, item);
                
            } else {
                String key = feedEntry.getId();
                
                // Create the item from XML
                List<?> contents = feedEntry.getContents();
                if (contents.isEmpty()) {
                    return null;
                }
                Content content = (Content)contents.get(0);
                String value = content.getValue();
                Object data = mediator.mediate(value, itemXMLType, itemClassType, null);

                return new org.apache.tuscany.sca.data.collection.Entry<Object, Object>(key, data);
            }
        } else {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {

        // Authenticate the user
        String user = processAuthorizationHeader(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        if (path == null || path.length() == 0 || path.equals("/")) {
            Entry createdFeedEntry = null;

            // Create a new Atom entry
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                Entry feedEntry;
                try {
                    feedEntry = AtomFeedEntryUtil.readFeedEntry(feedType, request.getReader());
                } catch (JDOMException e) {
                    throw new ServletException(e);
                } catch (FeedException e) {
                    throw new ServletException(e);
                }

                // Let the component implementation create it
                if (supportsFeedEntries) {
                    
                    // The service implementation supports feed entries, pass the entry to it
                    Message requestMessage = messageFactory.createMessage();
                    requestMessage.setBody(new Object[] {feedEntry});
                    Message responseMessage = postInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    createdFeedEntry = responseMessage.getBody();
                } else {
                    
                    // The service implementation does not support feed entries, pass the data item to it
                    Message requestMessage = messageFactory.createMessage();
                    org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry = createEntry(feedEntry);
                    requestMessage.setBody(new Object[] {entry.getKey(), entry.getData()});
                    Message responseMessage = postInvoker.invoke(requestMessage);
                    if (responseMessage.isFault()) {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                    entry.setKey(responseMessage.getBody());
                    createdFeedEntry = createFeedEntry(entry);
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
                for (Object l : createdFeedEntry.getOtherLinks()) {
                    Link link = (Link)l;
                    if (link.getRel() == null || "edit".equals(link.getRel())) {
                        response.addHeader("Location", link.getHref());
                        break;
                    }
                }

                // Write the created Atom entry
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/atom+xml; charset=utf-8");
                try {
                    AtomFeedEntryUtil.writeFeedEntry(createdFeedEntry, feedType, getWriter(response));
                } catch (FeedException e) {
                    throw new ServletException(e);
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
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        if (path != null && path.startsWith("/")) {
            String id = path.substring(1);

            // Update an Atom entry
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                Entry feedEntry;
                try {
                    feedEntry = AtomFeedEntryUtil.readFeedEntry(feedType, request.getReader());
                } catch (JDOMException e) {
                    throw new ServletException(e);
                } catch (FeedException e) {
                    throw new ServletException(e);
                }

                // Let the component implementation create it
                if (supportsFeedEntries) {
                    
                    // The service implementation supports feed entries, pass the entry to it
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
                    
                    // The service implementation does not support feed entries, pass the data item to it
                    Message requestMessage = messageFactory.createMessage();
                    org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry = createEntry(feedEntry);
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
        //FIXME Why are we using endsWith instead of equals here??
        return ("admin".endsWith(user) && "admin".equals(password));
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
