/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

             http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.     
 */
package org.apache.tuscany.container.javascript;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.xmlbeans.XmlObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.xml.XMLObject;

/**
 * This Interceptor encasulates the data mediation required  by the JavaScriptReferenceProxy.  The 
 * invocation handler of this class traps the javascript reference calls, performs data mediation 
 * and calls then calls the actual referred service.  This classes implementation is subject to 
 * review and change when the DataMediation infrastructure of Tuscany is ready.
 *
 */
public class JavaScriptRefInvocInterceptor implements InvocationHandler {
    private Object actualProxy;

    private Class wireInterface;

    private Scriptable instanceScope;

    JavaScriptRefInvocInterceptor(Object wireProxy, Class wireIfc, Scriptable instScope) {
        this.actualProxy = wireProxy;
        this.wireInterface = wireIfc;
        this.instanceScope = instScope;
    }

    public Object invoke(Object arg0, Method method, Object[] args) throws Throwable {
        // TODO Auto-generated method stub
        Context cx = Context.enter();
        try {
            Method invokedMethod = getInvokedMethod(method.getName());
            Object[] tranformedArgs = new Object[args.length];
            for (int count = 0; count < args.length; ++count) {
                tranformedArgs[count] = fromJavaScript(invokedMethod.getParameterTypes()[count], args[count]);
            }

            Object response = invokedMethod.invoke(actualProxy, tranformedArgs);
            response = toJavaScript(response, instanceScope, cx);
            return response;
        } finally {
            Context.exit();
        }
    }

    private Method getInvokedMethod(String methodName) {
        Method[] methods = wireInterface.getMethods();

        for (int count = 0; count < methods.length; ++count) {
            if (methods[count].getName().equals(methodName)) {
                return methods[count];
            }
        }
        throw new RuntimeException("Unable to find invocation method");
    }

    protected Object fromJavaScript(Class reqArgType, Object jsArg) throws Exception {
        Object javaArg;
        if (Context.getUndefinedValue().equals(jsArg)) {
            javaArg = null;
        } else if (jsArg instanceof XMLObject) {
            // TODO: E4X Bug? Shouldn't need this copy, but without it the outer element gets lost???
            Scriptable jsXML = (Scriptable) ScriptableObject.callMethod((Scriptable) jsArg, "copy", new Object[0]);
            Wrapper wrapper = (Wrapper) ScriptableObject.callMethod(jsXML, "getXmlObject", new Object[0]);
            javaArg = wrapper.unwrap();

            XMLStreamReader xmlReader = StAXUtils.createXMLStreamReader(new ByteArrayInputStream(javaArg.toString().getBytes()));
            StAXOMBuilder staxOMBuilder = new StAXOMBuilder(OMAbstractFactory.getOMFactory(), xmlReader);
            javaArg = staxOMBuilder.getDocumentElement();
        } else if (jsArg instanceof Wrapper) {
            javaArg = ((Wrapper) jsArg).unwrap();
        } else {
            if (reqArgType != null) {
                javaArg = Context.jsToJava(jsArg, reqArgType);
            } else {
                javaArg = Context.jsToJava(jsArg, String.class);
            }
        }

        return javaArg;
    }

    protected Object toJavaScript(Object retVal, Scriptable scope, Context cx) throws RuntimeException {
        Object jsRetVal;
        if (retVal instanceof OMElement) {
            try {
                XmlObject xmlObject = XmlObject.Factory.parse(retVal.toString());
                Object jsXML = cx.getWrapFactory().wrap(cx, scope, xmlObject, XmlObject.class);
                jsRetVal = cx.newObject(scope, "XML", new Object[] { jsXML });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (retVal instanceof XmlObject) {
            Object jsXML = cx.getWrapFactory().wrap(cx, scope, (XmlObject) retVal, XmlObject.class);
            jsRetVal = cx.newObject(scope, "XML", new Object[] { jsXML });
        } else {
            jsRetVal = Context.toObject(retVal, scope);
        }

        return jsRetVal;
    }
}
