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

package org.apache.tuscany.sca.implementation.bpel.impl;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.wsdl.PortType;

import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;

import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.implementation.bpel.xml.BPELImportElement;
import org.apache.tuscany.sca.implementation.bpel.xml.BPELPartnerLinkElement;

/**
 * The BPEL process definition implementation.
 * 
 * @version $Rev$ $Date$
 */
public class BPELProcessDefinitionImpl implements BPELProcessDefinition {
    private QName   name;
    private URI     uri;
    private URL     location;
    private boolean unresolved;
    private List<BPELPartnerLinkElement> partnerLinks = new ArrayList<BPELPartnerLinkElement>();
    private List<BPELImportElement> imports = new ArrayList<BPELImportElement>();
    private Collection<PortType> thePortTypes = null;
    private Collection<WSDLInterface> theInterfaces = null;
    
    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public URI getURI() {
        return uri;
    }
    
    public void setURI(URI uri) {
        this.uri = uri;
    }
    
    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location; 
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }  
    
    /**
     * Return the list of PartnerLinks for this process
     * @return List<BPELPartnerLinkElement> the list of Partner Links
     */
    public List<BPELPartnerLinkElement> getPartnerLinks() {
    	return partnerLinks;
    }
    
    /**
     * Return the list of imports for this process
     * @return List<BPELImportElement> the list of Import elements
     */
    public List<BPELImportElement> getImports() {
    	return imports;
    }
    
    public void setPortTypes( Collection<PortType> thePortTypes ) {
    	this.thePortTypes = thePortTypes;
    }
    
    public Collection<PortType> getPortTypes() {
    	return thePortTypes;
    }
    
    /**
     * Set the associated collection of WSDL interfaces
     * @param theInterfaces
     */
    public void setInterfaces( Collection<WSDLInterface> theInterfaces ) {
    	this.theInterfaces = theInterfaces;
    } // end setInterfaces
    
    /**
     * Return the collection of associated WSDL interfaces
     * @return
     */
    public Collection<WSDLInterface> getInterfaces() {
    	return theInterfaces;
    } // end getInterfaces
    
    public void compile() {
        /*
        String bpelFile = reader.getAttributeValue(null, "file");  // FIXME: 

        // Resolving the BPEL file and compiling it
        URL bpelURL = getClass().getClassLoader().getResource(bpelFile);
        if (bpelURL == null)
            throw new ODEProcessException("Couldn't find referenced bpel file " + bpelFile);
        BpelC bpelc = BpelC.newBpelCompiler();
        ByteArrayOutputStream compiledProcess = new ByteArrayOutputStream();
        bpelc.setOutputStream(compiledProcess);
        try {
            bpelc.compile(new File(bpelURL.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BPELProcessDefinition) {
            if (getName() != null) {
                return getName().equals(((BPELProcessDefinition)obj).getName());
            } else {
                return ((BPELProcessDefinition)obj).getName() == null;
            }
        } else {
            return false;
        }
    }
}
