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
package org.apache.tuscany.runtime.standalone;

import java.net.URL;

import org.apache.tuscany.host.runtime.TuscanyRuntime;

/**
 * Extends the tuscany runtime to add the behavious to deploy an
 * application SCDL.
 * 
 * @version $Revision$ $Date$
 *
 */
public interface StandaloneRuntime extends TuscanyRuntime<StandaloneRuntimeInfo> {
    
    /**
     * Deploys the specified application SCDL and runs the lauched component within the deployed composite.
     * 
     * @param applicationScdl Application SCDL that implements the composite.
     * @param applicationClassLoader Classloader used to deploy the composite.
     * @param args Arguments to be passed to the lauched component.
     * @deprecated This is a hack for deployment and should be removed.
     */
    int deployAndRun(URL applicationScdl, ClassLoader applicationClassLoader, String[] args) throws Exception;

}
