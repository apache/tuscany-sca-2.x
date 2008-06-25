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

package org.apache.tuscany.sca.host.corba;

import org.omg.CORBA.Object;

/**
 * CORBA Service hosting interface
 */
public interface CorbaHost {

    /**
     * Registers servant in name server.
     * 
     * @param name binding name
     * @param host name server host
     * @param port name server port
     * @param serviceObject
     * @throws CorbaHostException
     */
    void registerServant(String name, String host, int port, Object serviceObject) throws CorbaHostException;

    /**
     * Removes servant from name server
     * 
     * @param name binding name
     * @param host name server host
     * @param port name server port
     * @throws CorbaHostException
     */
    void unregisterServant(String name, String host, int port) throws CorbaHostException;

    /**
     * Gets reference to object
     * 
     * @param name binding name
     * @param host name server host
     * @param port name server port
     * @return objects reference
     * @throws CorbaHostException
     */
    Object getReference(String name, String host, int port) throws CorbaHostException;

}
