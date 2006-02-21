/**
 *
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
package org.apache.tuscany.binding.axis.mediator.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.Constants;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.Mapping;
import org.apache.tuscany.binding.axis.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis.mediator.SOAPMediator;
import org.apache.tuscany.common.io.util.UTF8String;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.sdo.helper.HelperProviderImpl;
import org.osoa.sca.ModuleContext;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

/**
 */
public abstract class SOAPBaseMediatorImpl
        implements SOAPMediator {

    protected WebServicePortMetaData portMetaData;

    /**
     * 
     */
    public SOAPBaseMediatorImpl(WebServicePortMetaData portMetaData) {
        super();
        this.portMetaData = portMetaData;
    }

    /**
     * Read a SOAP envelope into a Message DataObject
     *
     * @param moduleContext
     * @param soapEnvelope
     * @return
     * @throws SOAPException
     * @throws IOException
     */
    protected Message readSOAPEnvelope(ModuleContext moduleContext, SOAPEnvelope soapEnvelope)
            throws SOAPException, IOException {
        XMLHelper xmlHelper = getHelperProvider(moduleContext).getXMLHelper();

        String xml;
        try {
            xml = soapEnvelope.getAsString();
        }
        catch (Exception e) {
            throw new SOAPException(e);
        }
        InputStream is = UTF8String.getInputStream(xml);

        // Fix the namespace prefix attributes
        // fixAttributes(soapEnvelope);

        // Load the SOAP envelope into a Message DataObject
        // Message message=(Message)xmlHelper.load(soapEnvelope);
        Message message = (Message) xmlHelper.load(is);
        return message;
    }

    /**
     * Fix the attributes of the given element.
     *
     * @param element
     */
    protected void fixAttributes(MessageElement element) {
        if (element.namespaces != null) {
            for (Iterator i = element.namespaces.iterator(); i.hasNext();) {
                Mapping mapping = (Mapping) i.next();
                String prefix = mapping.getPrefix();
                String nsURI = mapping.getNamespaceURI();
                String qname = prefix.length() != 0 ? "xmlns:" + prefix : "xmlns";
                element.setAttributeNS(Constants.NS_URI_XMLNS, qname, nsURI);
            }
        }
        for (Iterator i = element.getChildElements(); i.hasNext();) {
            Object e = i.next();
            if (e instanceof MessageElement) {
                fixAttributes((MessageElement) e);
            }
        }
    }

    /**
     * @param moduleContext
     * @param message
     * @param soapEnvelope
     * @throws IOException
     */
    protected void writeSOAPEnvelope(ModuleContext moduleContext, Message message, SOAPEnvelope soapEnvelope)
            throws IOException {
        XMLHelper xmlHelper = getHelperProvider(moduleContext).getXMLHelper();
        SOAPDocumentImpl document = new SOAPDocumentImpl(soapEnvelope);
        xmlHelper.save((DataObject) message, document);
    }

    /*
    protected static DataFactory getDataFactory( ModuleContext moduleContext )
    {
        HelperProvider helperProvider = getHelperProvider( moduleContext );
        DataFactory dataFactory = helperProvider.getDataFactory();
        return dataFactory;
    }

    protected static XSDHelper getXSDHelper( ModuleContext moduleContext )
    {
        HelperProvider helperProvider = getHelperProvider( moduleContext );
        return helperProvider.getXSDHelper();
    }
    */

}
