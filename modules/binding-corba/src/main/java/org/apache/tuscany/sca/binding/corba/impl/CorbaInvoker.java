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

import org.apache.tuscany.sca.binding.corba.CorbaBinding;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.omg.CORBA.Any;
import org.omg.CORBA.Request;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class CorbaInvoker implements Invoker {
    private Operation operation;
    private CorbaBinding binding;

    public CorbaInvoker(CorbaBinding binding, Operation operation) {
        super();
        this.binding = binding;
        this.operation = operation;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Invoker#invoke(org.apache.tuscany.sca.invocation.Message)
     */
    public Message invoke(Message msg) {
        try {
            org.omg.CORBA.ORB orb = initORB(binding.getHost(), String.valueOf(binding.getPort()));

            org.omg.CORBA.Object service = orb.string_to_object(binding.getURI());

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt instead of NamingContext. This is 
            // part of the Interoperable Naming Service.  
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            org.omg.CORBA.Object impl = ncRef.resolve_str(binding.getURI());

            String op = operation.getName();
            Request req = impl._request(op);
            Any any = req.add_in_arg();
            return null;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private org.omg.CORBA.ORB initORB(String host, String port) {
        java.util.Properties props = new java.util.Properties();
        props.put("org.omg.CORBA.ORBInitialHost", host);
        props.put("org.omg.CORBA.ORBInitialPort", port);
        org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[0], props);
        return orb;
    }

}
