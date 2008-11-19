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
package org.apache.tuscany.sca.databinding.job;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectOutputStream;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;
import org.apache.tuscany.sca.databinding.axiom.AxiomHelper;

@SuppressWarnings("unchecked")
public class Job2OMElement extends BaseTransformer<Job, OMElement> implements
        PullTransformer<Job, OMElement> {

    @Override
    protected Class getSourceType() {
        return Job.class;
    }

    @Override
    protected Class getTargetType() {
        return OMElement.class;
    }

    public OMElement transform(Job source, TransformationContext context) {
        OMElement element = null;

        try {
            // JobDataSource dataSource = new JobDataSource();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.close();
            byte[] bytes = bos.toByteArray();
            // ByteArrayDataSource ds = new ByteArrayDataSource(bytes);
            OMFactory factory = OMAbstractFactory.getOMFactory();
            OMNamespace ns = AxiomHelper.createOMNamespace(factory, new QName(
                    "http://job"));
            element = factory.createOMElement("jobreference", ns);
            // OMText data = factory.createOMText(new DataHandler(ds), true);
            // element.addChild(data);
            element.setText(Base64Binary.encode(bytes));
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
