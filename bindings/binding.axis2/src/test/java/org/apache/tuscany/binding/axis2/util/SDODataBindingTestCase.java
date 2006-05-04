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
package org.apache.tuscany.binding.axis2.util;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sdo.helper.XSDHelperImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.TypeHelper;

public class SDODataBindingTestCase extends TestCase {

    private SDODataBinding dataBinding;

    public void testToOMElement() {
        String s = "petra";

        OMElement omElement = dataBinding.toOMElement(new Object[] { s });
        assertNotNull(omElement);

        Object[] os = dataBinding.fromOMElement(omElement);
        assertNotNull(os);
        assertEquals(1, os.length);
        assertEquals(s, os[0]);
    }

    protected void setUp() throws Exception {
        super.setUp();
        DataObjectUtil.initRuntime();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            TypeHelper typeHelper = SDOUtil.createTypeHelper();
            URL url = getClass().getResource("helloworld.wsdl");
            new XSDHelperImpl(typeHelper).define(url.openStream(), null);

            QName getGreetingsQName = new QName("http://helloworldaxis.samples.tuscany.apache.org", "getGreetings");

            this.dataBinding = new SDODataBinding(typeHelper, getGreetingsQName);

        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

}
