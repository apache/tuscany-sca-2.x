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

package org.apache.tuscany.sca.host.corba.jse;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Default implementation of JSE CORBA host
 */
public class DefaultCorbaHost implements CorbaHost {
    private static final Logger logger = Logger.getLogger(DefaultCorbaHost.class.getName());

    private Map<String, ORB> orbs = new ConcurrentHashMap<String, ORB>();

    private void validatePort(int port) throws IllegalArgumentException {
        if (port < 1) {
            throw new IllegalArgumentException("Port value should be > 0");
        }
    }

    private NamingContextExt getNamingContext(ORB orb, String nameService) throws Exception {
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
        ORB orb = createORB(details.getHost(), details.getPort(), false);
        try {
            NamingContext namingCtx = getNamingContext(orb, details.getNameService());
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
            }
        } catch (CorbaHostException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void unregisterServant(String uri) throws CorbaHostException {
        CorbanameURL details = CorbaHostUtils.getServiceDetails(uri);
        ORB orb = createORB(details.getHost(), details.getPort(), false);
        try {
            NamingContextExt namingCtx = getNamingContext(orb, details.getNameService());
            namingCtx.unbind(namingCtx.to_name(details.getName()));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public Object lookup(String uri) throws CorbaHostException {
        Object result = null;
        try {
            CorbanameURL url = new CorbanameURL(uri);
            ORB orb = createORB(url.getHost(), url.getPort(), false);
            NamingContextExt context = getNamingContext(orb, url.getNameService());
            result = context.resolve_str(url.getName());
        } catch (Exception e) {
            handleException(e);
        }
        if (result == null) {
            throw new CorbaHostException(CorbaHostException.NO_SUCH_OBJECT);
        }
        return result;
    }

    public ORB createORB(String host, int port, boolean server) throws CorbaHostException {
        validatePort(port);

        String key = host + ":" + port;
        ORB orb = orbs.get(key);
        if (orb != null) {
            return orb;
        }
        // Create an ORB object
        Properties props = new Properties();
        props.putAll(System.getProperties());

        String portStr = String.valueOf(port);
        props.put("org.omg.CORBA.ORBServerId", "1000000");
        props.put("org.omg.CORBA.ORBInitialHost", host);
        props.put("org.omg.CORBA.ORBInitialPort", portStr);

        // STEP 1: Set ORBPeristentServerPort property
        // Set the proprietary property to open up a port to listen to
        // INS requests.

        if (server) {
            props.put("com.sun.CORBA.POA.ORBPersistentServerPort", portStr);
            props.put("com.ibm.CORBA.ListenerPort", portStr);
            props.put("gnu.CORBA.ListenerPort", portStr);
            // props.put("org.omg.CORBA.ORBClass",
            // "org.apache.yoko.orb.CORBA.ORB");
            // props.put("org.omg.CORBA.ORBSingletonClass",
            // "org.apache.yoko.orb.CORBA.ORBSingleton");
            props.put("yoko.orb.oa.endpoint", "iiop --host " + host + " --port " + port);
            props.put("yoko.orb.poamanager.TNameService.endpoint", "iiop --host " + host);
        }

        String[] args = {"-ORBInitialHost", host, "-ORBInitialPort", "" + port};
        orb = ORB.init(args, props);
        orbs.put(key, orb);
        return orb;
    }

    public void stop() {
        for (ORB orb : orbs.values()) {
            try {
                orb.shutdown(true);
                orb.destroy();
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}
