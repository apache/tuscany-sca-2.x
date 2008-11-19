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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2String;
import org.osoa.sca.CallableReference;

/**
 * @version $Rev$ $Date$
 */
public class CallableReferenceXMLAdapter extends XmlAdapter<Source, CallableReference> {
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    @Override
    public CallableReference unmarshal(Source v) throws Exception {
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        transformerFactory.newTransformer().transform(v, result);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(sw.toString()));
        XMLStreamReader2CallableReference t2 = new XMLStreamReader2CallableReference();
        return t2.transform(reader, null);
    }

    @Override
    public Source marshal(CallableReference v) throws Exception {
        CallableReference2XMLStreamReader t = new CallableReference2XMLStreamReader();
        XMLStreamReader reader = t.transform(v, null);
        XMLStreamReader2String t2 = new XMLStreamReader2String();
        String xml = t2.transform(reader, null);
        return new StreamSource(new StringReader(xml));
    }
}
