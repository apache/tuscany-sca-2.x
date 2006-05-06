/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tuscany.container.rhino.rhino;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.util.StreamWrapper;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

/**
 * Utility methods for converting between AXIOM OMElements and E4X XML objects
 */
public class E4XAXIOMUtils {

    /**
     * Get an AXIOM OMElement from E4X XML
     */
    public static OMElement toOMElement(Scriptable jsXML) {

        XmlObject xmlObject = toXmlObject(jsXML);

        XMLStreamReader reader = xmlObject.newXMLStreamReader();
        StreamWrapper stream = new StreamWrapper(reader);
        OMFactory factory = OMAbstractFactory.getOMFactory();
        StAXOMBuilder builder = new StAXOMBuilder(factory, stream);
        OMElement omElement = builder.getDocumentElement();

        // TODO: is this required anymore?
        // // TODO: remove when AXIS2 fixed (its in the gen'd stub code, JIRA?)
        // omElement.buildSource();
        // OptimizeExposer.optimizeContent(omElement, new QName[0]);

        return omElement;
    }

    /**
     * Get an E4X XML object from an AXIOM OMElement
     */
    public static Scriptable toScriptableObject(OMElement omElement, Scriptable scope) throws XmlException {

        // TODO: is this required anymore?
        // // TODO: remove when AXIS2 fixed (its in the gen'd stub code, JIRA?)
        // omElement.buildSource();

        // XMLStreamReader reader = omElement.getXMLStreamReaderWithoutCaching();
        XMLStreamReader reader = omElement.getXMLStreamReader(); // TODO: cache?
        XmlObject xmlObject = XmlObject.Factory.parse(reader);

        Context cx = Context.enter();
        try {

            Object jsSOAPEnvelope = cx.getWrapFactory().wrap(cx, scope, xmlObject, XmlObject.class);
            Scriptable jsXML = cx.newObject(scope, "XML", new Object[] { jsSOAPEnvelope });

            return jsXML;

        } finally {
            Context.exit();
        }
    }

    /**
     * Get an XMLBeans XmlObject from E4X XML
     */
    private static XmlObject toXmlObject(Scriptable jsXML) {
        // TODO: E4X Bug? Shouldn't need this copy, but without it the outer element gets lost???
        jsXML = (Scriptable) ScriptableObject.callMethod(jsXML, "copy", new Object[0]);

        Wrapper wrapper = (Wrapper) ScriptableObject.callMethod(jsXML, "getXmlObject", new Object[0]);
        XmlObject xmlObject = (XmlObject) wrapper.unwrap();
        return xmlObject;
    }

}
