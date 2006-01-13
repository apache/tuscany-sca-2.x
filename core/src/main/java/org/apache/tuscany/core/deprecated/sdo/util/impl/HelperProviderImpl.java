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
package org.apache.tuscany.core.deprecated.sdo.util.impl;


import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.model.util.ConfiguredResourceSetImpl;
import org.apache.tuscany.common.resource.loader.ResourceLoaderFactory;
import org.apache.tuscany.core.deprecated.sdo.util.CopyHelper;
import org.apache.tuscany.core.deprecated.sdo.util.DataFactory;
import org.apache.tuscany.core.deprecated.sdo.util.HelperProvider;
import org.apache.tuscany.core.deprecated.sdo.util.TypeHelper;
import org.apache.tuscany.core.deprecated.sdo.util.XMLHelper;
import org.apache.tuscany.core.deprecated.sdo.util.XSDHelper;


/**
 */
public class HelperProviderImpl implements HelperProvider {

    private ConfiguredResourceSet configuredResourceSet;

    private DataFactory dataFactory;
    private TypeHelper typeHelper;
    private XMLHelper xmlHelper;
    private CopyHelper copyHelper;
    private XSDHelper xsdHelper;

    /**
     * Constructor
     */
    public HelperProviderImpl() {
        this.configuredResourceSet = new ConfiguredResourceSetImpl(ResourceLoaderFactory.getResourceLoader(Thread.currentThread().getContextClassLoader()));
        dataFactory = new DataFactoryImpl(this.configuredResourceSet);
        xmlHelper = new XMLHelperImpl(this.configuredResourceSet);
        typeHelper = new TypeHelperImpl(this.configuredResourceSet);
        copyHelper = new CopyHelperImpl();
        xsdHelper = new XSDHelperImpl(configuredResourceSet);
    }

    /**
     * Constructor
     */
    public HelperProviderImpl(ConfiguredResourceSet resourceSet) {
        this.configuredResourceSet = resourceSet;
        dataFactory = new DataFactoryImpl(this.configuredResourceSet);
        xmlHelper = new XMLHelperImpl(this.configuredResourceSet);
        typeHelper = new TypeHelperImpl(this.configuredResourceSet);
        copyHelper = new CopyHelperImpl();
        xsdHelper = new XSDHelperImpl(configuredResourceSet);
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.HelperProvider#getDataFactory()
     */
    public DataFactory getDataFactory() {
        return dataFactory;
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.HelperProvider#getXMLHelper()
     */
    public XMLHelper getXMLHelper() {
        return xmlHelper;
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.HelperProvider#getTypeHelper()
     */
    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.HelperProvider#getCopyHelper()
     */
    public CopyHelper getCopyHelper() {
        return copyHelper;
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.HelperProvider#getXSDHelper()
     */
    public XSDHelper getXSDHelper() {
        return xsdHelper;
    }

}
