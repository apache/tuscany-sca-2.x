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
package org.apache.tuscany.services.maven;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.spi.services.artifact.Artifact;
import org.codehaus.plexus.util.IOUtil;

/**
 * Helper class for resolving dependencies from WAR files.
 * 
 * @author Administrator
 *
 */
public class WarRepositoryHelper {
    
    /** WAR Repository URL */
    private URL reporsitoryUrl;
    
    /** Dependency metadata */
    private Map<String, Set<String>> transDependencyMap = new HashMap<String, Set<String>>();
    
    /**
     * Initializes the repository URL.
     * @param baseUrl Base URL.
     */
    @SuppressWarnings("unchecked")
    public WarRepositoryHelper(URL baseUrl) {
        
        
        InputStream transDepMapInputStream = null;
        try {
            
            reporsitoryUrl = new URL(baseUrl, "repository/");
            URL transDependencyMapUrl = new URL(baseUrl, "repository/dependency.metadata");
            transDepMapInputStream = transDependencyMapUrl.openStream();
            
            XMLDecoder decoder = new XMLDecoder(transDepMapInputStream);
            transDependencyMap = (Map<String, Set<String>>)decoder.readObject();
            decoder.close();
            
        } catch (MalformedURLException ex) {
            // throw new TuscanyDependencyException(ex);
        } catch (IOException  ex) {
            // throw new TuscanyDependencyException(ex);
        } finally {
            IOUtil.close(transDepMapInputStream);
        }
        
    }

    /**
     * Resolves the dependencies transitively.
     * 
     * @param artifact
     *            Artifact whose dependencies need to be resolved.
     * @throws TuscanyDependencyException
     *             If unable to resolve the dependencies.
     */
    public boolean resolveTransitively(Artifact rootArtifact) throws TuscanyDependencyException {
        
        String artKey = rootArtifact.getGroup() + "/" + rootArtifact.getName() + "/" + rootArtifact.getVersion() + "/";
        if(!transDependencyMap.containsKey(artKey)) {
            return false;
        }
        
        
        for(String dep : transDependencyMap.get(artKey)) {
            
            String[] tokens = dep.split("/");
            String artName = tokens[1];

            try {
                if(artName.equals(rootArtifact.getName())) {
                    rootArtifact.setUrl(new URL(reporsitoryUrl, dep));
                } else {      
                    Artifact depArtifact = new Artifact();
                    depArtifact.setGroup(tokens[0]);
                    depArtifact.setName(tokens[1]);
                    depArtifact.setVersion(tokens[2]);
                    depArtifact.setUrl(new URL(reporsitoryUrl, dep));
                    rootArtifact.addDependency(depArtifact);
                    
                }
            } catch (MalformedURLException ex) {
                throw new TuscanyDependencyException(ex);
            }      
        }
        return rootArtifact.getUrl()!=null;
    }

}
