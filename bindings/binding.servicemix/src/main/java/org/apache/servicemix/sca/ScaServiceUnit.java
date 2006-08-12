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
package org.apache.servicemix.sca;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;

import org.apache.servicemix.common.ServiceUnit;
import org.apache.servicemix.sca.assembly.JbiBinding;
import org.apache.servicemix.sca.tuscany.CommonsLoggingMonitorFactory;
import org.apache.servicemix.sca.tuscany.TuscanyRuntime;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;

public class ScaServiceUnit extends ServiceUnit {

	protected static final ThreadLocal<ScaServiceUnit> SERVICE_UNIT = new ThreadLocal<ScaServiceUnit>();
	
	public static ScaServiceUnit getCurrentScaServiceUnit() {
		return SERVICE_UNIT.get();
	}
	
	protected TuscanyRuntime tuscanyRuntime;
	protected ClassLoader classLoader;
	
	public void init() throws Exception {
        SERVICE_UNIT.set(this);
		createScaRuntime();
		createEndpoints();
        SERVICE_UNIT.set(null);
	}
	
	protected void createScaRuntime() throws Exception {
		File root = new File(getRootPath());
		File[] files = root.listFiles(new JarFileFilter());
		URL[] urls = new URL[files.length + 1];
		for (int i = 0; i < files.length; i++) {
			urls[i] = files[i].toURL();
		}
		urls[urls.length - 1] = root.toURL();
		classLoader = new URLClassLoader(urls, getClass().getClassLoader());
		
        tuscanyRuntime = new TuscanyRuntime(getName(), getRootPath(), classLoader, new CommonsLoggingMonitorFactory());
	}
	
	protected void createEndpoints() throws Exception {
        Module module = tuscanyRuntime.getModuleComponent().getModuleImplementation();
        for (Iterator i = module.getEntryPoints().iterator(); i.hasNext();) {
            EntryPoint entryPoint = (EntryPoint) i.next();
            Binding binding = (Binding) entryPoint.getBindings().get(0);
            if (binding instanceof JbiBinding) {
                JbiBinding jbiBinding = (JbiBinding) binding;
                ScaEndpoint endpoint = new ScaEndpoint(entryPoint);
                endpoint.setServiceUnit(this);
                endpoint.setService(jbiBinding.getServiceName());
                endpoint.setEndpoint(jbiBinding.getEndpointName());
                endpoint.setInterfaceName(jbiBinding.getInterfaceName());
                Definition definition = jbiBinding.getDefinition();
                if (definition != null) {
                    endpoint.setDefinition(definition);
                    endpoint.setDescription(WSDLFactory.newInstance().newWSDLWriter().getDocument(definition));
                }
                addEndpoint(endpoint);
            }
        }
	}
	
	private static class JarFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
	}

	public TuscanyRuntime getTuscanyRuntime() {
		return tuscanyRuntime;
	}

	@Override
	public void start() throws Exception {
	    tuscanyRuntime.start();
		super.start();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		tuscanyRuntime.stop();
	}

}
