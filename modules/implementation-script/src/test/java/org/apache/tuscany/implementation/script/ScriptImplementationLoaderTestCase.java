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

package org.apache.tuscany.implementation.script;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.script.ScriptImplementation;
import org.apache.tuscany.implementation.script.ScriptImplementationLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.easymock.EasyMock;

public class ScriptImplementationLoaderTestCase extends TestCase {

    private String XML_START =
        "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/2.0-alpha\">";
    private String XML_END = "</composite>";

    public void testLoadNamedScript() throws XMLStreamException, LoaderException {

        String xml =
            XML_START + "<implementation.script script=\"path/foo.py\" language=\"myLang\" class=\"myClass\" />"
                + XML_END;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
        reader.next();
        reader.next();

        LoaderRegistry reg = EasyMock.createNiceMock(LoaderRegistry.class);
        ScriptImplementationLoader loader = new ScriptImplementationLoader(reg);
        DeploymentContext deploymentContext = EasyMock.createNiceMock(DeploymentContext.class);
        ScriptImplementation impl = loader.load(null, reader, deploymentContext);

        assertEquals("path/foo.py", impl.getScriptName());
        assertEquals("myLang", impl.getScriptLanguage());
        assertEquals("myClass", impl.getScriptClassName());
        assertNull(impl.getInlineSrc());
    }

//    public void testLoadInlineScript() throws XMLStreamException, LoaderException {
//
//        String xml =
//            XML_START + "<implementation.script language=\"myLang\" class=\"myClass\">"
//                + "myScriptSrc"
//                + "</implementation.script>"
//                + XML_END;
//        XMLInputFactory factory = XMLInputFactory.newInstance();
//        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
//        reader.next();
//        reader.next();
//        
//        LoaderRegistry reg = EasyMock.createNiceMock(LoaderRegistry.class);
//        ScriptImplementationLoader loader = new ScriptImplementationLoader(reg);
//        DeploymentContext deploymentContext = EasyMock.createNiceMock(DeploymentContext.class);
//        ScriptImplementation impl = loader.load(null, reader, deploymentContext);
//
//        assertNull(impl.getScriptName());
//        assertEquals("myScriptSrc", impl.getInlineSrc());
//        assertEquals("myLang", impl.getScriptLanguage());
//        assertEquals("myClass", impl.getScriptClassName());
//    }
}
