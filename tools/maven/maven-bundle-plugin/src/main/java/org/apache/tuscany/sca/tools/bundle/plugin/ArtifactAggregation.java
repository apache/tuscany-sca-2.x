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
package org.apache.tuscany.sca.tools.bundle.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;


public class ArtifactAggregation {
    private String symbolicName;
    private String version;
    private List<ArtifactMember> artifactMemebers = new ArrayList<ArtifactMember>();
    private transient List<Artifact> artifacts = new ArrayList<Artifact>();

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public void setSymbolicName(String symbolicName) {
        this.symbolicName = symbolicName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ArtifactMember> getArtifactMembers() {
        return artifactMemebers;
    }

    public void setArtifactMembers(List<ArtifactMember> artifacts) {
        this.artifactMemebers = artifacts;
    }

    public String toString() {
        return symbolicName + ";version=\"" + version + "\"\n" + artifactMemebers;
    }
    
    public boolean matches(Artifact artifact) {
        for(ArtifactMember m: artifactMemebers) {
            if(m.matches(artifact)) {
                return true;
            }
        }
        return false;
    }
}