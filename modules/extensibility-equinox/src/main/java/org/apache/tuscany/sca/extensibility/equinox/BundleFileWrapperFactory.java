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

import java.io.File;
import java.io.IOException;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;
import org.eclipse.osgi.baseadaptor.hooks.BundleFileFactoryHook;
import org.eclipse.osgi.baseadaptor.hooks.BundleFileWrapperFactoryHook;

/**
 * Bundle file wrapper factory that converts plain jars into OSGi bundles 
 */
public class BundleFileWrapperFactory implements BundleFileWrapperFactoryHook, BundleFileFactoryHook {

    public BundleFile createBundleFile(Object content, BaseData data, boolean base) throws IOException {
        return null;
    }

    public BundleFile wrapBundleFile(BundleFile file, Object content, BaseData data, boolean base) throws IOException {
        if (data.getBundleID() == 0) {
            return null;
        }
        if (!(content instanceof File)) {
            return null;
        }
        return null;
    }

}
