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

package org.apache.tuscany.contribution.processor.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.contribution.processor.PackageProcessor;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;

/**
 * The base class for ContributionPackageProcessor implementations
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractContributionPackageProcessor implements PackageProcessor {
    /**
     * The ContributionProcessorRegistry that this processor should register
     * with; usually set by injection. This registry may also be used to process
     * other sub-artifacts.
     */
    protected final PackageProcessorExtensionPoint packageProcessors;

    /**
     * @param packageProcessors the registry to set
     */
    public AbstractContributionPackageProcessor(PackageProcessorExtensionPoint packageProcessors) {
        this.packageProcessors = packageProcessors;
        this.packageProcessors.register(this.getPackageType(), this);
    }

    public URL getArtifactURL(URL packageSourceURL, URI artifact) throws MalformedURLException {
        return new URL(packageSourceURL, artifact.toString());
    }

    /**
     * Returns the type of package handled by this package processor.
     * 
     * @return the type of package handled by this package processor
     */
    public abstract String getPackageType();

}
