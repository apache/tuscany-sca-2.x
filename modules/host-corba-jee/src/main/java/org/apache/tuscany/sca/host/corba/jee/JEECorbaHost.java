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

package org.apache.tuscany.sca.host.corba.jee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.host.corba.CorbaHostException;
import org.apache.tuscany.sca.host.corba.CorbaHostUtils;
import org.apache.tuscany.sca.host.corba.CorbanameURL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 * @version $Rev$ $Date$
 * Implementation of JEE CORBA host
 */
public class JEECorbaHost implements CorbaHost {

    public static final String ORB_NAME = "java:comp/ORB";

    private List<String> registeredServants = Collections.synchronizedList(new ArrayList<String>());
    private ORB orb;

    private void registerURI(String uri) {
        registeredServants.add(uri);
    }

    private void removeURI(String uri) {
        registeredServants.remove(uri);
    }

    private NamingContextExt getNamingContext(String nameService) throws Exception {
        org.omg.CORBA.Object objRef = orb.resolve_initial_references(nameService);
        return NamingContextExtHelper.narrow(objRef);
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

    public void registerServant(String uri, Object servantObject) throws CorbaHostException {
        CorbanameURL details = CorbaHostUtils.getServiceDetails(uri);
        try {
            NamingContext namingCtx = getNamingContext(details.getNameService());
            for (int i = 0; i < details.getNamePath().size() - 1; i++) {
                NameComponent nc = new NameComponent(details.getNamePath().get(i), "");
                NameComponent[] path = new NameComponent[] {nc};
                try {
                    namingCtx = NamingContextHelper.narrow(namingCtx.resolve(path));
                } catch (Exception e) {
                    namingCtx = namingCtx.bind_new_context(path);
                }
            }
            NameComponent finalName =
                new NameComponent(details.getNamePath().get(details.getNamePath().size() - 1), "");
            try {
                namingCtx.resolve(new NameComponent[] {finalName});
                // no exception means that some object is already registered
                // under this name, we need to crash here
                throw new CorbaHostException(CorbaHostException.BINDING_IN_USE);
            } catch (NotFound e) {
                namingCtx.bind(new NameComponent[] {finalName}, servantObject);
                registerURI(uri);
            }
        } catch (CorbaHostException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void unregisterServant(String uri) throws CorbaHostException {
        CorbanameURL details = CorbaHostUtils.getServiceDetails(uri);
        try {
            NamingContextExt namingCtx = getNamingContext(details.getNameService());
            namingCtx.unbind(namingCtx.to_name(details.getName()));
            removeURI(uri);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public Object lookup(String uri) throws CorbaHostException {
        Object result = null;
        try {
            CorbanameURL url = new CorbanameURL(uri);
            NamingContextExt context = getNamingContext(url.getNameService());
            result = context.resolve_str(url.getName());
        } catch (Exception e) {
            // e.printStackTrace();
            handleException(e);
        }
        if (result == null) {
            throw new CorbaHostException(CorbaHostException.NO_SUCH_OBJECT);
        }
        return result;
    }

    // obtain ORB from environment
    public void start() throws CorbaHostException {
        Context ctx;
        try {
            ctx = new InitialContext();
            orb = (ORB)PortableRemoteObject.narrow(ctx.lookup(ORB_NAME), org.omg.CORBA.ORB.class);
        } catch (Exception e) {
            throw new CorbaHostException(e);
        }
    }

    // unregister registered servants
    public void stop() {
        // use copy because unregisterServant(...) method removes from
        // registeredServants collection
        List<String> rsCopy = new ArrayList<String>(registeredServants);
        for (String servantURI : rsCopy) {
            try {
                unregisterServant(servantURI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
