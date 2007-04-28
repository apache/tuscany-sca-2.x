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
package calculator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



/**
 * An implementation of the Calculator service.
 */
public class CalculatorServiceRmiImpl extends UnicastRemoteObject implements CalculatorService {

    private static final long serialVersionUID = -1543948944662001428L;
    private AddService addService = new AddServiceImpl();
    private SubtractService subtractService = new SubtractServiceImpl();
    private MultiplyService multiplyService = new MultiplyServiceImpl();
    private DivideService divideService = new DivideServiceImpl();
    
    public CalculatorServiceRmiImpl() throws RemoteException {
        super();       
    }

    public double add(double n1, double n2) throws RemoteException {
        return addService.add(n1, n2);
    }

    public double subtract(double n1, double n2) {
        return subtractService.subtract(n1, n2);
    }

    public double multiply(double n1, double n2) {
        return multiplyService.multiply(n1, n2);
    }

    public double divide(double n1, double n2) {
        return divideService.divide(n1, n2);
    }
}
