/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.tuscany.core.wire.InvocationRuntimeException;
import org.apache.tuscany.databinding.sdo.SDOXMLHelper;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.osoa.sca.ServiceRuntimeException;

import commonj.sdo.DataObject;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;

/**
 * DataBinding for converting between AXIOM OMElement and Java Objects
 */
public class SDODataBinding {

    private ClassLoader classLoader;

    private TypeHelper typeHelper;

    private QName elementQName;

    private boolean isWrapped;

    public SDODataBinding(ClassLoader classLoader, TypeHelper typeHelper, QName elementQName, boolean isWrapped) {
        this.classLoader = classLoader;
        this.typeHelper = typeHelper;
        this.elementQName = elementQName;
        this.isWrapped = isWrapped;
    }

    public Object[] fromOMElement(OMElement omElement) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            omElement.serialize(baos);

            baos.flush();
            baos.close();

            return SDOXMLHelper.toObjects(classLoader,typeHelper, baos.toByteArray(), isWrapped);

        } catch (IOException e) {
            throw new InvocationRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new InvocationRuntimeException(e);
        }
    }

    public OMElement toOMElement(Object[] os) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DataObject dataObject = SDOXMLHelper.toDataObject(classLoader, typeHelper, os, elementQName, isWrapped);
            XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
            xmlHelper.save(dataObject, elementQName.getNamespaceURI(), elementQName.getLocalPart(), baos);
            baos.close();

            XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
            OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), xsr);
            OMElement omElement = builder.getDocumentElement();

            return omElement;

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
        } catch (FactoryConfigurationError e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
