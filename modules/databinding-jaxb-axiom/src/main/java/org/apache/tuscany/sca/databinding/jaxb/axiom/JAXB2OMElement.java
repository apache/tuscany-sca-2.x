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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;

/**
 * JAXB Object --> AXIOM OMElement transformer
 * 
 * @version $Rev$ $Date$
 */
public class JAXB2OMElement extends BaseTransformer<Object, OMElement> implements PullTransformer<Object, OMElement> {
    private OMFactory factory = OMAbstractFactory.getOMFactory();

    @Override
    public String getSourceDataBinding() {
        return JAXBDataBinding.NAME;
    }

    @SuppressWarnings("unchecked")
    public OMElement transform(Object source, TransformationContext context) throws TransformationException {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContextHelper.createJAXBContext(context, true);
        } catch (JAXBException e) {
            throw new TransformationException(e);
        }
        Object element = JAXBContextHelper.createJAXBElement(jaxbContext, context.getTargetDataType(), source);
        QName name = jaxbContext.createJAXBIntrospector().getElementName(element);
        JAXBDataSource dataSource = new JAXBDataSource(element, jaxbContext);
        OMElement omElement = AxiomHelper.createOMElement(factory, name, dataSource);
        return omElement;
    }

    @Override
    public Class<Object> getSourceType() {
        return Object.class;
    }

    @Override
    public Class<OMElement> getTargetType() {
        return OMElement.class;
    }

    @Override
    public int getWeight() {
        return 3000;
    }

}
