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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;

import com.ibm.jvm.util.ByteArrayOutputStream;

public class DependencyUtils {

    public static List<String> getDependencies(String contributionURI, Map<String, ZipInputStream> possibles) throws ValidationException, IOException, ContributionReadException, XMLStreamException {

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
                    
                    contributionMetaDatas.put(curi, (ContributionMetadata)TuscanyRuntime.newInstance().getDeployer().loadXMLDocument(new StringReader(baos.toString())));                    
                }
           }
           zis.close(); // close it so no one tries to reuse the already read stream
        }
        return getDependencies(contributionMetaDatas, contributionURI);
    }

    public static List<String> getDependencies(Map<String, ContributionMetadata> possibles, String targetURI) throws ValidationException {   
        if (!possibles.containsKey(targetURI)) {
            throw new IllegalArgumentException(targetURI);
        }

        Set<String> dependencies = new HashSet<String>();

        // Go through the contribution imports
        for (Import import_ : possibles.get(targetURI).getImports()) {
            boolean resolved = false;

            // Go through all contribution candidates and their exports
            List<Export> matchingExports = new ArrayList<Export>();
            
            for (String dependencyURI : possibles.keySet()) {
                if (dependencyURI.equals(targetURI)) {
                    // Do not self import
                    continue;
                }
                ContributionMetadata dependency = possibles.get(dependencyURI);

                // When a contribution contains a reference to an artifact from a namespace that 
                // is declared in an import statement of the contribution, if the SCA artifact 
                // resolution mechanism is used to resolve the artifact, the SCA runtime MUST resolve 
                // artifacts from the locations identified by the import statement(s) for the namespace.
                if (import_ instanceof NamespaceImport) {
                        NamespaceImport namespaceImport = (NamespaceImport)import_;
                        if (namespaceImport.getLocation() != null)
                                if (!namespaceImport.getLocation().equals(dependencyURI)) 
                                        continue;
                }                
                if (import_ instanceof JavaImport) {
                        JavaImport javaImport = (JavaImport)import_;
                        if (javaImport.getLocation() != null)
                                if (!javaImport.getLocation().equals(dependencyURI)) 
                                        continue;
                }
                
                for (Export export : dependency.getExports()) {

                    // If an export from a contribution matches the import in hand
                    // add that contribution to the dependency set
                    if (import_.match(export)) {
                        resolved = true;
                        matchingExports.add(export);

                        if (!dependencies.contains(dependencyURI)) {
                            dependencies.add(dependencyURI);

                            // Now add the dependencies of that contribution
                            getDependencies(possibles, dependencyURI);

                        } // end if
                    } // end if 
                } // end for
            } // end for

            if (!resolved) {
                throw new ValidationException("Contribution " + targetURI + " has unresolved Import: " + import_);
            }
        }

        return new ArrayList<String>(dependencies);
    }
}
