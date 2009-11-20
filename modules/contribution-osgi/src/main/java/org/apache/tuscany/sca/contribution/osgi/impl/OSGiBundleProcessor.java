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

package org.apache.tuscany.sca.contribution.osgi.impl;

import java.util.List;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.osgi.BundleReference;
import org.osgi.framework.Bundle;

/**
 * OSGi bundle processor
 *
 * @version $Rev$ $Date$
 */
public class OSGiBundleProcessor {

    //    private boolean initializedOSGi;
    //    private OSGiRuntime osgiRuntime;

    public OSGiBundleProcessor() {
    }

    public Object installContributionBundle(Contribution contribution) {

        Object bundle = null;
        try {
            bundle = OSGiBundleActivator.installBundle(contribution.getLocation());
        } catch (Exception e) {
            // If OSGi cannot process the jar, treat the bundle as a plain jar file.
        }
        return bundle;
    }

    public BundleReference installNestedBundle(Contribution contribution,
                                               String bundleSymbolicName,
                                               String bundleVersion) {

        BundleReference bundleReference = null;

        List<Artifact> artifacts = contribution.getArtifacts();
        for (Artifact a : artifacts) {
            if (a.getURI().endsWith(".jar")) {
                try {
                    Bundle bundle = OSGiBundleActivator.installBundle(a.getLocation());
                    if (bundle != null) {
                        bundleReference = new BundleReference(bundle);
                        break;
                    }
                } catch (Exception e) {
                    // If OSGi cannot process the jar, treat the bundle as a plain jar file.
                }
            }
        }
        return bundleReference;
    }
}
