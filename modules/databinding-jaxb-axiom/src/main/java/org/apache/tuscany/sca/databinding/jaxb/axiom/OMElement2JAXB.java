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

package org.apache.tuscany.sca.databinding.jaxb.axiom;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;

/**
 * @version $Rev$ $Date$
 */
public class OMElement2JAXB extends BaseTransformer<OMElement, Object> implements PullTransformer<OMElement, Object> {

    @Override
    public String getSourceDataBinding() {
        return org.apache.axiom.om.OMElement.class.getName();
    }

    @SuppressWarnings("unchecked")
    public Object transform(final OMElement source, final TransformationContext context) throws TransformationException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws JAXBException, XMLStreamException {
                    // Marshalling directly to the output stream is faster than marshalling through the
                    // XMLStreamWriter. 
                    // Take advantage of this optimization if there is an output stream.
                    JAXBContext jaxbContext = JAXBContextHelper.createJAXBContext(context, true);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    XMLStreamReader reader = source.getXMLStreamReaderWithoutCaching();
                    Object result =
                        unmarshaller.unmarshal(reader, JAXBContextHelper.getJavaType(context.getTargetDataType()));
                    reader.close();
                    return JAXBContextHelper.createReturnValue(context.getTargetDataType(), result);
                }
            });
        } catch (PrivilegedActionException e) {
            throw new TransformationException(e.getException());
        }
    }

    @Override
    public Class<OMElement> getSourceType() {
        return OMElement.class;
    }

    @Override
    public Class<Object> getTargetType() {
        return Object.class;
    }

    @Override
    public int getWeight() {
        return 3000;
    }
}
