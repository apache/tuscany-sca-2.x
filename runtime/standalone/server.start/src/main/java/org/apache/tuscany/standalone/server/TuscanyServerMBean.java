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
package org.apache.tuscany.standalone.server;

/**
 * Management interface for the tuscany server.
 * 
 * @version $Revision$ $Date$
 *
 */
public interface TuscanyServerMBean {

    /**
     * Starts a runtime specified by the bootpath.
     * 
     * @param profileName Profile for the runtime.
     */
    public void startRuntime(String profileName);

    /**
     * Shuts down a runtime specified by the bootpath.
     * 
     * @param bootPath Bootpath for the runtime.
     */
    public void shutdownRuntime(String bootPath);

    /**
     * Starts the server.
     *
     */
    public void shutdown();

}
