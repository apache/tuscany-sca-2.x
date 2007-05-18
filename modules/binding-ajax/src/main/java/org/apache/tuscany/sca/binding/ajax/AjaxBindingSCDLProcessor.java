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

package org.apache.tuscany.sca.binding.ajax;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * A StAXArtifactProcessor to handle the <binding.ajax> SCDL
 */
public class AjaxBindingSCDLProcessor implements StAXArtifactProcessor {

    public static QName AJAX_BINDING_QNAME = new QName(Constants.SCA10_NS, "binding.ajax");

    public QName getArtifactType() {
        return AJAX_BINDING_QNAME;
    }
    public Class getModelType() {
        return AjaxBinding.class;
    }

    public Object read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        AjaxBinding dwrBinding = new AjaxBinding();
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && AJAX_BINDING_QNAME.equals(reader.getName())) {
                break;
            }
        }
        return dwrBinding;
    }

    public void write(Object arg0, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        writer.writeStartElement(Constants.SCA10_NS, AJAX_BINDING_QNAME.getLocalPart());
        
        writer.writeEndElement();
    }


    public void resolve(Object arg0, ModelResolver arg1) throws ContributionResolveException {
        // not needed by Ajax binding
    }

}
