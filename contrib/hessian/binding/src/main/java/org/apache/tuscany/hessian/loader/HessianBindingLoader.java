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
package org.apache.tuscany.hessian.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.hessian.model.logical.HessianBindingDefinition;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.annotations.Reference;

/**
 * Loader for hessian binding.
 * 
 * @version $Revision$ $Date$
 */
public class HessianBindingLoader extends LoaderExtension<HessianBindingDefinition> {

    /**
     * Qualified name of the binding.
     */
    public static final QName BINDING_HESSIAN =
        new QName("http://tuscany.apache.org/xmlns/binding/hessian/2.0-alpha2-incubating-SNAPSHOT", "binding.hessian");

    /**
     * Injects the loader registry.
     * 
     * @param loaderRegistry Loader registry.
     */
    protected HessianBindingLoader(@Reference(name = "loaderRegistry")
    LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    /**
     * Gets the qualified name of the binding element.
     */
    @Override
    public QName getXMLType() {
        return BINDING_HESSIAN;
    }

    /**
     * Loads the binding from the XML.
     */
    public HessianBindingDefinition load(ModelObject arg0, XMLStreamReader arg1, DeploymentContext arg2)
        throws XMLStreamException, LoaderException {
        // TODO populate the model object
        return new HessianBindingDefinition();
    }

}
