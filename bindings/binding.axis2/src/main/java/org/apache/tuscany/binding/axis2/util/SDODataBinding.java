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
package org.apache.tuscany.binding.axis2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;

import commonj.sdo.helper.TypeHelper;
import org.apache.axiom.om.OMElement;

/**
 * DataBinding for converting between AXIOM OMElement and Java Objects
 */
public class SDODataBinding {

    //private ClassLoader classLoader;
    //private TypeHelper typeHelper;
    //private boolean isWrapped;
    //private QName outElementQName;

    public SDODataBinding(ClassLoader classLoader, TypeHelper typeHelper, boolean isWrapped, QName outElementQName) {
        //this.classLoader = classLoader;
        //this.typeHelper = typeHelper;
        //this.isWrapped = isWrapped;
        //this.outElementQName = outElementQName;
    }

    public Object[] fromOMElement(OMElement omElement) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            baos.flush();
            baos.close();

            //FIXME: need to use SDOXMLHelper
            //return SDOXMLHelper.toObjects(classLoader,typeHelper, baos.toByteArray(), isWrapped);
            return null;

        } catch (IOException e) {
            throw new InvocationRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new InvocationRuntimeException(e);
        }
    }

    public OMElement toOMElement(Object[] os) {
        //FIXME
/*
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DataObject dataObject = SDOXMLHelper.toDataObject(classLoader, typeHelper, os, outElementQName, isWrapped);
            XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
            xmlHelper.save(dataObject, outElementQName.getNamespaceURI(), outElementQName.getLocalPart(), baos);
            baos.close();

            XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(
            baos.toByteArray()));
            OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), xsr);
            OMElement omElement = builder.getDocumentElement();

            return omElement;
            return null;
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
        } catch (FactoryConfigurationError e) {
            throw new ServiceRuntimeException(e);
        }*/
        return null;
    }

}
