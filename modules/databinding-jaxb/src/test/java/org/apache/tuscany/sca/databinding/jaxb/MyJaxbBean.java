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

package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

/**
 * 
 */
@XmlRootElement(name = "myBean", namespace = "http://ns1")
public class MyJaxbBean {
    public MyBean myBean;

    @XmlJavaTypeAdapter(AnyTypeXmlAdapter.class)
    public MyInterface myInterface;

    @XmlElement(type = MyInterfaceImpl.class)
    public MyInterface myInterface1;
    
    @XmlJavaTypeAdapter(MyInterfaceAdapter.class)
    public MyInterface myInterface2;

    public Object myObject;
    
    @XmlAnyElement
    public Element anyElement;
    
    public static class MyInterfaceAdapter extends XmlAdapter<MyInterfaceImpl, MyInterface> {

        @Override
        public MyInterfaceImpl marshal(MyInterface v) throws Exception {
            return (MyInterfaceImpl) v;
        }

        @Override
        public MyInterface unmarshal(MyInterfaceImpl v) throws Exception {
            return (MyInterface) v;
        }

    }
}
