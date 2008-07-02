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

/**
 * @version $Rev$ $Date$
 */
package org.apache.tuscany.sca.host.corba.naming;

import org.omg.CORBA.ORB;

/**
 * A stand-alone naming service launchable from a command line.
 */
public class TransientNameServer {
    private final TransientNameService service;
    private final Object flag = new Object();
    private Boolean started;

    public TransientNameServer(String host, int port, String serviceName) {
        this.service = new TransientNameService(host, port, serviceName);
    }

    public ORB getORB() {
        return service.getORB();
    }

    public Thread start() {
        Thread t = new Thread() {
            public void run() {
                ORB orb = null;
                synchronized (flag) {
                    try {
                        service.run();
                        orb = service.getORB();
                        started = Boolean.TRUE;
                    } catch (Throwable e) {
                        started = Boolean.FALSE;
                        throw new IllegalStateException(e);
                    } finally {
                        flag.notifyAll();
                    }
                }
                // Wait for requests
                orb.run();
            }
        };
        t.setDaemon(true);
        t.start();
        checkState();
        return t.isAlive() ? t : null;
    }

    private Boolean checkState() {
        synchronized (flag) {
            while (started == null) {
                try {
                    flag.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return started;
        }
    }

    public void stop() {
        if (started == Boolean.TRUE) {
            service.destroy();
        }
    }

    /**
     * Launch a name service as a stand alone process.  The
     * Host, port, and service name are controlled using
     * program arguments.
     *
     * @param args   The array of arguments for tailoring the service.
     *
     * @exception Exception
     */
    public static void main(String args[]) throws Exception {
        int port = TransientNameService.DEFAULT_SERVICE_PORT;
        String host = TransientNameService.DEFAULT_SERVICE_HOST;
        String serviceName = TransientNameService.DEFAULT_SERVICE_NAME;

        // see if we have
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ORBInitialPort")) {
                i++;
                if (i < args.length) {
                    port = java.lang.Integer.parseInt(args[i]);
                } else {
                    throw new IllegalArgumentException("Invalid -ORBInitialPort option");
                }
            } else if (args[i].equals("-ORBInitialHost")) {
                i++;
                if (i < args.length) {
                    host = args[i];
                } else {
                    throw new IllegalArgumentException("Invalid -ORBInitialHost option");
                }
            } else if (args[i].equals("-ORBServiceName")) {
                i++;
                if (i < args.length) {
                    serviceName = args[i];
                } else {
                    throw new IllegalArgumentException("Invalid -ORBServiceName option");
                }
            }

        }
        // create a services, and just spin it off.  We wait forever after that.
        TransientNameServer server = new TransientNameServer(host, port, serviceName);
        server.start().join();
    }

}
