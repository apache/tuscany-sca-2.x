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
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
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

    private FactoryExtensionPoint modelFactories;

    protected OSGiImplementationImpl(FactoryExtensionPoint modelFactories) {
        super(TYPE);
        this.modelFactories = modelFactories;
    }

    public OSGiImplementationImpl(FactoryExtensionPoint modelFactories, String bundleSymbolicName, String bundleVersion) {
        super(TYPE);
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
        this.modelFactories = modelFactories;
    }

    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public FactoryExtensionPoint getModelFactories() {
        return modelFactories;
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

    private boolean areEqual(Object obj1, Object obj2) {
        if (obj1 == obj2)
            return true;
        if (obj1 == null || obj2 == null)
            return false;
        return obj1.equals(obj2);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof OSGiImplementationImpl))
            return super.equals(obj);
        OSGiImplementationImpl impl = (OSGiImplementationImpl)obj;
        if (!areEqual(bundleSymbolicName, impl.bundleSymbolicName))
            return false;
        if (!areEqual(bundleVersion, impl.bundleVersion))
            return false;
        return super.equals(obj);
    }

    public void setBundleSymbolicName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

}
