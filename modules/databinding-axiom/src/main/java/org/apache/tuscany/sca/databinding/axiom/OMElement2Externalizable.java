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

import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.ObjectInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;

/**
 * Transformer to convert data from an OMElement to XML String
 *
 * @version $Rev$ $Date$
 */
public class OMElement2Externalizable extends BaseTransformer<OMElement, Externalizable> implements PullTransformer<OMElement, Externalizable> {
    // private XmlOptions options;
    
    public Externalizable transform(OMElement source, TransformationContext context) {
        try {
            String value = source.getText();
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64Binary.decode(value));
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            ois.close();
            Externalizable aReference = (Externalizable) obj;
            return aReference;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    protected Class<OMElement> getSourceType() {
        return OMElement.class;
    }

    @Override
    protected Class<Externalizable> getTargetType() {
        return Externalizable.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
