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

package org.apache.tuscany.services.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.services.contribution.model.ContentType;
import org.apache.tuscany.services.contribution.model.Contribution;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionProcessor;
import org.apache.tuscany.services.spi.contribution.extension.ContributionProcessorExtension;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.implementation.java.IntrospectionRegistry;
import org.apache.tuscany.spi.implementation.java.Introspector;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

public class JavaContributionProcessor extends ContributionProcessorExtension implements ContributionProcessor {
    /**
     * Content-type that this processor can handle
     */
    public static final String CONTENT_TYPE = ContentType.JAVA;
    /**
     * Pojo introspector
     */
    private Introspector introspector;

    @Constructor
    public JavaContributionProcessor(@Reference IntrospectionRegistry introspector) {
        //this.introspector = introspector;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    private String getClazzName(URL clazzURL) {
        String clazzName;

        clazzName = clazzURL.toExternalForm().substring(clazzURL.toExternalForm().lastIndexOf("!/") + 2,
            clazzURL.toExternalForm().length() - ".class".length());
        clazzName = clazzName.replace("/", ".");

        return clazzName;
    }


    public void processContent(Contribution contribution, URI artifactURI, InputStream inputStream)
        throws ContributionException, IOException {
        if (artifactURI == null) {
            throw new IllegalArgumentException("Invalid null artifact uri.");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid null source inputstream.");
        }

        // TODO Auto-generated method stub

        try {
            CompositeClassLoader cl = new CompositeClassLoader(null, getClass().getClassLoader());
            cl.addURL(contribution.getLocation());

            String clazzName = getClazzName(contribution.getArtifact(artifactURI).getLocation());

            Class clazz = cl.loadClass(clazzName);

            //PojoComponentType javaInfo = introspector.introspect(null, clazz, null, null);

            //contribution.getArtifact(artifactURI).addModelObject(PojoComponentType.class, null, javaInfo);

        } catch (ClassNotFoundException cnfe) {
            throw new InvalidPojoComponentDefinitionlException(contribution.getArtifact(artifactURI).getLocation().toExternalForm(), cnfe);
        //} catch (ProcessingException pe) {
        //    throw new InvalidPojoComponentDefinitionlException(contribution.getArtifact(artifactURI).getLocation().toExternalForm(), pe);
        }
    }

    public void processModel(Contribution contribution, URI source, Object modelObject)
        throws ContributionException, IOException {
        // TODO Auto-generated method stub

    }

}
