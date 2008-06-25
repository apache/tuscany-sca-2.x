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

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 * Default implementation of CORBA host
 */
public class DefaultCorbaHost implements CorbaHost {

    private void validatePort(int port) throws IllegalArgumentException {
        if (port < 1) {
            throw new IllegalArgumentException("Port value should be > 0");
        }
    }

    private void validateName(String name) throws IllegalArgumentException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Object name shouldn't be null");
        }
    }

    private String[] getArgs(String host, int port) {
        String[] args = {"-ORBInitialHost", host, "-ORBInitialPort", "" + port};
        return args;
    }

    private NamingContext getNamingContext(String host, int port) throws Exception {
        ORB orb = ORB.init(getArgs(host, port), null);
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        return NamingContextHelper.narrow(objRef);
    }

    private void handleException(Exception e) throws CorbaHostException {
        // The cause of the Exception is JDK specific
        if (e instanceof NotFound) {
            throw new CorbaHostException(CorbaHostException.NO_SUCH_OBJECT, e);
        } else if (e instanceof InvalidName) {
            throw new CorbaHostException(e);
        } else {
            throw new CorbaHostException(e);
        }
    }

    public void registerServant(String name, String host, int port, Object servantObject) throws CorbaHostException {
        validatePort(port);
        validateName(name);
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        try {
            NamingContext ncRef = getNamingContext(host, port);
            NameComponent nc = new NameComponent(name, "");
            NameComponent[] path = {nc};
            try {
                ncRef.resolve(path);
                // no exception means that some object is already registered
                // under this name, we need to crash here
                throw new CorbaHostException(CorbaHostException.BINDING_IN_USE);
            } catch (NotFound e) {
                ncRef.bind(path, servantObject);
            }
        } catch (CorbaHostException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void unregisterServant(String name, String host, int port) throws CorbaHostException {
        validatePort(port);
        validateName(name);
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        try {
            NamingContext ncRef = getNamingContext(host, port);
            NameComponent nc = new NameComponent(name, "");
            NameComponent[] path = {nc};
            ncRef.unbind(path);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public Object getReference(String name, String host, int port) throws CorbaHostException {
        validatePort(port);
        validateName(name);
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        Object result = null;
        try {
            NamingContext ncRef = getNamingContext(host, port);
            NameComponent nc = new NameComponent(name, "");
            NameComponent path[] = {nc};
            result = ncRef.resolve(path);
        } catch (Exception e) {
            handleException(e);
        }
        return result;
    }

}
