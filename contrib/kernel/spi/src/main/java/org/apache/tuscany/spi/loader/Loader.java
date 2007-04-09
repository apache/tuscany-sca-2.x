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
package org.apache.tuscany.spi.loader;

import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * System service for loading physical artifacts that represent SCDL configurations and creating the model objects that
 * represent them.
 *
 * @version $Rev$ $Date$
 */
public interface Loader {
    /**
     * Parse the supplied XML stream, dispatching to the appropriate registered loader for each element encountered in
     * the stream.
     * <p/>
     * This method must be called with the XML cursor positioned on a START_ELEMENT event. When this method returns, the
     * stream will be positioned on the corresponding END_ELEMENT event.
     *
     * @param object            the model object to load configuration data into. If null, the loader dispatched to is
     *                          responsible for creating a model object itself
     * @param reader            the XML stream to parse
     * @param deploymentContext the current deployment context
     * @return the model object obtained by parsing the current element on the stream
     * @throws LoaderException if there was a problem loading the document
     * @throws XMLStreamException if there was a problem reading the stream
     */
    ModelObject load(ModelObject object, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException;

    /**
     * Load a model object from a specified location.
     *
     * @param object  the model object to load configuration data into. If null, the loader dispatched to is responsible
     *                for creating a model object itself
     * @param url     the location of an XML document to be loaded
     * @param type    the type of ModelObject that is expected to be in the document
     * @param context the current deployment context
     * @return the model ojbect loaded from the document
     * @throws LoaderException if there was a problem loading the document
     */
    <MO extends ModelObject> MO load(ModelObject object, URL url, Class<MO> type, DeploymentContext context)
        throws LoaderException;

    /**
     * Load the component type definition for a given implementation. How the component type information is located is
     * defined by the implementation specification. It may include loading from an XML sidefile, introspection of some
     * artifact related to the implementation, some combination of those techniques or any other implementation-defined
     * mechanism.
     *
     * @param implementation the implementation whose component type should be loaded
     * @param context        the current deployment context
     * @throws LoaderException if there was a problem loading the component type definition
     */
    <I extends Implementation<?>> void loadComponentType(I implementation, DeploymentContext context)
        throws LoaderException;
}
