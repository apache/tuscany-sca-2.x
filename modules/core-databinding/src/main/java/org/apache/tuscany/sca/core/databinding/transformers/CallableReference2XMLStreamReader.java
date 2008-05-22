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
package org.apache.tuscany.sca.core.databinding.transformers;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.xml.StAXHelper;
import org.osoa.sca.CallableReference;

public class CallableReference2XMLStreamReader extends BaseTransformer<CallableReference, XMLStreamReader> implements
    PullTransformer<CallableReference, XMLStreamReader> {

    @Override
    protected Class<CallableReference> getSourceType() {
        return CallableReference.class;
    }

    @Override
    protected Class<XMLStreamReader> getTargetType() {
        return XMLStreamReader.class;
    }

    public XMLStreamReader transform(CallableReference source, TransformationContext context) {
        try {
            if (source != null) {
                if (source instanceof CallableReferenceImpl) {
                    XMLStreamReader xmlReader = ((CallableReferenceImpl)source).getXMLReader();
                    if (xmlReader != null) {
                        return xmlReader;
                    } else {
                        String xmlString = ((CallableReferenceImpl)source).toXMLString();

                        // remove "<?xml...?>" processing instruction and wrap with a top-level element
                        return StAXHelper.createXMLStreamReader("<reference xmlns=\"http://callable\">"
                                        + xmlString.substring(xmlString.indexOf("?>") + 2)
                                        + "</reference>");
                    }
                } else {   
                    throw new TransformationException("Unrecognized transformation source object");
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }
	
    @Override
    public int getWeight() {
        return 10;
    }

}
