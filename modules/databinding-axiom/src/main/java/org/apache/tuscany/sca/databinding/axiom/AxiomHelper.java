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

package org.apache.tuscany.sca.databinding.axiom;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Helper for AXIOM
 * 
 * @version $Rev$ $Date$
 */
public class AxiomHelper {
    private static final String DEFAULT_PREFIX = "_ns_";

    private AxiomHelper() {
    }

    /**
     * See http://issues.apache.org/jira/browse/WSCOMMONS-240
     * @param om
     */
    public static void completeAndClose(OMElement om) {
        // Get the builder associated with the om element
        OMXMLParserWrapper builder = om.getBuilder();
        if (builder != null) {
            if (builder instanceof StAXBuilder) {
                // FIXME: Uncomment it for AXIOM 1.2.5
                // ((StAXBuilder)builder).releaseParserOnClose(true);
            }
            OMElement document = builder.getDocumentElement();
            if (document != null) {
                document.build();
            }
        }
        if (builder instanceof StAXBuilder) {
            ((StAXBuilder)builder).close();
        }
    }

    /**
     * This method will close the builder immediately.  Any subsequent Axiom objects won't 
     * be built or accessible.
     */
    public static void closeImmediately(OMElement om) {
        // Get the builder associated with the om element
        OMXMLParserWrapper builder = om.getBuilder();
        if (builder != null) {
            if (builder instanceof StAXBuilder) {
                // FIXME: Uncomment it for AXIOM 1.2.5
                // ((StAXBuilder)builder).releaseParserOnClose(true);
                ((StAXBuilder)builder).close();
            }
            // builder.close();
        }
    }

    /**
     * @param context
     * @param element
     */
    public static void adjustElementName(TransformationContext context, OMElement element) {
        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object logical = dataType == null ? null : dataType.getLogical();
            if (!(logical instanceof XMLType)) {
                return;
            }
            XMLType xmlType = (XMLType)logical;
            if (xmlType.isElement() && !xmlType.getElementName().equals(element.getQName())) {
                // FIXME:: Throw expection or switch to the new Element?
                OMFactory factory = OMAbstractFactory.getOMFactory();
                QName name = xmlType.getElementName();
                OMNamespace namespace = factory.createOMNamespace(name.getNamespaceURI(), name.getPrefix());
                element.setNamespace(namespace);
                element.setLocalName(name.getLocalPart());
            }
        }
    }

    public static OMElement createOMElement(OMFactory factory, QName element) {
        String localName = element.getLocalPart();
        OMNamespace ns = createOMNamespace(factory, element);

        return factory.createOMElement(localName, ns);

    }

    public static OMElement createOMElement(OMFactory factory, QName element, OMDataSource dataSource) {
        String localName = element.getLocalPart();
        OMNamespace ns = createOMNamespace(factory, element);

        return factory.createOMElement(dataSource, localName, ns);

    }

    /**
     * @param factory
     * @param name
     * @return
     */
    public static OMNamespace createOMNamespace(OMFactory factory, QName name) {
        String namespaceURI = name.getNamespaceURI();
        String prefix = name.getPrefix();

        OMNamespace ns = null;
        if (namespaceURI.length() != 0) {
            // Qualified Element: we need an OMNamespace         
            if (prefix.length() == 0) {
                // The prefix does not appear to be specified, let's create one
                prefix = DEFAULT_PREFIX;
            }
            ns = factory.createOMNamespace(namespaceURI, prefix);
        }
        return ns;
    }
}
