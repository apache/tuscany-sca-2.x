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

package org.apache.tuscany.sca.implementation.bpel.ode;

import java.io.File;

/**
 * Deployment information
 * 
 * @version $Rev$ $Date$
 */
public class ODEDeployment {
    /** The directory containing the deploy.xml and artefacts. */
    public File deployDir;

    /** If non-null the type of exception we expect to get when we deploy. */
    public Class expectedException = null;

    public ODEDeployment(File deployDir) {
        this.deployDir = deployDir;
    }

    public String toString() {
        return "Deployment#" + deployDir;
    }
}