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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.sca.binding.feed.collection.NotFoundException;
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

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.WireFeedOutput;

/**
 * A resource collection binding listener, implemented as a servlet and
 * registered in a servlet host provided by the SCA hosting runtime.
 */
public class FeedBindingListenerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final static Namespace APP_NS = Namespace.getNamespace("app", "http://purl.org/atom/app#");
    private final static Namespace ATOM_NS = Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom");

    private RuntimeWire wire;
    private Invoker getFeedInvoker;
    private Invoker getInvoker;
    private Invoker postInvoker;
    private Invoker postMediaInvoker;
    private Invoker putInvoker;
    private Invoker putMediaInvoker;
    private Invoker deleteInvoker;
    private MessageFactory messageFactory;
    private String feedType;

    /**
     * Constructs a new binding listener.
     * 
     * @param wire
     * @param messageFactory
     * @param feedType
     */
    public FeedBindingListenerServlet(RuntimeWire wire, MessageFactory messageFactory, String feedType) {
        this.wire = wire;
        this.messageFactory = messageFactory;
        this.feedType = feedType;

        // Get the invokers for the supported operations
        for (InvocationChain invocationChain : this.wire.getInvocationChains()) {
            String operationName = invocationChain.getSourceOperation().getName();
            if (operationName.equals("getFeed")) {
                getFeedInvoker = invocationChain.getHeadInvoker();
            } else if (operationName.equals("get")) {
                getInvoker = invocationChain.getHeadInvoker();
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
    }

    @Override
    public void init(ServletConfig config) {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // No authentication required for a get request

        // Get the request path
        String path = request.getPathInfo();

        // The feedType parameter is used to override what type of feed is going
        // to
        // be produced
        String requestFeedType = request.getParameter("feedType");
        if (requestFeedType == null)
            requestFeedType = feedType;

        System.out.println(">>> FeedEndPointServlet (" + requestFeedType + ") " + request.getRequestURI());

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
                outputter.setFormat(Format.getPrettyFormat());
                outputter.output(document, response.getWriter());

            } else if (path == null || path.length() == 0 || path.equals("/")) {

                // Return a feed containing the entries in the collection

                // Get the Feed from the service implementation
                Message requestMessage = messageFactory.createMessage();
                Message responseMessage = getFeedInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                Feed feed = (Feed)responseMessage.getBody();
                if (feed != null) {

                    // Write the Atom feed
                    response.setContentType("application/atom+xml; charset=utf-8");
                    feed.setFeedType(requestFeedType);
                    WireFeedOutput feedOutput = new WireFeedOutput();
                    try {
                        OutputStream output = response.getOutputStream();
                        feedOutput.output(feed, new PrintWriter(output));
                    } catch (FeedException e) {
                        throw new ServletException(e);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else if (path.startsWith("/")) {

                // Return a specific entry in the collection

                // Get the entry from the service implementation
                Message requestMessage = messageFactory.createMessage();
                String id = path.substring(1);
                requestMessage.setBody(new Object[] {id});
                Message responseMessage = getInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                Entry entry = responseMessage.getBody();

                // Write the Atom entry
                if (entry != null) {
                    response.setContentType("application/atom+xml; charset=utf-8");
                    try {
                        AtomEntryUtil.writeEntry(entry, feedType, response.getWriter());
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

                // Get the Feed from the service
                Message requestMessage = messageFactory.createMessage();
                Message responseMessage = getFeedInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                Feed feed = (Feed)responseMessage.getBody();
                if (feed != null) {

                    // Convert to an RSS feed
                    response.setContentType("application/rss+xml; charset=utf-8");
                    feed.setFeedType("atom_1.0");
                    SyndFeed syndFeed = new SyndFeedImpl(feed);
                    syndFeed.setFeedType(requestFeedType);
                    SyndFeedOutput syndOutput = new SyndFeedOutput();
                    try {
                        OutputStream output = response.getOutputStream();
                        syndOutput.output(syndFeed, new PrintWriter(output));
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
        String path = request.getPathInfo();

        if (path == null || path.length() == 0 || path.equals("/")) {
            Entry createdEntry = null;

            // Create a new Atom entry
            String contentType = request.getContentType();
            if (contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                Entry entry;
                try {
                    entry = AtomEntryUtil.readEntry(feedType, request.getReader());
                } catch (JDOMException e) {
                    throw new ServletException(e);
                } catch (FeedException e) {
                    throw new ServletException(e);
                }

                // Let the component implementation create it
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[] {entry});
                Message responseMessage = postInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
                createdEntry = responseMessage.getBody();

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
                createdEntry = responseMessage.getBody();
            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }

            // A new entry was created successfully
            if (createdEntry != null) {

                // Set location of the created entry in the Location header
                for (Object l : createdEntry.getOtherLinks()) {
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
                    AtomEntryUtil.writeEntry(createdEntry, feedType, response.getWriter());
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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Authenticate the user
        String user = processAuthorizationHeader(request);
        if (user == null) {
            unauthorized(response);
            return;
        }

        // Get the request path
        String path = request.getPathInfo();

        if (path != null && path.startsWith("/")) {
            String id = path.substring(1);
            Entry updatedEntry = null;

            // Update an Atom entry
            String contentType = request.getContentType();
            if (contentType.startsWith("application/atom+xml")) {

                // Read the entry from the request
                Entry entry;
                try {
                    entry = AtomEntryUtil.readEntry(feedType, request.getReader());
                } catch (JDOMException e) {
                    throw new ServletException(e);
                } catch (FeedException e) {
                    throw new ServletException(e);
                }

                // Let the component implementation create it
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[] {id, entry});
                Message responseMessage = putInvoker.invoke(requestMessage);
                if (responseMessage.isFault()) {
                    Object body = responseMessage.getBody();
                    if (body instanceof NotFoundException) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                } else {
                    updatedEntry = responseMessage.getBody();
                }

            } else if (contentType != null) {

                // Updated a media entry

                // Let the component implementation create the media entry
                Message requestMessage = messageFactory.createMessage();
                requestMessage.setBody(new Object[] {id, contentType, request.getInputStream()});
                Message responseMessage = putMediaInvoker.invoke(requestMessage);
                Object body = responseMessage.getBody();
                if (body instanceof NotFoundException) {
                    if (body instanceof NotFoundException) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        throw new ServletException((Throwable)responseMessage.getBody());
                    }
                } else {
                    updatedEntry = (Entry) body;
                }

            } else {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }

            // The entry was successfully updated
            if (updatedEntry != null) {

                // Write the updated Atom entry
                response.setContentType("application/atom+xml; charset=utf-8");
                try {
                    AtomEntryUtil.writeEntry(updatedEntry, feedType, response.getWriter());
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
        String path = request.getPathInfo();

        if (path.startsWith("/")) {
            String id = path.substring(1);

            // Delete a specific entry from the collection
            Message requestMessage = messageFactory.createMessage();
            requestMessage.setBody(new Object[] {id});
            Message responseMessage = deleteInvoker.invoke(requestMessage);
            if (responseMessage.isFault()) {
                Object body = responseMessage.getBody();
                if (body instanceof NotFoundException) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    throw new ServletException((Throwable)responseMessage.getBody());
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
