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
package testClient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the testClient package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TestException_QNAME = new QName("http://test/", "TestException");
    private final static QName _InvokeTestArg0_QNAME = new QName("", "arg0");
    private final static QName _InvokeTestResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: testClient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InvokeTest }
     * 
     */
    public InvokeTest createInvokeTest() {
        return new InvokeTest();
    }

    /**
     * Create an instance of {@link InvokeTestResponse }
     * 
     */
    public InvokeTestResponse createInvokeTestResponse() {
        return new InvokeTestResponse();
    }

    /**
     * Create an instance of {@link TestException }
     * 
     */
    public TestException createTestException() {
        return new TestException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://test/", name = "TestException")
    public JAXBElement<TestException> createTestException(TestException value) {
        return new JAXBElement<TestException>(_TestException_QNAME, TestException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = InvokeTest.class)
    public JAXBElement<String> createInvokeTestArg0(String value) {
        return new JAXBElement<String>(_InvokeTestArg0_QNAME, String.class, InvokeTest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = InvokeTestResponse.class)
    public JAXBElement<String> createInvokeTestResponseReturn(String value) {
        return new JAXBElement<String>(_InvokeTestResponseReturn_QNAME, String.class, InvokeTestResponse.class, value);
    }

}
