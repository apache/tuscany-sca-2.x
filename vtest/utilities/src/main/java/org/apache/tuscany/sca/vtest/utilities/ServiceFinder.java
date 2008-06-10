/*
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

package org.apache.tuscany.sca.vtest.utilities;

import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * 
 * 
 */
public class ServiceFinder {

    private static SCADomain domain;

    protected ServiceFinder() {
        super();
    }

    public static void init(String compositeFileName) {
        if (domain != null)
            System.out.println("VTEST WARNING: domain already exists and is will be overwritten!");
        domain = SCADomain.newInstance(compositeFileName);
    } 
    
    
    public static <B> B getService(Class<B> businessInterface, String serviceName) {
        return domain.getService(businessInterface, serviceName);
    }

    public static void cleanup() {
        domain.close();
        domain = null;
    }

    private static String getUri(String component, String service, String binding) {
        
    	List<ComponentService> csList = domain.getComponentManager().getComponent(component).getServices();
    	
    	for (int i = 0; i < csList.size(); i++) {
    		ComponentService cs = csList.get(i);
    		if (service.equals(cs.getName())) {
        		List<Binding> bList = cs.getBindings();
        		for (int j = 0; j < bList.size(); j++) {
        			String bName = bList.get(j).getName();
        			if (bName.equals(binding)) {
            			String bUri  = bList.get(j).getURI();
            			System.out.println(component + "/" + service + "-> binding name: " + bName + ", uri: " + bUri);
            			return bUri;
        			}
        		}
    		}
    	}
    	return null;
    }
    
    public static Definition getWSDLDefinition(String component, String service) {
    	return getWSDLDefinition(component, service, service);
    }
    	
    public static Definition getWSDLDefinition(String component, String service, String binding) {

    	String uri = getUri(component, service, binding);

        if (uri == null)
    		return null;
    	
    	try {
    		WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
            wsdlReader.setFeature("javax.wsdl.verbose",false);
            wsdlReader.setFeature("javax.wsdl.importDocuments",true);
            return wsdlReader.readWSDL(uri + "?wsdl");
    	} catch (WSDLException e) {
    		e.printStackTrace(System.out);
    	}
    	return null;
    }


}
