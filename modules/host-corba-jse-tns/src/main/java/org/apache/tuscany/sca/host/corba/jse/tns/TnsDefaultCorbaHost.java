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

package org.apache.tuscany.sca.host.corba.jse.tns;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.host.corba.CorbaHostException;
import org.apache.tuscany.sca.host.corba.CorbanameURL;
import org.apache.tuscany.sca.host.corba.SocketUtil;
import org.apache.tuscany.sca.host.corba.jse.DefaultCorbaHost;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.omg.CORBA.Object;

/**
 * @version $Rev$ $Date$
 * Default implementation of CORBA host
 */
public class TnsDefaultCorbaHost implements CorbaHost {
    
    private static final Logger logger = Logger.getLogger(TnsDefaultCorbaHost.class.getName());
    private Map<Integer, TransientNameServer> localServers = new ConcurrentHashMap<Integer, TransientNameServer>();
    private Map<Integer, Integer> clientsCount = new ConcurrentHashMap<Integer, Integer>();
    private CorbaHost targetHost = new DefaultCorbaHost();

    public void registerServant(String uri, Object servantObject) throws CorbaHostException {
        CorbanameURL details = new CorbanameURL(uri);
        if (SocketUtil.isLocalhost(details.getHost())) {
            createLocalNameServer(details.getPort());
        }
        targetHost.registerServant(uri, servantObject);
    }

    public void unregisterServant(String uri) throws CorbaHostException {
        targetHost.unregisterServant(uri);
        CorbanameURL details = new CorbanameURL(uri);
        if (SocketUtil.isLocalhost(details.getHost())) {
            releaseLocalNameServer(details.getPort());
        }
    }

    public Object lookup(String uri) throws CorbaHostException {
        return targetHost.lookup(uri);
    }

    /**
     * Starts transient name server under given port. If TNS was previously
     * spawned it increments clients counter.
     */
    synchronized private void createLocalNameServer(int port) throws CorbaHostException {
        int useCount = clientsCount.containsKey(port) ? clientsCount.get(port) : 0;
        // no server previously spawned
        if (useCount == 0) {
            TransientNameServer server =
                new TransientNameServer("localhost", port, TransientNameService.DEFAULT_SERVICE_NAME);
            Thread thread = server.start();
            if (thread == null) {
                throw new CorbaHostException("TransientNameServer couldn't be started");
            } else {
                localServers.put(port, server);
            }
        }
        clientsCount.put(port, ++useCount);
    }

    /**
     * Stops transient name server if there is only one client left using such
     * TNS. Decrements clients counter if TNS is used by 2 or more clients.
     */
    synchronized private void releaseLocalNameServer(int port) throws CorbaHostException {
        int useCount = clientsCount.containsKey(port) ? clientsCount.get(port) : 0;
        if (useCount == 1) {
            // last client executed stop, cleaning up
            TransientNameServer server = localServers.get(port);
            if (server != null) {
                server.stop();
                clientsCount.remove(port);
                localServers.remove(port);
            } else {
                logger.warning("Local name server on port " + port + " was null!");
            }
        } else if (useCount > 1) {
            clientsCount.put(port, --useCount);
        } else {
            logger.warning("Tried to release non existing local name server on port " + port);
        }

    }

    public void stop() {
        for (TransientNameServer tns : localServers.values()) {
            tns.stop();
        }
    }

}
