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


/**
 * A weak reference to a class, which should be used to register classes
 * with an ArtifactResolver and resolve these classes later.
 *
 * @version $Rev$ $Date$
 */
public class BundleReference {

    /**
     * The bundle.
     */
    private Object bundle;

    /**
     * The bundle name.
     */
    private String bundleName;

    /**
     * The bundle version.
     */
    private String bundleVersion;

    /**
     * The bundle name and version.
     */
    private String bundleUniqueName;

    /**
     * The bundle relative path.
     */
    private String bundleRelativePath;

    /**
     * Constructs a new BundleReference.
     * 
     * @param bundle The bundle reference
     * @param bundleName The bundle name
     * @param bundleVersion The bundle version
     * @param bundleRelativePath The relative path for the bundle
     */
    public BundleReference(Object bundle, String bundleName, String bundleVersion, String bundleRelativePath) {
        this.bundle = bundle;
        this.bundleName = bundleName;
        this.bundleVersion = bundleVersion;       
        this.bundleRelativePath = bundleRelativePath;
        this.bundleUniqueName = bundleName + "(" + (bundleVersion == null?"0.0.0":bundleVersion) + ")";
    }
    
    /**
     * Constructs a new BundleReference.
     * 
     * @param bundleName The bundle name
     * @param bundleVersion The bundle version
     */
    public BundleReference(String bundleName, String bundleVersion) {
        this.bundleName = bundleName;
        this.bundleVersion = bundleVersion;
        this.bundleUniqueName = bundleName + "(" + (bundleVersion == null?"0.0.0":bundleVersion) + ")";
    }
    
    /**
     * Get the referenced bundle.
     * 
     * @return The referenced bundle
     */
    public Object getBundle() {
        return bundle;
    }
    
    /**
     * Get the referenced bundle name.
     * 
     * @return The bundle name
     */
    public String getBundleName() {
        return bundleName;
    }
    
    /**
     * Get the referenced bundle version.
     * 
     * @return The bundle version
     */
    public String getBundleVersion() {
        return bundleVersion;
    }
    
    /**
     * Get the referenced bundle name and version.
     * 
     * @return The bundle name
     */
    public String getBundleUniqueName() {
        return bundleUniqueName;
    }
    
    /**
     * Get the relative location of the bundle inside its contribution.
     * 
     * @return The bundle path
     */
    public String getBundleRelativePath() {
        return bundleRelativePath;
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
        return bundleUniqueName.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof BundleReference) {
                BundleReference ref = (BundleReference)obj;
                return bundleName.equals(ref.bundleName) &&
                       (bundleVersion == null || ref.bundleVersion == null ||
                        bundleVersion.equals(ref.bundleVersion));
            } else {
                return false;
            }
        }
    }

}
