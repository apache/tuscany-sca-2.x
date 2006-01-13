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
package org.apache.tuscany.model.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;

import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 *         <p/>
 *         A configured ResourceSet.
 */
public class ExtendedResourceSetImpl extends org.eclipse.emf.ecore.resource.impl.ResourceSetImpl implements ExtendedResourceSet {

    private ExtendedMetaData extendedMetaData;
    private ResourceLoader bundleContext;

    /**
     * Constructor
     */
    public ExtendedResourceSetImpl(ResourceLoader bundleContext) {
        super();
        this.bundleContext = bundleContext;
    }

    /**
     * @see com.ibm.ws.sca.model.config.Config#getExtendedMetaData()
     */
    public ExtendedMetaData getExtendedMetaData() {
        if (extendedMetaData == null) {
            extendedMetaData = new BasicExtendedMetaData(getPackageRegistry());
        }
        return extendedMetaData;
    }

    /**
     * @see org.apache.tuscany.model.util.ExtendedResourceSet#getResourceLoader()
     */
    public ResourceLoader getResourceLoader() {
        return bundleContext;
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getLoadOptions()
     */
    public Map getLoadOptions() {
        if (loadOptions == null) {
            loadOptions = new HashMap();
            XMLResource.XMLMap xmlMap = new XMLMapImpl();
            loadOptions.put(XMLResource.OPTION_XML_MAP, xmlMap);
            loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
            loadOptions.put(XMLResource.OPTION_ENCODING, "UTF-8");
            loadOptions.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
            loadOptions.put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);
        }
        return loadOptions;
    }

}