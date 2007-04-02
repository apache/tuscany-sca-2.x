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
package org.apache.tuscany.container.javascript.utils.xmlfromxsd;

import java.util.Hashtable;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Scope;

import org.apache.xmlbeans.XmlObject;

/**
 * An implementation for the XMLInstnaceRegistry
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@EagerInit
public class XmlInstanceRegistryImpl implements XmlInstanceRegistry {

    private Hashtable<String, Map<String, XmlObject>> wsdlXmlInstances;

    private XMLGenerator xmlGenerator;

    public XmlInstanceRegistryImpl() {
        wsdlXmlInstances = new Hashtable<String, Map<String, XmlObject>>();

        XMLfromXSDConfiguration generationConfig = new XMLfromXSDConfiguration();
        xmlGenerator = XMLGeneratorFactory.getInstance().createGenerator(XMLGenerator.XMLBEANS_BASED);
        xmlGenerator.setConfig(generationConfig);
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.container.javascript.XmlInstanceRegistry#getXmlInstance(java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String, XmlObject> getXmlInstance(String wsdlPath) throws XmlInstanceCreationException {
        Map<String, XmlObject> xmlInstanceMap = null;
        if ((xmlInstanceMap = wsdlXmlInstances.get(wsdlPath)) == null) {
            try {
                xmlGenerator.getConfig().setXsdFileName(wsdlPath);
                xmlInstanceMap = xmlGenerator.generateXmlAll();
                wsdlXmlInstances.put(wsdlPath, xmlInstanceMap);
            } catch (Exception e) {
                throw new XmlInstanceCreationException(e);
            }
        }
        return xmlInstanceMap;
    }

}
