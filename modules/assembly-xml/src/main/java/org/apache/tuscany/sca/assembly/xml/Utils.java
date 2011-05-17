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

package org.apache.tuscany.sca.assembly.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class Utils {
    
    /**
     * Helper method to get the XML string for a model object.
     */
    public static String modelToXML(Base model, boolean pretty, ExtensionPointRegistry extensionPointRegistry) {
        try {
            StAXHelper stAXHelper = StAXHelper.getInstance(extensionPointRegistry);
            StAXArtifactProcessorExtensionPoint staxProcessors = extensionPointRegistry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
            ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, null, stAXHelper.getOutputFactory());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            staxProcessor.write(model, bos, new ProcessorContext(extensionPointRegistry));
            bos.close();

            if (!pretty) {
                return bos.toString();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document document = parser.parse(new ByteArrayInputStream(bos.toByteArray()));        
            
            DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation().getFeature("LS", "3.0");
            LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
            lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            LSOutput lsOutput = domImplementationLS.createLSOutput();
            lsOutput.setEncoding("UTF-8");
            StringWriter stringWriter = new StringWriter();
            lsOutput.setCharacterStream(stringWriter);
            lsSerializer.write(document, lsOutput);
            return stringWriter.toString();

        } catch (ContributionWriteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
