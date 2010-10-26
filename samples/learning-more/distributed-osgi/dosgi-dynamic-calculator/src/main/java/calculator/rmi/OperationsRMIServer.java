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

package calculator.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 */
public class OperationsRMIServer implements OperationsRemote, Serializable {

    private static final long serialVersionUID = 6081008315263103012L;
    private transient Registry registry;

    public OperationsRMIServer() throws RemoteException {
        super();
    }

    public double add(double n1, double n2) {
        return n1 + n2;
    }

    public double subtract(double n1, double n2) {
        return n1 - n2;
    }

    public double divide(double n1, double n2) {
        return n1 / n2;
    }

    public double multiply(double n1, double n2) {
        return n1 * n2;
    }

    public void start() throws RemoteException {
        Thread thread = new Thread() {
            public void run() {
                try {
                    System.out.println("Starting the RMI server for calculator operations...");
                    Remote stub = UnicastRemoteObject.exportObject(OperationsRMIServer.this);
                    registry = LocateRegistry.createRegistry(8085);
                    registry.bind("AddService", stub);
                    registry.bind("SubtractService", stub);
                    registry.bind("MultiplyService", stub);
                    registry.bind("DivideService", stub);
                    System.out.println("RMI server for calculator operations is now started.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void stop() {
        if (registry != null) {
            try {
                registry.unbind("AddService");
                registry.unbind("SubtractService");
                registry.unbind("MultiplyService");
                registry.unbind("DivideService");
                UnicastRemoteObject.unexportObject(this, false);
                UnicastRemoteObject.unexportObject(registry, false);
                registry = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
