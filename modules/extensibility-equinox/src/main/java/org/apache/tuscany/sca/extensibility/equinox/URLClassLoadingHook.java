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

package org.apache.tuscany.sca.extensibility.equinox;

import java.security.ProtectionDomain;
import java.util.ArrayList;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

/**
 * 
 */
public class URLClassLoadingHook implements ClassLoadingHook {

    /* (non-Javadoc)
     * @see org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#addClassPathEntry(java.util.ArrayList, java.lang.String, org.eclipse.osgi.baseadaptor.loader.ClasspathManager, org.eclipse.osgi.baseadaptor.BaseData, java.security.ProtectionDomain)
     */
    public boolean addClassPathEntry(ArrayList cpEntries,
                                     String cp,
                                     ClasspathManager hostmanager,
                                     BaseData sourcedata,
                                     ProtectionDomain sourcedomain) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#createClassLoader(java.lang.ClassLoader, org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate, org.eclipse.osgi.framework.adaptor.BundleProtectionDomain, org.eclipse.osgi.baseadaptor.BaseData, java.lang.String[])
     */
    public BaseClassLoader createClassLoader(ClassLoader parent,
                                             ClassLoaderDelegate delegate,
                                             BundleProtectionDomain domain,
                                             BaseData data,
                                             String[] bundleclasspath) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#findLibrary(org.eclipse.osgi.baseadaptor.BaseData, java.lang.String)
     */
    public String findLibrary(BaseData data, String libName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#getBundleClassLoaderParent()
     */
    public ClassLoader getBundleClassLoaderParent() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#initializedClassLoader(org.eclipse.osgi.baseadaptor.loader.BaseClassLoader, org.eclipse.osgi.baseadaptor.BaseData)
     */
    public void initializedClassLoader(BaseClassLoader baseClassLoader, BaseData data) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#processClass(java.lang.String, byte[], org.eclipse.osgi.baseadaptor.loader.ClasspathEntry, org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry, org.eclipse.osgi.baseadaptor.loader.ClasspathManager)
     */
    public byte[] processClass(String name,
                               byte[] classbytes,
                               ClasspathEntry classpathEntry,
                               BundleEntry entry,
                               ClasspathManager manager) {
        // TODO Auto-generated method stub
        return null;
    }

}
