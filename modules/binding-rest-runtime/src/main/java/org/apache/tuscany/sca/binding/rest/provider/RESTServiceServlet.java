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
package org.apache.tuscany.sca.binding.rest.provider;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.rest.RESTBindingForJersey;
import org.apache.tuscany.sca.host.webapp.WebAppServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;


import com.sun.jersey.impl.container.servlet.ServletAdaptor;

import com.sun.jersey.tuscany.common.CommonDataStructures;
import com.sun.jersey.tuscany.common.CommonInterface;
/**
 * Servlet that handles REST requests invoking SCA services.
 * 
 * There is an instance of this Servlet for each <binding.rest>
 */
public class RESTServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    transient Binding binding;
    transient String serviceName;
    transient Object serviceInstance;
    transient RuntimeComponentService componentService;
    transient InterfaceContract serviceContract;
    transient Class<?> serviceInterface;
    
	private ServletConfig servletConfig;
	private ServletContext servletContext;
    
    private static ServletAdaptor jerseyServletContainer;

    public RESTServiceServlet(Binding binding,
                                 RuntimeComponentService componentService,
                                 InterfaceContract serviceContract,
                                 Class<?> serviceInterface,
                                 Object serviceInstance) {
        this.binding = binding;
        this.serviceName = binding.getName();
        this.componentService = componentService;
        this.serviceContract = serviceContract;
        this.serviceInterface = serviceInterface;
        this.serviceInstance = serviceInstance;
        
        //The interface name will be stored in a HashMap in the RESTBindingForJersey class along with a
		//reference to this servlet
        RESTBindingForJersey.updateInterfacenameServletMap(serviceInterface.getSimpleName(), this);
        
    }

   
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) {
    	try{
			
			servletConfig = config;	        
	        servletContext = servletConfig.getServletContext();
	        String contextPath = servletContext.getContextPath();
			RESTBindingForJersey.setContextPath(contextPath);
			
			//The following code has been moved to the host-webapp modlue. 
			//(org.apache.tuscany.sca.host.webapp.WebAppServletHost)
			
			//jerseyServletContainer.init(config);
			
			//Setting the reference below in CommonDataStructures for
			//com.sun.jersey.impl.model.method.dispatch.EntityParamDispatchProvider
			//to be able to invoke the method returnResult() method.
			CommonInterface commonInterface = new RESTBindingForJersey();
			CommonDataStructures.setCommonInterface(commonInterface);
			
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	try{
    		jerseyServletContainer = WebAppServletHost.getJerseyServletContainer();
			jerseyServletContainer.service(request, response);			
		}catch(ServletException e){
			e.printStackTrace(System.out);
		}
    }
    
	public Binding getBinding() {
		return binding;
	}


	public RuntimeComponentService getComponentService() {
		return componentService;
	}


	public InterfaceContract getServiceContract() {
		return serviceContract;
	}


	public static void setJerseyServletContainer(
			ServletAdaptor jerseyServletContainer) {
		RESTServiceServlet.jerseyServletContainer = jerseyServletContainer;
	}
    
}
