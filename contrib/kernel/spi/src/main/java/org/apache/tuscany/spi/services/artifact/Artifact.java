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
package org.apache.tuscany.spi.services.artifact;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Description of some packaged artifact such as a JAR file or a Composite.
 *
 * @version $Rev$ $Date$
 */
public class Artifact {

    /* Artifact group */
    private String group;

    /* Artifact name */
    private String name;

    /* Artifact version */
    private String version;

    /* Artifact classifier */
    private String classifier;

    /* Artifact type */
    private String type;

    /* Artifact url */
    private URL url;

    /* Transitive dependencies */
    private Set<Artifact> dependencies = new HashSet<Artifact>();

    /**
     * Adds a transitive dependency to the artifact.
     *
     * @param artifact Dependency to be added.
     */
    public void addDependency(Artifact artifact) {
        dependencies.add(artifact);
    }

    /**
     * Gets the URLs for all the transitive dependencies.
     *
     * @return Sets of URLs for all the transitive dependencies.
     */
    public Set<URL> getUrls() {

        Set<URL> urls = new HashSet<URL>();

        for (Artifact artifact : dependencies) {
            urls.add(artifact.getUrl());
        }
        urls.add(getUrl());

        return urls;

    }

    /**
     * Returns the name of a logical grouping to which this artifact belongs. For example, this might represent the
     * original publisher of the artifact.
     *
     * @return the name of a logical grouping to which this artifact belongs
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the name of a logical grouping to which this artifact belongs.
     *
     * @param group the name of a logical grouping to which this artifact belongs
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Returns the name of an artifact.
     *
     * @return the name of an artifact
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of an artifact.
     *
     * @param name the name of an artifact
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the version of an artifact.
     *
     * @return the version of an artifact
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of an artifact.
     *
     * @param version the version of an artifact
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns a way of classifying an artifact. This can be used to distinguish variants of an artifact that provide
     * the same function but which may have platform specific requirements. For example, it may contain the name of a
     * hardware platform for artifacts that contain native code.
     *
     * @return a way of classifying an artifact
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * Sets a way of classifying an artifact
     *
     * @param classifier a way of classifying an artifact
     */
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    /**
     * Returns the type of artifact.
     *
     * @return the type of artifact
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of artifact.
     *
     * @param type the type of artifact
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns a URL from which the artifact can be obtained.
     *
     * @return a URL from which the artifact can be obtained
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets a URL from which the artifact can be obtained.
     *
     * @param url a URL from which the artifact can be obtained
     */
    public void setUrl(URL url) {
        this.url = url;
    }


    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(':').append(name).append(':').append(version).append(':').append(type);
        return buf.toString();
    }
}
