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
package org.apache.tuscany.sca.binding.ejb.util;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.ServiceUnavailableException;

public class EJBStubHelper {

    private static Object stub;
    private static ServiceRuntimeException exception;

    private EJBStubHelper() {
    }

    /**
     * @param owner
     * @param jndiName
     * @return
     */

    public static Object lookup(NamingEndpoint endpoint) {
        return getStub(endpoint);
    }

    public static Object getStub(NamingEndpoint namingEndpoint) {
        try {
            stub = EJBObjectFactory.createStub(namingEndpoint);
        } catch (NamingException e) {
            exception = new ServiceUnavailableException(e);
            e.printStackTrace();
            throw (ServiceUnavailableException)exception;
        } catch (CreateException e) {
            exception = new ServiceUnavailableException(e);
            throw (ServiceUnavailableException)exception;
        } catch (RemoteException e) {
            exception = new ServiceRuntimeException(e);
            throw (ServiceRuntimeException)exception;
        }

        if (exception == null) {
            return stub; // Normal result
        } else {
            throw exception; // Throw the exception
        }
    }

}
