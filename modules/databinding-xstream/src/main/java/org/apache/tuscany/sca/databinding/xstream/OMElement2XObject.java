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
package org.apache.tuscany.sca.databinding.xstream;

import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;

import com.thoughtworks.xstream.XStream;

/**
 * Job DataObject --> AXIOM OMElement transformer
 * 
 * @version $Rev$ $Date$
 */
public class OMElement2XObject extends BaseTransformer<OMElement, XObject> implements
    PullTransformer<OMElement, XObject> {

    public XObject transform(OMElement source, TransformationContext context) {

        XStream xs = new XStream();
        xs.alias("xobject", MetaObjectImpl.class);
        MetaObjectImpl mo;
        try {
            java.io.StringWriter writer = new StringWriter();
            source.serialize(writer);
            String w = writer.toString();
            // FIXME: a better way to get eliminate param0
            w = w.replaceAll("param0", "xobject");
            w = w.replaceAll("xmlns=\"\"", "");
            mo = (MetaObjectImpl)xs.fromXML(w.trim());
            return mo.getInstance();
        } catch (XMLStreamException e) {

            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class<OMElement> getSourceType() {
        return OMElement.class;
    }

    @Override
    public Class<XObject> getTargetType() {
        return XObject.class;
    }

    @Override
    public int getWeight() {
        return 10;
    }

}
