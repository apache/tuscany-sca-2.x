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

package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.xml.sax.ContentHandler;

/**
 * @version $Rev$ $Date$
 */
public class JAXB2SAX extends BaseTransformer<Object, ContentHandler> implements
    PushTransformer<Object, ContentHandler> {

    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    @Override
    protected Class<ContentHandler> getTargetType() {
        return ContentHandler.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.PushTransformer#transform(java.lang.Object, java.lang.Object, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public void transform(Object source, ContentHandler target, TransformationContext tContext) {
        try {
            JAXBContext context = JAXBContextHelper.createJAXBContext(tContext, true);
            Marshaller marshaller = context.createMarshaller();
            Object jaxbElement = JAXBContextHelper.createJAXBElement(context, tContext.getSourceDataType(), source);
            marshaller.marshal(jaxbElement, target);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public int getWeight() {
        return 20;
    }

    @Override
    public String getSourceDataBinding() {
        return JAXBDataBinding.NAME;
    }
}
