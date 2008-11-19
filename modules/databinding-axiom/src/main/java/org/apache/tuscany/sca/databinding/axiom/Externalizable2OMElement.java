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

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectOutputStream;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;

/**
 *
 * @version $Rev$ $Date$
 */
public class Externalizable2OMElement extends BaseTransformer<Externalizable, OMElement> implements
    PullTransformer<Externalizable, OMElement> {

    @Override
    protected Class<Externalizable> getSourceType() {
        return  Externalizable.class;
    }

    @Override
    protected Class<OMElement> getTargetType() {
        return OMElement.class;
    }

    public OMElement transform(Externalizable source, TransformationContext context) {
        OMElement element = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.close();
            OMFactory factory = OMAbstractFactory.getOMFactory();
            OMNamespace ns = AxiomHelper.createOMNamespace(factory, new QName("http://callable"));
            element = factory.createOMElement("reference",ns);
            element.setText(Base64Binary.encode(bos.toByteArray()));
            return element;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }
	
    @Override
    public int getWeight() {
        return 10;
    }

}
