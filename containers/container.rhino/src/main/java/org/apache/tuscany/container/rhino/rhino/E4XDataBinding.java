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
package org.apache.tuscany.container.rhino.rhino;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.core.wire.InvocationRuntimeException;
import org.apache.tuscany.databinding.sdo.SDOXMLHelper;
import org.apache.xmlbeans.XmlObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import commonj.sdo.helper.TypeHelper;

/**
 * DataBinding to convert between Java objects and JavaScript E4X XML objects. This uses SDO to do the conversion between XML and Java so WSDL/XSDs
 * need to have be registered with the SDO runtime.
 * 
 * TODO: suport non-wrapped WSDL 
 */
public class E4XDataBinding {

    private ClassLoader classLoader;

    private TypeHelper typeHelper;

    private Map<String, QName> function2ElementMap;

    private static final boolean IS_WRAPPED = true;

    public E4XDataBinding(ClassLoader classLoader, TypeHelper typeHelper) {
        this.classLoader = classLoader;
        this.typeHelper = typeHelper;
        this.function2ElementMap = new HashMap<String, QName>();
    }

    /**
     * Convert E4X XML to Java objects
     * 
     * @param e4xXML
     * @return the array of Objects
     */
    public Object[] toObjects(Scriptable e4xXML) {
        byte[] xmlBytes = e4xXML.toString().getBytes();
        Object[] os = SDOXMLHelper.toObjects(classLoader, typeHelper, xmlBytes, IS_WRAPPED);
        return os;
    }

    /**
     * Convert request Java objects to XML 
     * 
     * @param functionName
     * @param os
     * @param scope
     * @return a JavaScript E4X XML object
     */
    public Scriptable toE4X(String functionName, Object[] os, Scriptable scope) {
        QName elementQN = function2ElementMap.get(functionName);
        byte[] xmlBytes = SDOXMLHelper.toXMLBytes(classLoader, typeHelper, os, elementQN, IS_WRAPPED);

        XmlObject xmlObject;
        try {
            xmlObject = XmlObject.Factory.parse(new ByteArrayInputStream(xmlBytes));
        } catch (Exception e) {
            throw new InvocationRuntimeException(e);
        }

        Context cx = Context.enter();
        try {

            Object xml = cx.getWrapFactory().wrap(cx, scope, xmlObject, XmlObject.class);
            Scriptable jsXML = cx.newObject(scope, "XML", new Object[] { xml });

            return jsXML;

        } finally {
            Context.exit();
        }
    }

    /**
     * Add the XML element name to use for an operation when converting from 
     * Java objects to XML. 
     *  
     * @param functionName
     * @param elementQN
     */
    public void addElementQName(String functionName, QName elementQN) {
        function2ElementMap.put(functionName, elementQN);
    }
}
