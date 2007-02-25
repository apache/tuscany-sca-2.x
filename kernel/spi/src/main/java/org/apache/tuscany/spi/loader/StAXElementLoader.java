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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * A loader that creates a model object from a StAX input stream.
 *
 * @version $Rev$ $Date$
 */
public interface StAXElementLoader<T extends ModelObject> {
    /**
     * Create the model object for an element in an XML stream. When this method returns the stream will be positioned
     * on the corresponding END_ELEMENT.
     *
     * @param object  the model object to load configuration data into. An implementation may choose to return a
     *                different model object than the one passed in, in which case it is responsible for copying data.
     *                If null, the loader is responsible for creating a model object itself
     * @param reader  the XML stream reader positioned on the applicable START_ELEMENT
     * @param context the context for the load operation
     * @return the model object for that element
     */
    T load(ModelObject object, XMLStreamReader reader, DeploymentContext context)
        throws XMLStreamException, LoaderException;
}
