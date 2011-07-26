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

package org.apache.tuscany.sca.core.assembly.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import javax.wsdl.PortType;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException;

public class WSDLHelper {

    /**
     * This creates a WSDLInterfaceContract from a WSDL document
     * TODO: Presently this writes the wsdl string to a temporary file which is then used by the Tuscany contribution
     * code to turn the wsdl into the correctly populated Tuscany model objects. There must/should be a way to have
     * that happen without needing the external file but i've not been able to find the correct configuration to 
     * get that to happen with all the schema objects created correctly. 
     */
    public static WSDLInterfaceContract createWSDLInterfaceContract(ExtensionPointRegistry registry, String wsdl) {
        File wsdlFile = null;
        try {
            
            wsdlFile = writeToFile(wsdl);
            System.out.println("wsdl: " + wsdlFile);

            FactoryExtensionPoint fep = registry.getExtensionPoint(FactoryExtensionPoint.class);
            URLArtifactProcessorExtensionPoint apep = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            ExtensibleURLArtifactProcessor aproc = new ExtensibleURLArtifactProcessor(apep);
            ProcessorContext ctx = new ProcessorContext();
            
            ContributionFactory cf = fep.getFactory(ContributionFactory.class);
            final Contribution c = cf.createContribution();
            c.setURI("temp");
            c.setLocation(wsdlFile.toURI().toURL().toString());
            c.setModelResolver(new ExtensibleModelResolver(c, registry.getExtensionPoint(ModelResolverExtensionPoint.class), fep));
            
            WSDLDefinition wd = aproc.read(null, new URI("temp.wsdl"), wsdlFile.toURI().toURL(), ctx, WSDLDefinition.class);
            c.getModelResolver().addModel(wd, ctx);
            c.getModelResolver().resolveModel(WSDLDefinition.class, wd, ctx);
            PortType pt = (PortType)wd.getDefinition().getAllPortTypes().values().iterator().next();
            
            WSDLFactory wsdlFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(WSDLFactory.class);
            WSDLInterface nwi = wsdlFactory.createWSDLInterface(pt, wd, c.getModelResolver(), null);
            nwi.setWsdlDefinition(wd);
            WSDLInterfaceContract wsdlIC = wsdlFactory.createWSDLInterfaceContract();
            wsdlIC.setInterface(nwi);
            
            wsdlFile.delete();
            
            return wsdlIC;

        } catch (InvalidWSDLException e) {
            //* TODO: Also, this doesn't seem to work reliably and sometimes the schema objects don't get built correctly
            //* org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException: Element cannot be resolved: {http://sample/}sayHello
            //*         at org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLOperationIntrospectorImpl$WSDLPart.<init>(WSDLOperationIntrospectorImpl.java:276)
            //* It seems like it works ok for me with IBM JDK but not with a Sun one        
            // I'm still trying to track this down but committing like this to see if anyone has any ideas 
            e.printStackTrace();
            return null;
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (wsdlFile != null) {
                wsdlFile.delete();
            }
        }
    }
    
    private static File writeToFile(String wsdl) throws FileNotFoundException, IOException {
        File f = File.createTempFile("endpoint", ".wsdl");
        Writer out = new OutputStreamWriter(new FileOutputStream(f));
        try {
          out.write(wsdl);
        }
        finally {
          out.close();
        }
        return f;
    }
}
