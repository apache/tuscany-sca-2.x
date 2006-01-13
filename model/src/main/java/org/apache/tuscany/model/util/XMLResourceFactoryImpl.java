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

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.sdo.SDOPackage;
import org.eclipse.emf.ecore.xmi.XMLParserPool;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

/**
 *         <p/>
 *         A configured XMLResourceFactory.
 */
public class XMLResourceFactoryImpl extends org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl {

    private static final XMLParserPool parserPool = new XMLParserPoolImpl();

    /**
     * Constructor.
     */
    public XMLResourceFactoryImpl() {
        super();
    }

    /**
     * @see com.ibm.wsspi.sca.scdl.util.SCDLResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
     */
    public Resource createResource(URI uri) {
        Resource result = doCreateResource(uri);

        Map options = getDefaultLoadOptions(result);
        XMLResource.XMLMap xmlMap = new XMLMapImpl();
        options.put(XMLResource.OPTION_XML_MAP, xmlMap);
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
        options.put(XMLResource.OPTION_USE_PARSER_POOL, parserPool);
        options.put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);
        options.put(XMLResource.OPTION_ANY_SIMPLE_TYPE, SDOPackage.eINSTANCE.getEDataObjectSimpleAnyType());
        options.put(XMLResource.OPTION_ANY_TYPE, SDOPackage.eINSTANCE.getEDataObjectAnyType());
        options.put(XMLResource.OPTION_LAX_FEATURE_PROCESSING, Boolean.TRUE);

        options = getDefaultSaveOptions(result);
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        options.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);
        options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);

        return result;
    }

    /**
     * Returns the resource default load options
     *
     * @param resource
     * @return
     */
    protected Map getDefaultLoadOptions(Resource resource) {
        return ((XMLResource) resource).getDefaultLoadOptions();
    }

    /**
     * Returns the resource default save options
     *
     * @param resource
     * @return
     */
    protected Map getDefaultSaveOptions(Resource resource) {
        return ((XMLResource) resource).getDefaultSaveOptions();
    }

    /**
     * Create a resource.
     * @param uri
     * @return
     */
    protected Resource doCreateResource(URI uri) {
        return new XMLResourceImpl(uri);
	}

}