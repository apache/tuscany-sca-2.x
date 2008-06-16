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

package org.apache.tuscany.sca.binding.corba.impl;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.PortableServer.DynamicImplementation;
import org.omg.PortableServer.POA;


/**
 * This is the CORBA DSI servant that dispatches CORBA requests to SCA components
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyServant extends DynamicImplementation {
    private ORB orb;
    
    @Override
    public void invoke(ServerRequest request) {       
    }

    @Override
    public String[] _all_interfaces(POA poa, byte[] objectId) {
        // TODO Auto-generated method stub
        return null;
    }

}
