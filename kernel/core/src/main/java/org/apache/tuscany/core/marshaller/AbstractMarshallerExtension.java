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
package org.apache.tuscany.core.marshaller;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.marshaller.ModelMarshaller;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

/**
 * Abstract marshaller that supports marshaller registry.
 * 
 * @version $Revision$ $Date: 2007-03-03 12:17:30 +0000 (Sat, 03 Mar
 *          2007) $
 * @param <MD>
 */
@EagerInit
public abstract class AbstractMarshallerExtension<MD extends ModelObject> implements ModelMarshaller<MD> {

    // Private Model marshaller registry
    protected ModelMarshallerRegistry registry;

    /**
     * Injects the model marshaller registry.
     * 
     * @param registry Model marshaller registry.
     */
    @Reference
    public final void setMarshallerRegistry(ModelMarshallerRegistry registry) {

        this.registry = registry;

        Class<MD> marshallerType = getModelObjectType();
        QName marshallerQName = getModelObjectQName();

        registry.registerMarshaller(marshallerType, marshallerQName, this);

    }

    /**
     * Gets the qualified name of the XML fragment for the marshalled model
     * object.
     * 
     * @return Qualified name of the XML fragment.
     */
    protected abstract QName getModelObjectQName();

    /**
     * Retursn the type of the model object.
     * 
     * @return Model object type.
     */
    protected abstract Class<MD> getModelObjectType();

}
