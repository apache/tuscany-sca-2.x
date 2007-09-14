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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessState;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.w3c.dom.Node;

/**
 * The model representing a BPEL implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public class BPELImplementationImpl extends ComponentTypeImpl implements BPELImplementation {

    private Service _bpelService;
    private QName _processName;
    private byte[] _compiledProcess;
    private boolean unresolved;

    /**
     * Constructs a new BPEL implementation.
     */
    public BPELImplementationImpl(AssemblyFactory assemblyFactory,
                              WSDLFactory wsdlFactory) {

    }

    public void setCompiledProcess(byte[] compiledProcess) {
        _compiledProcess = compiledProcess;
    }

    public QName getProcess() {
        return _processName;
    }
    
    public void setProcess(QName processName) {
        _processName = processName;
    }

    public ProcessConf getProcessConf() {
        return null;
    }

    @Override
    public String getURI() {
        // The sample BPEL implementation does not have a URI
        return null;
    }

    @Override
    public void setURI(String uri) {
        // The sample BPEL implementation does not have a URI
    }

    @Override
    public ConstrainingType getConstrainingType() {
        // The sample BPEL implementation does not support constrainingTypes
        return null;
    }

    @Override
    public List<Property> getProperties() {
        // The sample BPEL implementation does not support properties
        return Collections.emptyList();
    }

    
    @Override
    public List<Reference> getReferences() {
        // The sample BPEL implementation does not support properties
        return Collections.emptyList();
    }

    public boolean isUnresolved() {
        return this.unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    private class ProcessConfImpl implements ProcessConf {
        public QName getProcessId() {
            return _processName;
        }

        public QName getType() {
            return _processName;
        }

        public long getVersion() {
            // TODO Versioniong?
            return 0;
        }

        public boolean isTransient() {
            return false;
        }

        public InputStream getCBPInputStream() {
            return new ByteArrayInputStream(_compiledProcess);
        }

        public String getBpelDocument() {
            return null;
        }

        public URI getBaseURI() {
            return null;
        }

        public Date getDeployDate() {
            return null;
        }

        public String getDeployer() {
            return null;
        }

        public ProcessState getState() {
            return null;
        }

        public List<File> getFiles() {
            return null;
        }

        public Map<QName, Node> getProperties() {
            return null;
        }

        public String getPackage() {
            return null;
        }

        public Definition getDefinitionForService(QName qName) {
            return null;
        }

        public Definition getDefinitionForPortType(QName qName) {
            return null;
        }

        public Map<String, Endpoint> getProvideEndpoints() {
            return null;
        }

        public Map<String, Endpoint> getInvokeEndpoints() {
            return null;
        }

        public boolean isEventEnabled(List<String> strings, BpelEvent.TYPE type) {
            return true;
        }
    }
}
