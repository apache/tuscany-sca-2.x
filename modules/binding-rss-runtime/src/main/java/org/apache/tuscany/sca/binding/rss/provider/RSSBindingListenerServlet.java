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
package org.apache.tuscany.sca.binding.rss.provider;

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

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * An RSS binding listener, implemented as a Servlet and
 * registered in a Servlet host provided by the SCA hosting runtime.
 *
 * @version $Rev$ $Date$
 */
class RSSBindingListenerServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RSSBindingListenerServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private RuntimeWire wire;
    private Invoker getFeedInvoker;
    private Invoker getAllInvoker;
    private Invoker queryInvoker;
    private MessageFactory messageFactory;
    private Mediator mediator;
    private DataType<?> itemClassType;
    private DataType<?> itemXMLType;
    private boolean supportsFeedEntries;

    /**
     * Constructs a new binding listener.
     * 
     * @param wire
     * @param messageFactory
     */
    RSSBindingListenerServlet(RuntimeWire wire, MessageFactory messageFactory, Mediator mediator) {
        this.wire = wire;
        this.messageFactory = messageFactory;
        this.mediator = mediator;

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
                getOperation = operation;
            }
        }

        // Determine the collection item type
        if (getOperation != null) {
            itemXMLType = new DataTypeImpl<Class<?>>(String.class.getName(), String.class, String.class);
            Class<?> itemClass = getOperation.getOutputType().getPhysical();
            if (itemClass == SyndEntry.class) {
                supportsFeedEntries = true;
            }
            DataType<XMLType> outputType = getOperation.getOutputType();
            QName qname = outputType.getLogical().getElementName();
            qname = new QName(qname.getNamespaceURI(), itemClass.getSimpleName());
            itemClassType = new DataTypeImpl<XMLType>("java:complexType", itemClass, new XMLType(qname, null));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // No authentication required for a get request

        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");

        logger.fine("get " + request.getRequestURI());

        // Handle an RSS request
        if (path == null || path.length() == 0 || path.equals("/")) {

            // Return an RSS feed containing the entries in the collection
            SyndFeed feed = null;
            if (supportsFeedEntries) {

                // The service implementation supports feed entries, invoke its getFeed operation
                Message requestMessage = messageFactory.createMessage();
                Message responseMessage = getFeedInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                feed = (SyndFeed)responseMessage.getBody();
                
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
                    feed = new SyndFeedImpl();
                    feed.setTitle("Feed");
                    feed.setDescription("Feed description");
                    
                    for (org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry: collection) {
                        SyndEntry feedEntry = createFeedEntry(entry);
                        feed.getEntries().add(feedEntry);
                    }
                }
            }

            // Convert to an RSS feed
            if (feed != null) {
                response.setContentType("application/rss+xml; charset=utf-8");
                feed.setFeedType("rss_2.0");
                feed.setLink(path);
                SyndFeedOutput syndOutput = new SyndFeedOutput();
                try {
                    syndOutput.output(feed, getWriter(response));
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

    /**
     * Create an RSS entry from a data collection entry.
     * @param entry 
     * @return
     */
    private SyndEntry createFeedEntry(org.apache.tuscany.sca.data.collection.Entry<Object, Object> entry) {
        Object key = entry.getKey();
        Object data = entry.getData();
        if (data instanceof Item) {
            Item item = (Item)data;
            
            SyndEntry feedEntry = new SyndEntryImpl();
            feedEntry.setUri(key.toString());
            feedEntry.setTitle(item.getTitle());
    
            String value = item.getContents();
            if (value != null) {
                SyndContent content = new SyndContentImpl();
                content.setType("text/xml");
                content.setValue(value);
                List<SyndContent> contents = new ArrayList<SyndContent>();
                contents.add(content);
                feedEntry.setContents(contents);
            }
    
            String href = item.getLink();
            if (href == null) {
                href = key.toString();
            }
            SyndLink link = new SyndLinkImpl();
            link.setRel("edit");
            link.setHref(href);
            feedEntry.getLinks().add(link);
            link = new SyndLinkImpl();
            link.setRel("alternate");
            link.setHref(href);
            feedEntry.getLinks().add(link);
            feedEntry.setLink(href);
    
            Date date = item.getDate();
            if (date == null) {
                date = new Date();
            }
            feedEntry.setPublishedDate(date);
            return feedEntry;
            
        } else if (data != null) {
            SyndEntry feedEntry = new SyndEntryImpl();
            feedEntry.setUri(key.toString());
            feedEntry.setTitle("item");
    
            // Convert the item to XML
            String value = mediator.mediate(data, itemClassType, itemXMLType, null).toString();
            
            SyndContent content = new SyndContentImpl();
            content.setType("text/xml");
            content.setValue(value);
            List<SyndContent> contents = new ArrayList<SyndContent>();
            contents.add(content);
            feedEntry.setContents(contents);
    
            SyndLink link = new SyndLinkImpl();
            link.setRel("edit");
            link.setHref(key.toString());
            feedEntry.getLinks().add(link);
            link = new SyndLinkImpl();
            link.setRel("alternate");
            link.setHref(key.toString());
            feedEntry.getLinks().add(link);
    
            feedEntry.setPublishedDate(new Date());
            return feedEntry;
        } else {
            return null;
        }
    }

    /**
     * Create a data collection entry from an RSS entry.
     * @param feedEntry
     * @return
     */
    private org.apache.tuscany.sca.data.collection.Entry<Object, Object> createEntry(SyndEntry feedEntry) {
        if (feedEntry != null) {
            if (itemClassType.getPhysical() == Item.class) {
                String key = feedEntry.getUri();
                
                Item item = new Item();
                item.setTitle(feedEntry.getTitle());
                
                List<?> contents = feedEntry.getContents();
                if (!contents.isEmpty()) {
                    SyndContent content = (SyndContent)contents.get(0);
                    String value = content.getValue();
                    item.setContents(value);
                }
                
                for (Object l : feedEntry.getLinks()) {
                    SyndLink link = (SyndLink)l;
                    if (link.getRel() == null || "edit".equals(link.getRel())) {
                        String href = link.getHref();
                        if (href.startsWith("null/")) {
                            href = href.substring(5);
                        }
                        item.setLink(href);
                        break;
                    }
                }
                
                item.setDate(feedEntry.getPublishedDate());
                
                return new org.apache.tuscany.sca.data.collection.Entry<Object, Object>(key, item);
                
            } else {
                String key = feedEntry.getUri();
                
                // Create the item from XML
                List<?> contents = feedEntry.getContents();
                if (contents.isEmpty()) {
                    return null;
                }
                SyndContent content = (SyndContent)contents.get(0);
                String value = content.getValue();
                Object data = mediator.mediate(value, itemXMLType, itemClassType, null);

                return new org.apache.tuscany.sca.data.collection.Entry<Object, Object>(key, data);
            }
        } else {
            return null;
        }
    }


    private Writer getWriter(HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        return writer;
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
