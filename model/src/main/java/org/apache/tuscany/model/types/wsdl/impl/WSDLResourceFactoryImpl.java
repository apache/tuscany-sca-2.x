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
package org.apache.tuscany.model.types.wsdl.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.wst.wsdl.WSDLPlugin;
import org.eclipse.wst.wsdl.binding.soap.internal.util.SOAPExtensibilityElementFactory;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;

/**
 *         <p/>
 *         A configured WSDLResourceFactory.
 */
public class WSDLResourceFactoryImpl extends ResourceFactoryImpl {

    public final static WSDLResourceFactoryImpl INSTANCE = new WSDLResourceFactoryImpl();
    private static final String SOAP_NAMESPACE_URI = "http://schemas.xmlsoap.org/wsdl/soap/";

    static {
        WSDLPlugin.INSTANCE.getExtensibilityElementFactoryRegistry().registerFactory(SOAP_NAMESPACE_URI, new SOAPExtensibilityElementFactory());
    }

    /**
     * Constructor
     */
    public WSDLResourceFactoryImpl() {
        super();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
     */
    public Resource createResource(URI uri) {
        return new WSDLResourceImpl(uri);
    }

}
