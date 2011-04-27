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
import org.apache.tuscany.sca.databinding.BaseTransformer;

/**
 *
 * @version $Rev$ $Date$
 */
public class OMElement2XMLStreamReader extends BaseTransformer<OMElement, XMLStreamReader> implements
    PullTransformer<OMElement, XMLStreamReader> {

    /*
     * Reverting the behavior here in 2.x (though not in 1.x) to pass through the 
     * XMLStreamReader even in the case of an xsi:nil element.  This appears to only
     * be relied upon in 1.x by the XMLStreamReader2CallableReference transformer, and can
     * be changed in 2.x without breaking anything.  
     *
     * I'd preferto move the responsibility for handling xsi:nil to transformers such as
     * XMLStreamReader2CallableReference.  While for something like JAXB, xsi:nil would
     * typically map to 'null', for something XML-centric like DOM I think it's more useful
     * to transform to a DOM Element with xsi:nil="true".   For now I'll leave this issue
     * unaddressed in 2.x, where we'd have to adjust XMLStreamReader2CallableReference in 
     * order to make a change like this.
     */
    public XMLStreamReader transform(OMElement source, TransformationContext context) {
        return source != null ? source.getXMLStreamReader() : null;  
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
