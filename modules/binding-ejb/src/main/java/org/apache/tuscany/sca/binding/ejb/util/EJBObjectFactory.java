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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.naming.NamingException;
import javax.rmi.CORBA.Util;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.stub.java.rmi._Remote_Stub;
import org.osoa.sca.ServiceRuntimeException;

public final class EJBObjectFactory {

    private EJBObjectFactory() {
    }

    /**
     * Get either a generated of dynamic EJB stub using the specified JNDI
     * properties.
     * <p>
     * The returned stub will implement the specified stubInterface Interface.
     * If the underlying EJB stub is not assignable from the specified
     * stubInterface then a proxy is used to convert between the two.
     * <p>
     * The returned EJB stub may be either the pregenerated EJB stub or a
     * dynamic stub. This allows a client to invoke an EJB without requiring any
     * of the pregenerated EJB stub classes be avaiable in the classpath.
     * <p>
     */
    public static Object createStub(NamingEndpoint namingEndpoint, InterfaceInfo ejbInterface) throws NamingException,
        RemoteException, CreateException {

        EJBLocator locator = namingEndpoint.getLocator();
        Object homeObject = locator.locate(namingEndpoint.getJndiName());
        /*
         * The type of the object returned from the lookup is as follows: If the
         * generated stub exists on the classpath, it's an instance of that
         * type, otherwise, "org.omg.stub.java.rmi._Remote_Stub" or
         * "org.omg.stub.javax.ejb._EJBHome_Stub"
         */
        Object stub = getEJBStub(homeObject, ejbInterface);
        // Cache dynamic stub only
        return stub;
    }

    /**
     * @param homeObject
     * @param ejbHomeClass
     * @return
     * @throws RemoteException
     */
    protected static Object getEJBStub(Object homeObject, InterfaceInfo ejbInterface) throws RemoteException,
        CreateException {

        Object stub = null;

        // Get the business interface of the EJB 
        Class ejbInterfaceClass = null;
        try {
            ejbInterfaceClass = Thread.currentThread().getContextClassLoader().loadClass(ejbInterface.getName());
        } catch (ClassNotFoundException e) {
            // ignore
        }

        if (ejbInterfaceClass != null && ejbInterfaceClass.isInstance(homeObject)) {
            // EJB 3
            stub = homeObject;
        } else if (homeObject instanceof EJBLocalHome) {
            // Local EJB
            stub = createEJBLocalObject(homeObject);
        } else {
            // Handle dynamic stub
            if (homeObject instanceof ObjectImpl) {
                ObjectImpl objectImpl = (ObjectImpl)homeObject;
                stub = createEJBObject(objectImpl);
            }/**
                       	* Above checks will be satisfied if Bean is running on servers like Websphere. With this 
                       	* logic, client (SCA composite with EJB ref binding) doesn't need to include home class or 
                       	* client stubs.
                       	* 
                       	* Below check is needed SCA composite with EJB ref binding is accessing openEJB implementation. 
                       	* For e.g if the bean is running on Geronimo. 
                       	*/
            else if ((javax.rmi.PortableRemoteObject.narrow(homeObject, javax.ejb.EJBHome.class)) instanceof javax.ejb.EJBHome) {
                stub = createEJBObjectFromHome(homeObject);
            } else
                throw new ServiceRuntimeException("Invalid stub type: " + homeObject.getClass());
        }
        return stub;
    }

    /**
     * Create a pre-generated EJB stub
     * 
     * @param homeObject
     * @return
     * @throws RemoteException
     */
    protected static Object createEJBLocalObject(Object homeObject) throws RemoteException {

        Object stub = null;
        try {
            // Find the "create()" method
            Method createMethod = homeObject.getClass().getMethod("create", null);
            // Create an EJB object
            stub = createMethod.invoke(homeObject, null);
        } catch (NoSuchMethodException e) {
            // "create()" method not found, it's still a dynamic stub
            stub = null;
        } catch (InvocationTargetException e) {
            throw new RemoteException(e.getTargetException().toString());
        } catch (Exception e) {
            throw new RemoteException(e.toString());
        }
        return stub;
    }

    /**
     * Here homeObject is instance of EJBHome
     * 
     * @param homeObject
     * @return
     * @throws RemoteException
     */
    protected static Object createEJBObjectFromHome(Object homeObject) throws RemoteException {

        Object stub = null;
        try {
            // Find the "create()" method
            Method createMethod = homeObject.getClass().getMethod("create", null);
            // Create an EJB object
            stub = createMethod.invoke(homeObject, null);
        } catch (NoSuchMethodException e) {
            // "create()" method not found, it's still a dynamic stub
            stub = null;
        } catch (InvocationTargetException e) {
            throw new RemoteException(e.getTargetException().toString());
        } catch (Exception e) {
            throw new RemoteException(e.toString());
        }
        return stub;
    }

    /**
     * Create an EJBObject using RMI/IIOP APIs
     * 
     * @param ejbHomeObject
     * @return The EJBObject remote stub
     * @throws CreateException
     * @throws RemoteException
     */
    protected static Object createEJBObject(ObjectImpl ejbHomeObject) throws CreateException, RemoteException {

        try {
            org.omg.CORBA_2_3.portable.InputStream in = null;
            try {
                org.omg.CORBA.portable.OutputStream out = ejbHomeObject._request("create", true);
                in = (org.omg.CORBA_2_3.portable.InputStream)ejbHomeObject._invoke(out);
                // The Remote stub should be available in JDK
                // TODO: [rfeng] Work around an issue in Apache Yoko which doesn't understand the org.omg.stub.*
                return in.read_Object(_Remote_Stub.class);
            } catch (ApplicationException ex) {
                in = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                String id = in.read_string();
                if (id.equals("IDL:javax/ejb/CreateEx:1.0")) {
                    throw (CreateException)in.read_value(CreateException.class);
                }
                throw new UnexpectedException(id);
            } catch (RemarshalException ex) {
                return createEJBObject(ejbHomeObject);
            } finally {
                ejbHomeObject._releaseReply(in);
            }
        } catch (SystemException ex) {
            throw Util.mapSystemException(ex);
        }
    }
}
