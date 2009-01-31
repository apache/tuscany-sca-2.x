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
package service.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the service.jaxws package. 
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

    private final static QName _GetUsingMoreComplexObjectArg0_QNAME = new QName("", "arg0");
    private final static QName _GetUsingStringResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: service.jaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AnObject }
     * 
     */
    public AnObject createAnObject() {
        return new AnObject();
    }

    /**
     * Create an instance of {@link GetUsingStringResponse }
     * 
     */
    public GetUsingStringResponse createGetUsingStringResponse() {
        return new GetUsingStringResponse();
    }

    /**
     * Create an instance of {@link GetUsingMoreComplexObjectResponse }
     * 
     */
    public GetUsingMoreComplexObjectResponse createGetUsingMoreComplexObjectResponse() {
        return new GetUsingMoreComplexObjectResponse();
    }

    /**
     * Create an instance of {@link GetUsingString }
     * 
     */
    public GetUsingString createGetUsingString() {
        return new GetUsingString();
    }

    /**
     * Create an instance of {@link GetUsingMoreComplexObject }
     * 
     */
    public GetUsingMoreComplexObject createGetUsingMoreComplexObject() {
        return new GetUsingMoreComplexObject();
    }

    /**
     * Create an instance of {@link MoreComplexObject }
     * 
     */
    public MoreComplexObject createMoreComplexObject() {
        return new MoreComplexObject();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MoreComplexObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = GetUsingMoreComplexObject.class)
    public JAXBElement<MoreComplexObject> createGetUsingMoreComplexObjectArg0(MoreComplexObject value) {
        return new JAXBElement<MoreComplexObject>(_GetUsingMoreComplexObjectArg0_QNAME, MoreComplexObject.class,
                                                  GetUsingMoreComplexObject.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = GetUsingStringResponse.class)
    public JAXBElement<AnObject> createGetUsingStringResponseReturn(AnObject value) {
        return new JAXBElement<AnObject>(_GetUsingStringResponseReturn_QNAME, AnObject.class,
                                         GetUsingStringResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = GetUsingMoreComplexObjectResponse.class)
    public JAXBElement<AnObject> createGetUsingMoreComplexObjectResponseReturn(AnObject value) {
        return new JAXBElement<AnObject>(_GetUsingStringResponseReturn_QNAME, AnObject.class,
                                         GetUsingMoreComplexObjectResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = GetUsingString.class)
    public JAXBElement<String> createGetUsingStringArg0(String value) {
        return new JAXBElement<String>(_GetUsingMoreComplexObjectArg0_QNAME, String.class, GetUsingString.class, value);
    }

}
