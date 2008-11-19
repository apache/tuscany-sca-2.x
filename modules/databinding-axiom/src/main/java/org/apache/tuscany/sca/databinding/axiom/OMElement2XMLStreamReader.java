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
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;

/**
 *
 * @version $Rev$ $Date$
 */
public class OMElement2XMLStreamReader extends BaseTransformer<OMElement, XMLStreamReader> implements
    PullTransformer<OMElement, XMLStreamReader> {
    // private XmlOptions options;

    public static final QName QNAME_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil");

    public XMLStreamReader transform(OMElement source, TransformationContext context) {
        if (source == null) {
            return null;
        } else {
            if ("true".equals(source.getAttributeValue(QNAME_NIL))) {
                return null;
            } else {
                return source.getXMLStreamReader();
            }
        }
    }

    @Override
    protected Class<OMElement> getSourceType() {
        return OMElement.class;
    }

    @Override
    protected Class<XMLStreamReader> getTargetType() {
        return XMLStreamReader.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
