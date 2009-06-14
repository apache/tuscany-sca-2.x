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

package org.apache.tuscany.sca.binding.http.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.http.HTTPCacheContext;
import org.apache.tuscany.sca.binding.http.util.HTTPHeadersParser;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;

/**
 * Servlet responsible for dispatching HTTP requests to the
 * target component implementation.
 *
 * @version $Rev$ $Date$
 */
public class HTTPBindingListenerServlet extends HttpServlet {
    private static final long serialVersionUID = 2865466417329430610L;
    
    private static final QName AUTEHTICATION_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0","authentication");
    
    transient private Binding binding;

    transient private boolean requiresAuthentication = false;
    transient private BasicAuthenticationPolicy basicAuthenticationPolicy = null;

    private MessageFactory messageFactory;
    private Invoker getInvoker;
    private Invoker conditionalGetInvoker;
    private Invoker putInvoker;
    private Invoker conditionalPutInvoker;
    private Invoker postInvoker;
    private Invoker conditionalPostInvoker;
    private Invoker deleteInvoker;
    private Invoker conditionalDeleteInvoker;
    
    /**
     * Constructs a new HTTPServiceListenerServlet.
     */
    public HTTPBindingListenerServlet(Binding binding, MessageFactory messageFactory) {
        this.binding = binding;
        this.messageFactory = messageFactory;
        

        // find out which policies are active
        if (binding instanceof PolicySetAttachPoint) {
            List<Intent> intents = ((PolicySetAttachPoint)binding).getRequiredIntents();
            for(Intent intent : intents) {
                if(intent.getName().equals(AUTEHTICATION_INTENT)) {
                    requiresAuthentication = true;
                }
            }


            List<PolicySet> policySets = ((PolicySetAttachPoint)binding).getApplicablePolicySets();
            for (PolicySet ps : policySets) {
                for (Object p : ps.getPolicies()) {
                    if (BasicAuthenticationPolicy.class.isInstance(p)) {
                        basicAuthenticationPolicy = (BasicAuthenticationPolicy)p;
                    } else {
                        // etc. check for other types of policy being present
                    }
                }
            }
        }        
    }

    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(requiresAuthentication) {
            if(! hasAuthorization(request, response)) {
                response.setHeader("WWW-Authenticate", "BASIC realm=\"Tuscany\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        
        super.service(request, response);
    }    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();

        //store http headers to message
        requestMessage.getHeaders().addAll(HTTPHeadersParser.getHeaders(request));
        
        String id = path.substring(1);
        
        Message responseMessage = null;
        HTTPCacheContext cacheContext = null;
        try { 
           cacheContext = HTTPCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }

        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalGetInvoker != null )) {        
        	requestMessage.setBody(new Object[] {id, cacheContext});
        	responseMessage = conditionalGetInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {id});
        	responseMessage = getInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }
        		        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();        
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        String id = path.substring(1);
        
        Message responseMessage = null;
        HTTPCacheContext cacheContext = null;
        try { 
           cacheContext = HTTPCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }
        
        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalDeleteInvoker != null )) {        
        	requestMessage.setBody(new Object[] {id, cacheContext});
        	responseMessage = conditionalDeleteInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {id});
        	responseMessage = deleteInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }        		
        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();        
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        String id = path.substring(1);
        
        Message responseMessage = null;
        HTTPCacheContext cacheContext = null;
        try { 
           cacheContext = HTTPCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }
        
        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalPutInvoker != null )) {        
        	requestMessage.setBody(new Object[] {id, cacheContext});
        	responseMessage = conditionalPutInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {id});
        	responseMessage = putInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }        		
        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        // String id = path.substring(1);
        
        Message responseMessage = null;
        HTTPCacheContext cacheContext = null;
        try { 
           cacheContext = HTTPCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }
        
        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalPostInvoker != null )) {        
        	requestMessage.setBody(new Object[] {cacheContext});
        	responseMessage = conditionalPostInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {});
        	responseMessage = postInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }        		
        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }


        // Test if the ETag and LastModified are returned as a cache context.
    	Object body = responseMessage.getBody();
    	if ( body.getClass() == HTTPCacheContext.class ) {
    		// Transfer to header if so.
    		HTTPCacheContext cc = (HTTPCacheContext)responseMessage.getBody();
    		if (( cc != null ) && ( cc.isEnabled() )) {
    			String eTag = cc.getETag();
            	if ( eTag != null )
            		response.setHeader( "ETag", cc.getETag() );
            	String lastModified = cc.getLastModified();
            	if ( lastModified != null)
            		response.setHeader( "LastModified", cc.getLastModified() );
    		}
    	}
    }

    /**
     * @return the getInvoker
     */
    public Invoker getGetInvoker() {
        return getInvoker;
    }

    /**
     * @param getInvoker the getInvoker to set
     */
    public void setGetInvoker(Invoker getInvoker) {
        this.getInvoker = getInvoker;
    }

    /**
     * @return the conditionalGetInvoker
     */
    public Invoker getConditionalGetInvoker() {
        return conditionalGetInvoker;
    }

    /**
     * @param conditionalGetInvoker the conditionalGetInvoker to set
     */
    public void setConditionalGetInvoker(Invoker conditionalGetInvoker) {
        this.conditionalGetInvoker = conditionalGetInvoker;
    }

    /**
     * @return the putInvoker
     */
    public Invoker getPutInvoker() {
        return putInvoker;
    }

    /**
     * @param putInvoker the putInvoker to set
     */
    public void setPutInvoker(Invoker putInvoker) {
        this.putInvoker = putInvoker;
    }

    /**
     * @return the conditionalPutInvoker
     */
    public Invoker getConditionalPutInvoker() {
        return conditionalPutInvoker;
    }

    /**
     * @param conditionalPutInvoker the conditionalPutInvoker to set
     */
    public void setConditionalPutInvoker(Invoker conditionalPutInvoker) {
        this.conditionalPutInvoker = conditionalPutInvoker;
    }

    /**
     * @return the postInvoker
     */
    public Invoker getPostInvoker() {
        return postInvoker;
    }

    /**
     * @param postInvoker the postInvoker to set
     */
    public void setPostInvoker(Invoker postInvoker) {
        this.postInvoker = postInvoker;
    }

    /**
     * @return the conditionalPostInvoker
     */
    public Invoker getConditionalPostInvoker() {
        return conditionalPostInvoker;
    }

    /**
     * @param conditionalPostInvoker the conditionalPostInvoker to set
     */
    public void setConditionalPostInvoker(Invoker conditionalPostInvoker) {
        this.conditionalPostInvoker = conditionalPostInvoker;
    }

    /**
     * @return the deleteInvoker
     */
    public Invoker getDeleteInvoker() {
        return deleteInvoker;
    }

    /**
     * @param deleteInvoker the deleteInvoker to set
     */
    public void setDeleteInvoker(Invoker deleteInvoker) {
        this.deleteInvoker = deleteInvoker;
    }

    /**
     * @return the conditionalDeleteInvoker
     */
    public Invoker getConditionalDeleteInvoker() {
        return conditionalDeleteInvoker;
    }

    /**
     * @param conditionalDeleteInvoker the conditionalDeleteInvoker to set
     */
    public void setConditionalDeleteInvoker(Invoker conditionalDeleteInvoker) {
        this.conditionalDeleteInvoker = conditionalDeleteInvoker;
    }

    
    /** 
     * Utility Methods related to Policy
     */
    

    private boolean hasAuthorization(HttpServletRequest request, ServletResponse response) {
        boolean result = false;
        if(request.getHeader("Authorization") != null) {
            result = true;
        }
        
        return result;
    }


}
