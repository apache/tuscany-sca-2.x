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

package org.apache.tuscany.sca.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.TypeDescriber;
import org.apache.tuscany.sca.contribution.service.UnsupportedPackageTypeException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Implementation of an extensible package processor.
 * 
 * Takes a package processor extension point and delegates to the proper package
 * processor from the extension point based on the package's content type.
 * 
 * @version $Rev$ $Date$
 */
public class ExtensiblePackageProcessor implements PackageProcessor {

    private PackageProcessorExtensionPoint processors;
    private TypeDescriber packageTypeDescriber;
    private Monitor monitor;

    public ExtensiblePackageProcessor(PackageProcessorExtensionPoint processors, 
    								  TypeDescriber packageTypeDescriber,
    								  Monitor monitor) {
        this.processors = processors; 
        this.packageTypeDescriber = packageTypeDescriber;
        this.monitor = monitor;
    }
    
    /**
     * Marshals errors into the monitor
     * 
     * @param problems
     * @param message
     * @param model
     */
    protected void error(String message, Object model, Object... messageParameters) {
    	if (monitor != null) {
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	}
    }

    public List<URI> getArtifacts(URL packageSourceURL, InputStream inputStream) 
        throws ContributionException, IOException {
        String packageType = this.packageTypeDescriber.getType(packageSourceURL, null);
        if (packageType == null) {
        	error("UnsupportedPackageTypeException", packageTypeDescriber, packageSourceURL.toString());
            throw new UnsupportedPackageTypeException("Unsupported contribution package type: " + packageSourceURL.toString());
        }

        PackageProcessor packageProcessor = this.processors.getPackageProcessor(packageType);
        if (packageProcessor == null) {
        	error("UnsupportedPackageTypeException", packageTypeDescriber, packageType);
            throw new UnsupportedPackageTypeException("Unsupported contribution package type: " + packageType);
        }

        return packageProcessor.getArtifacts(packageSourceURL, inputStream);
    }

    public URL getArtifactURL(URL packageSourceURL, URI artifact) throws MalformedURLException {
        String contentType = this.packageTypeDescriber.getType(packageSourceURL, null);
        PackageProcessor packageProcessor = this.processors.getPackageProcessor(contentType);
        return packageProcessor.getArtifactURL(packageSourceURL, artifact);
    }
    
    public String getPackageType() {
        return null;
    }
}
