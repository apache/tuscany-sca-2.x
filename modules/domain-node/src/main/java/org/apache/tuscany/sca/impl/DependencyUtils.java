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

package org.apache.tuscany.sca.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;

public class DependencyUtils {

    public static List<String> getDependencies(String contributionURI, Map<String, ZipInputStream> possibles) throws ValidationException, IOException, ContributionReadException, XMLStreamException {
        Deployer deployer = TuscanyRuntime.newInstance().getDeployer();

        Map<String, ContributionMetadata> contributionMetaDatas = new HashMap<String, ContributionMetadata>();
        for (String curi : possibles.keySet()) {
            ZipInputStream zis = possibles.get(curi);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (Contribution.SCA_CONTRIBUTION_META.equals(entry.getName())) {

                    byte[] buffer = new byte[2048];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(baos, buffer.length);

                    int size;
                    while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, size);
                    }
                    bos.close();
                    
                    contributionMetaDatas.put(curi, (ContributionMetadata)deployer.loadXMLDocument(new StringReader(baos.toString())));                    
                }
           }
           zis.close(); // close it so no one tries to reuse the already read stream
        }
        Monitor monitor = deployer.createMonitor();
        try {
            return deployer.getDependencies(contributionMetaDatas, contributionURI, monitor);
        } finally {
            monitor.analyzeProblems();
        }
    }
}
