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

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.osoa.sca.CallableReference;

@SuppressWarnings("unchecked")
public class XMLStreamReader2CallableReference extends BaseTransformer<XMLStreamReader, CallableReference>
            implements PullTransformer<XMLStreamReader, CallableReference> {

    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String COMPOSITE = "composite";
    public static final QName COMPOSITE_QNAME = new QName(SCA10_NS, COMPOSITE);
    
    public CallableReference transform(XMLStreamReader source, TransformationContext context) {
        try {
            if (source != null) {
                skipTopLevelElement(source);
                Class refType = context.getTargetDataType().getPhysical();
                Class implType;
                if (refType.isAssignableFrom(CallableReferenceImpl.class)) {
                    implType = CallableReferenceImpl.class;
                } else if (refType.isAssignableFrom(ServiceReferenceImpl.class)) {
                    implType = ServiceReferenceImpl.class;
                } else {   
                    throw new TransformationException("Unrecognized transformation target type");
                }
                Constructor constructor = implType.getConstructor(new Class[] {XMLStreamReader.class});
                return (CallableReference)constructor.newInstance(new Object[] {source});
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    /*
     * Step over top-level element added by CallableReference2XMLStreamReader
     */
    private void skipTopLevelElement(XMLStreamReader source) throws XMLStreamException {
        while (source.hasNext()) {
            int event = source.getEventType();
            if (event == START_ELEMENT) {
                QName name = source.getName();
                if (COMPOSITE_QNAME.equals(name)) {
                    return;
                }
            }
            source.next();
        }
        throw new TransformationException("<composite> element not found");
    }

    @Override
    protected Class<XMLStreamReader> getSourceType() {
        return XMLStreamReader.class;
    }

    @Override
    protected Class<CallableReference> getTargetType() {
        return CallableReference.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
