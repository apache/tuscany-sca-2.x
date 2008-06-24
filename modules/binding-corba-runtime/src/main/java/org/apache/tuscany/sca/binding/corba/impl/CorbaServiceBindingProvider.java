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
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class CorbaServiceBindingProvider implements ServiceBindingProvider {
    private CorbaBinding binding;

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#getBindingInterfaceContract()
     */
    public InterfaceContract getBindingInterfaceContract() {
        // TODO Auto-generated method stub
        return null;
    }

    protected Servant createServant() {
        return null;
    }

    private NameComponent[] nameComponents;

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#start()
     */
    public void start() {
        try {
            java.util.Properties props = new java.util.Properties();
            props.put("org.omg.CORBA.ORBInitialHost", binding.getHost());
            props.put("org.omg.CORBA.ORBInitialPort", String.valueOf(binding.getPort()));
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[0], props);

            Servant servant = createServant();

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);

            org.omg.CORBA.Object href = null; // AddHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name(binding.getURI());
            ncRef.rebind(path, href);

            // wait for invocations from clients
            orb.run();

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#stop()
     */
    public void stop() {
        try {
            java.util.Properties props = new java.util.Properties();
            props.put("org.omg.CORBA.ORBInitialHost", binding.getHost());
            props.put("org.omg.CORBA.ORBInitialPort", String.valueOf(binding.getPort()));
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[0], props);

            org.omg.CORBA.Object object = orb.resolve_initial_references("NameService");
            org.omg.CosNaming.NamingContextExt root = org.omg.CosNaming.NamingContextExtHelper.narrow(object);
            root.unbind(nameComponents);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

    }

    /**
     * @see org.apache.tuscany.sca.provider.ServiceBindingProvider#supportsOneWayInvocation()
     */
    public boolean supportsOneWayInvocation() {
        // TODO Auto-generated method stub
        return false;
    }

}
