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

package org.apache.tuscany.sca.core.databinding.transformers;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.databinding.xml.Node2XMLStreamReader;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2Node;
import org.oasisopen.sca.CallableReference;
import org.w3c.dom.Element;

/**
 * @version $Rev$ $Date$
 */
public class CallableReferenceXMLAdapter extends XmlAdapter<Element, CallableReference> {

    @Override
    public CallableReference unmarshal(Element v) throws Exception {
        Node2XMLStreamReader tf = new Node2XMLStreamReader();
        XMLStreamReader reader = tf.transform(v, null);
        XMLStreamReader2CallableReference t2 = new XMLStreamReader2CallableReference();
        return t2.transform(reader, null);
    }

    @Override
    public Element marshal(CallableReference v) throws Exception {
        CallableReference2XMLStreamReader t = new CallableReference2XMLStreamReader();
        XMLStreamReader reader = t.transform(v, null);
        XMLStreamReader2Node t2 = new XMLStreamReader2Node();
        return (Element) t2.transform(reader, null);
    }
}
