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

package org.apache.tuscany.sca.contribution.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * A reference to an OSGi bundle
 *
 * @version $Rev$ $Date$
 */
public class BundleReference {

    /**
     * The bundle.
     */
    private Bundle bundle;

    /**
     * The bundle version.
     */
    private String version;

    /**
     * The bundle name and version.
     */
    private String symbolicName;

    /**
     * Constructs a new BundleReference.
     * 
     * @param bundle
     */
    public BundleReference(Bundle bundle) {
        this.bundle = bundle;
        this.symbolicName = bundle.getSymbolicName();
        this.version = (String)bundle.getHeaders().get(Constants.BUNDLE_VERSION);
    }

    /**
     * Constructs a new BundleReference.
     * 
     * @param bundleSymbolicName The bundle symbolic name
     * @param bundleVersion The bundle version
     */
    public BundleReference(String bundleSymbolicName, String bundleVersion) {
        this.version = bundleVersion;
        this.symbolicName = bundleSymbolicName;
    }

    /**
     * Get the referenced bundle.
     * 
     * @return The referenced bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Get the referenced bundle version.
     * 
     * @return The bundle version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the referenced bundle name and version.
     * 
     * @return The bundle name
     */
    public String getSymbolicName() {
        return symbolicName;
    }

    /**
     * Returns true if the bundle reference is unresolved.
     * 
     * @return Whether or not the bundle has been resolved
     */
    public boolean isUnresolved() {
        return bundle == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbolicName == null) ? 0 : symbolicName.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BundleReference other = (BundleReference)obj;
        if (symbolicName == null) {
            if (other.symbolicName != null)
                return false;
        } else if (!symbolicName.equals(other.symbolicName))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

}
