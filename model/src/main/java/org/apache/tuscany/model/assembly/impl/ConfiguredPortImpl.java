/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.impl;

import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Port;

/**
 */
public class ConfiguredPortImpl extends EDataObjectImpl implements ConfiguredPort {
    private Part part;
    private Port port;

    private Object runtimeConfiguration;
    private Object proxyFactory;

    /**
     * Constructor
     */
    protected ConfiguredPortImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredReference#getPart()
     */
    public Part getPart() {
        return part;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#setPart(org.apache.tuscany.model.assembly.Part)
     */
    public void setPart(Part part) {
        this.part = part;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#getPort()
     */
    public Port getPort() {
        return port;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#setPort(org.apache.tuscany.model.assembly.Port)
     */
    public void setPort(Port port) {
        this.port = port;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#getProxyFactory()
     */
    public Object getProxyFactory() {
        return proxyFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredPort#setProxyFactory(java.lang.Object)
     */
    public void setProxyFactory(Object proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredRuntimeObject#getRuntimeConfiguration()
     */
    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredRuntimeObject#setRuntimeConfiguration(java.lang.Object)
     */
    public void setRuntimeConfiguration(Object configuration) {
        runtimeConfiguration = configuration;
    }

}
