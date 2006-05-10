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
package org.apache.tuscany.databinding.sdo.system;

import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * Implementation of the SDO service.
 */
public class SDOServiceImpl implements SDOService {

    private TypeHelper typeHelper;

    private DataFactory dataFactory;

    private XSDHelper xsdHelper;

    private XMLHelper xmlHelper;
    
    //FIXME We need one instance of this service per application (SCA composite instance), using
    // the TypeHelper created for this particular composite instance. The type helper to use is a
    // property of the AssemblyContext in use in the particular composite instance.

    /**
     * Constructs a new SDOServiceImpl.
     * 
     * @param typeHelper
     */
    public SDOServiceImpl(TypeHelper typeHelper) {
        this.typeHelper = typeHelper;
        dataFactory = SDOUtil.createDataFactory(typeHelper);
        xsdHelper = SDOUtil.createXSDHelper(typeHelper);
        xmlHelper = SDOUtil.createXMLHelper(typeHelper);
    }

    /**
     * Constructs a new SDOServiceImpl.
     * 
     * @param typeHelper
     * @param dataFactory
     * @param xsdHelper
     * @param xmlHelper
     */
    public SDOServiceImpl(TypeHelper typeHelper, DataFactory dataFactory,
                                                XSDHelper xsdHelper, XMLHelper xmlHelper) {
        this.typeHelper = typeHelper;
        this.dataFactory = dataFactory;
        this.xsdHelper = xsdHelper;
        this.xmlHelper = xmlHelper;
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public DataFactory getDataFactory() {
        return dataFactory;
    }

    public XMLHelper getXMLHelper() {
        return xmlHelper;
    }

    public XSDHelper getXSDHelper() {
        return xsdHelper;
    }

}
