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
package org.apache.tuscany.service.discovery.jxta;

import java.io.IOException;
import java.io.InputStream;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLElement;
import net.jxta.protocol.PipeAdvertisement;

/**
 * Utility class to construct a pipe advertidment message.
 * 
 * @author Dell
 *
 */
public abstract class AdvertismentHelper {

    /**
     * Utility class constructor.
     */
    private AdvertismentHelper() {
    }

    /**
     * Creates a pipe advertisment message for the domain.
     * 
     * @param domain Domain URI.
     * @param runtimeId Runtime Id.
     * @return Pipe advertisment message.
     */
    public static PipeAdvertisement getDomainAdvertisment(String domain, String runtimeId) {

        InputStream in = null;
        try {
            in = AdvertismentHelper.class.getClassLoader().getResourceAsStream("pipe.adv");
            StructuredDocument sd = StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, in);
            XMLElement root = (XMLElement) sd.getRoot();
            root.appendChild(sd.createElement("Domain", domain));
            root.appendChild(sd.createElement("RuntimeId", runtimeId));
            return (PipeAdvertisement)AdvertisementFactory.newAdvertisement(root);
        } catch (IOException ex) {
            throw new JxtaException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new JxtaException(ex);
            }
        }
        
    }

}
