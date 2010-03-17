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
package org.apache.tuscany.sca.implementation.osgi.impl;

import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * OSGi implementation
 *    All attributes from <implementation.osgi> have getters in this class
 * This class implements OSGiImplementationInterface which is associated with OSGiImplementationProvider.
 *
 * @version $Rev$ $Date$
 */
public class OSGiImplementationImpl extends ImplementationImpl implements OSGiImplementation {

    private String bundleSymbolicName;
    private String bundleVersion;
    private Bundle osgiBundle;

    protected OSGiImplementationImpl() {
        super(TYPE);
    }

    public OSGiImplementationImpl(String bundleSymbolicName, String bundleVersion) {
        super(TYPE);
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
    }

    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    /**
     * Since OSGi implementation annotations may not be processed until much later, leave it to
     * the OSGi invoker to decide whether pass-by-reference is allowed.
     * @return
     */
    public boolean isAllowsPassByReference() {
        return true;
    }

    public Bundle getBundle() {
        return osgiBundle;
    }

    public void setBundle(Bundle osgiBundle) {
        this.osgiBundle = osgiBundle;
        if (osgiBundle != null) {
            this.bundleSymbolicName = osgiBundle.getSymbolicName();
            this.bundleVersion = (String)osgiBundle.getHeaders().get(Constants.BUNDLE_VERSION);
        }
    }

    public void setBundleSymbolicName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bundleSymbolicName == null) ? 0 : bundleSymbolicName.hashCode());
        result = prime * result + ((bundleVersion == null) ? 0 : bundleVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OSGiImplementationImpl)) {
            return false;
        }
        OSGiImplementationImpl other = (OSGiImplementationImpl)obj;
        if (bundleSymbolicName == null) {
            if (other.bundleSymbolicName != null) {
                return false;
            }
        } else if (!bundleSymbolicName.equals(other.bundleSymbolicName)) {
            return false;
        }
        if (bundleVersion == null) {
            if (other.bundleVersion != null) {
                return false;
            }
        } else if (!bundleVersion.equals(other.bundleVersion)) {
            return false;
        }
        return true;
    }

}
