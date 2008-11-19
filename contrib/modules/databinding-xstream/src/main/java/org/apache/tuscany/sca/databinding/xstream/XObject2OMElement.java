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
package org.apache.tuscany.sca.databinding.xstream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import com.thoughtworks.xstream.XStream;

/**
 * XObject --> AXIOM OMElement transformer
 * 
 * @version $Rev$ $Date$
 */
public class XObject2OMElement extends BaseTransformer<XObject, OMElement> implements
    PullTransformer<XObject, OMElement> {

    public OMElement transform(XObject source, TransformationContext context) {
        MetaObject mj = new MetaObjectImpl(source);
        OMElement element = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XStream xs = new XStream();
        xs.alias("xobject", mj.getClass());
        xs.toXML(mj, out);
        /* TODO: Maybe a base64 conversion...? or not?
         *  ie. new ByteArrayInputStream(Utils.toBase64(out.toByteArray()).getBytes())
         */
        try {
            // what's better this one:
            StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(out.toByteArray()));
            // or this one:
            //  StaAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(Utils.toBase64(out.toByteArray()).getBytes()))
            element = builder.getDocumentElement();
            adjustElementName(context, element);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
        return element;

    }

    private static void adjustElementName(TransformationContext context, OMElement element) {
        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object logical = dataType == null ? null : dataType.getLogical();
            if (!(logical instanceof XMLType)) {
                return;
            }
            XMLType xmlType = (XMLType)logical;
            if (xmlType.isElement() && !xmlType.getElementName().equals(element.getQName())) {
                // FIXME:: Throw exception or switch to the new Element?
                OMFactory factory = OMAbstractFactory.getOMFactory();
                QName name = xmlType.getElementName();
                OMNamespace namespace = factory.createOMNamespace(name.getNamespaceURI(), name.getPrefix());
                element.setNamespace(namespace);
                element.setLocalName(name.getLocalPart());
            }
        }
    }

    @Override
    public Class<XObject> getSourceType() {
        return XObject.class;
    }

    @Override
    public Class<OMElement> getTargetType() {
        return OMElement.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
