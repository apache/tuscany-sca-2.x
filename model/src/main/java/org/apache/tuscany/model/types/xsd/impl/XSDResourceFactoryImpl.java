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
package org.apache.tuscany.model.types.xsd.impl;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.apache.tuscany.model.util.XMLResourceFactoryImpl;

/**
 *         <p/>
 *         A configured XSDResourceFactory.
 */
public class XSDResourceFactoryImpl extends XMLResourceFactoryImpl {

    public final static XSDResourceFactoryImpl INSTANCE = new XSDResourceFactoryImpl();

    /**
     * Constructor
     */
    public XSDResourceFactoryImpl() {
        super();
    }

    /**
     * @see com.ibm.ws.sca.resources.util.XMLResourceFactoryImpl#getDefaultLoadOptions(org.eclipse.emf.ecore.resource.Resource)
     */
    protected Map getDefaultLoadOptions(Resource resource) {
        return ((XSDResourceImpl) resource).getLoadSaveOptions();
    }

    /**
     * @see com.ibm.ws.sca.resources.util.XMLResourceFactoryImpl#getDefaultSaveOptions(org.eclipse.emf.ecore.resource.Resource)
     */
    protected Map getDefaultSaveOptions(Resource resource) {
        return ((XSDResourceImpl) resource).getDefaultSaveOptions();
    }

    /**
     * @see com.ibm.ws.sca.resources.util.XMLResourceFactoryImpl#doCreateResource(org.eclipse.emf.common.util.URI)
     */
    protected Resource doCreateResource(URI uri) {
        return new FixedXSDResourceImpl(uri);
    }

}
