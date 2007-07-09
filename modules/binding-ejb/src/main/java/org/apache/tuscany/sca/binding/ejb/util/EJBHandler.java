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

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJBObject;
import javax.rmi.CORBA.Util;

import org.apache.tuscany.sca.binding.ejb.java2idl.ExceptionType;
import org.apache.tuscany.sca.binding.ejb.java2idl.Java2IDLUtil;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.osoa.sca.ServiceRuntimeException;

/**
 * EJBMessageHandler
 */
public class EJBHandler {
    private static final Map<String, Class> PRIMITIVE_TYPES = new HashMap<String, Class>();
    static {
        PRIMITIVE_TYPES.put("boolean", boolean.class);
        PRIMITIVE_TYPES.put("byte", byte.class);
        PRIMITIVE_TYPES.put("char", char.class);
        PRIMITIVE_TYPES.put("short", short.class);
        PRIMITIVE_TYPES.put("int", int.class);
        PRIMITIVE_TYPES.put("long", long.class);
        PRIMITIVE_TYPES.put("float", float.class);
        PRIMITIVE_TYPES.put("double", double.class);
        PRIMITIVE_TYPES.put("void", void.class);
    }

    private Object ejbStub;

    private InterfaceInfo interfaceInfo;
    private Class ejbInterface;

    public EJBHandler(NamingEndpoint namingEndpoint, Class ejbInterface) {
        this(namingEndpoint, InterfaceInfo.getInstance(ejbInterface));
        this.ejbInterface = ejbInterface;
    }

    // locates the stub
    public EJBHandler(NamingEndpoint namingEndpoint, InterfaceInfo ejbInterface) {
        try {
            this.ejbStub = EJBStubHelper.lookup(namingEndpoint);
            this.interfaceInfo = ejbInterface;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private static Class loadClass(final String name) {
        Class type = (Class)PRIMITIVE_TYPES.get(name);
        if (type != null) {
            return type;
        }
        return AccessController.doPrivileged(new PrivilegedAction<Class>() {
            public Class run() {
                try {
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    return Class.forName(name, true, classLoader);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        });
    }

    // invokes EJB method
    public Object invoke(String methodName, Object[] args) {
        Object response = null;
        try {
            if (ejbStub instanceof ObjectImpl) {
                ObjectImpl objectImpl = (ObjectImpl)ejbStub;
                // TODO: If the Java 2 security is turned on, then
                // the ORB will try to create proxy
                // from the interfaces defined on the stub
                if (System.getSecurityManager() == null && objectImpl._is_local()) {
                    /*
                     * CORBA.Stub is what the object from JNDI will be for a
                     * remote EJB in the same JVM as the client, but with no
                     * stub classes available on the client
                     */
                    response = invokeLocalCORBACall(objectImpl, methodName, args);
                } else {
                    /*
                     * _EJBObject_Stub is what the object from JNDI will be for
                     * a remote EJB with no stub classes available on the client
                     */
                    response = invokeRemoteCORBACall(objectImpl, methodName, args);
                }
            } else {
                /*
                 * A generated ejb stub or it must be an EJB in the same ear as
                 * the client or an AppServer with a single classloader, so
                 * reflection can be used directly on the JNDI
                 */
                JavaReflectionAdapter reflectionAdapter =
                    JavaReflectionAdapter.createJavaReflectionAdapter(ejbStub.getClass());
                try {
                    Method method = reflectionAdapter.getMethod(methodName);
                    response = method.invoke(ejbStub, args);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    // FIXME need to throw really a business exception.
                    // ServiceBusinessException?
                    // Tuscany core doesn't have ServiceBusinessException
                    throw new ServiceRuntimeException(t);
                }
            }

            return response;
        } catch (Exception e) {
            // FIXME this be business exception? Tuscany core doesn't have
            // ServiceBusinessException
            throw new ServiceRuntimeException(e);

        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Get the IDL operation name for a java method
     * 
     * @param methodName java method name
     * @return The IDL operation name
     */
    private String getOperation(String methodName) {
        if (interfaceInfo == null) {
            return methodName;
        }
        MethodInfo methodInfo = interfaceInfo.getMethod(methodName);
        if (methodInfo != null) {
            return methodInfo.getIDLName();
        } else {
            return null;
        }
    }

    /*
     * Derive the EJB interface name from the Stub When loading a stub class
     * corresponding to an interface or class <packagename>.<typename>, the
     * class <packagename>._<typename>_Stub shall be used if it exists;
     * otherwise, the class org.omg.stub.<packagename>._<typename>_Stub shall
     * be used.
     */
    private static String getInterface(String stubName) {
        int index = stubName.lastIndexOf('.');
        String packageName = null;
        String typeName = stubName;
        if (index != -1) {
            packageName = stubName.substring(0, index);
            if (packageName.startsWith("org.omg.stub.")) {
                packageName = packageName.substring("org.omg.stub.".length());
            }
            typeName = stubName.substring(index + 1);
        }
        if (typeName.startsWith("_") && typeName.endsWith("_Stub")) {
            typeName = typeName.substring(1, typeName.length() - "_Stub".length());
        }
        if (packageName != null)
            return packageName + "." + typeName;
        else
            return typeName;
    }

    /**
     * Invoke a method on the local CORBA object
     * 
     * @param stub
     * @param methodName
     * @param args
     * @return
     * @throws RemoteException
     * @throws ServiceBusinessException
     */
    protected Object invokeLocalCORBACall(final ObjectImpl stub, String methodName, Object[] args)
        throws RemoteException {

        final String operation = getOperation(methodName);

        Class type = loadClass(getInterface(stub.getClass().getName()));
        if (type == null)
            type = (ejbInterface != null) ? ejbInterface : EJBObject.class;

        ServantObject so = stub._servant_preinvoke(operation, type);
        if (so == null) {
            // The Servant is not local any more
            return invokeRemoteCORBACall(stub, methodName, args);
        }
        Object[] newArgs = null;
        ORB orb = stub._orb();
        try {
            if (args != null)
                newArgs = Util.copyObjects(args, orb);
            JavaReflectionAdapter reflectionAdapter =
                JavaReflectionAdapter.createJavaReflectionAdapter(so.servant.getClass());
            Method method = reflectionAdapter.getMethod(methodName);
            Object obj = reflectionAdapter.invoke(method, so.servant, newArgs);
            Object result = Util.copyObject(obj, orb);
            return result;

        } catch (InvocationTargetException e) {
            Throwable exCopy = (Throwable)Util.copyObject(e.getTargetException(), orb);
            MethodInfo methodInfo = interfaceInfo.getMethod(methodName);
            String[] exceptionTypes = methodInfo.getExceptionTypes();
            for (int i = 0; i < exceptionTypes.length; i++) {
                Class exceptionType = loadClass(exceptionTypes[i]);
                if (exceptionType.isAssignableFrom(exCopy.getClass()))
                    throw new ServiceRuntimeException(exCopy); // FIXME should
                // be business
                // exception?
            }
            throw Util.wrapException(exCopy);
        } catch (Throwable e) {
            // Other exceptions thrown from "invoke"
            throw new ServiceRuntimeException(e);
        } finally {
            stub._servant_postinvoke(so);
        }
    }

    /**
     * Invoke a method on a remote CORBA object
     * 
     * @param stub The remote stub
     * @param methodName The name of the method
     * @param args Argument list
     * @return
     * @throws RemoteException
     * @throws ServiceBusinessException
     */
    protected Object invokeRemoteCORBACall(ObjectImpl stub, String methodName, Object[] args) throws RemoteException {

        try {
            String operation = getOperation(methodName);

            MethodInfo methodInfo = interfaceInfo.getMethod(methodName);
            if (methodInfo == null) {
                throw new ServiceRuntimeException("Invalid Method " + methodName);
            }
            String[] types = methodInfo.getParameterTypes();
            if (args != null) {
                if (types.length != args.length)
                    throw new ServiceRuntimeException(
                                                      "The argument list doesn't match the method signature of " + methodName);
            }

            Class[] parameterTypes = new Class[types.length];
            for (int i = 0; i < types.length; i++) {
                parameterTypes[i] = loadClass(types[i]);
            }
            Class returnType = loadClass(methodInfo.getReturnType());

            InputStream in = null;
            try {
                OutputStream out = (OutputStream)stub._request(operation, true);

                for (int i = 0; i < types.length; i++) {
                    // Object arg = (args.length < i) ? null : args[i];
                    writeValue(out, args[i], parameterTypes[i]);
                }
                if (returnType == void.class) {
                    // void return
                    stub._invoke(out);
                    return null;
                } else {
                    // read the return value
                    in = (InputStream)stub._invoke(out);
                    Object response = readValue(in, returnType);
                    return response;
                }

            } catch (ApplicationException ex) {
                in = (InputStream)ex.getInputStream();
                String id = in.read_string();
                // Check if the id matches to any declared exceptions for the
                // method
                String[] exceptionTypes = methodInfo.getExceptionTypes();
                for (int i = 0; i < exceptionTypes.length; i++) {
                    Class exceptionType = loadClass(exceptionTypes[i]);
                    String exceptionId = ExceptionType.getExceptionType(exceptionType).getExceptionRepositoryId();
                    if (id.equals(exceptionId)) {
                        Throwable t = (Throwable)in.read_value(exceptionType);
                        throw new ServiceRuntimeException(t); // FIXME should
                        // be
                        // ServcieBusinessException?
                        // no support by
                        // Tuscany core
                        // for
                        // ServcieBusinessException.
                    }
                }
                throw new UnexpectedException(id);
            } catch (RemarshalException ex) {
                return invokeRemoteCORBACall(stub, methodName, args);
            } finally {
                stub._releaseReply(in);
            }
        } catch (SystemException ex) {
            throw Util.mapSystemException(ex);
        }
    }

    /**
     * @param out
     * @param value
     * @param type
     */
    protected void writeValue(OutputStream out, Object value, Class type) {
        if (type == null)
            out.write_value((Serializable)value);
        else if (type == Object.class || type == Serializable.class || type == Externalizable.class) {
            // Any
            Util.writeAny(out, value);
        } else if (type == Integer.TYPE) {
            // java int maps to CORBA long
            out.write_long(((Integer)value).intValue());
        } else if (type == Short.TYPE) {
            out.write_short(((Short)value).shortValue());
        } else if (type == Boolean.TYPE) {
            out.write_boolean(((Boolean)value).booleanValue());
        } else if (type == Byte.TYPE) {
            out.write_octet(((Byte)value).byteValue());
        } else if (type == Long.TYPE) {
            out.write_longlong(((Long)value).longValue());
        } else if (type == Double.TYPE) {
            out.write_double(((Double)value).doubleValue());
        } else if (type == Float.TYPE) {
            out.write_float(((Float)value).floatValue());
        } else if (type == Character.TYPE) {
            out.write_wchar(((Character)value).charValue());
        } else if (type.isArray()) {
            out.write_value((Serializable)value, type);
        } else if (Java2IDLUtil.isRemoteInterface(type)) {
            // Remote interface
            Util.writeRemoteObject(out, value);
        } else if (Java2IDLUtil.isAbstractInterface(type)) {
            // Non-remote Interface
            Util.writeAbstractObject(out, value);
        } else {
            out.write_value((Serializable)value, type);
        }
    }

    /**
     * @param in
     * @param type
     * @return
     */
    protected Object readValue(InputStream in, Class type) {
        Object value = null;
        if (type == null) {
            value = in.read_value();
        } else if (type == Object.class || type == Serializable.class || type == Externalizable.class) {
            value = Util.readAny(in);
        } else if (type == Integer.TYPE) {
            value = Integer.valueOf(in.read_long());
        } else if (type == Short.TYPE) {
            value = new Short(in.read_short());
        } else if (type == Boolean.TYPE) {
            value = Boolean.valueOf(in.read_boolean());
        } else if (type == Byte.TYPE) {
            value = new Byte(in.read_octet());
        } else if (type == Long.TYPE) {
            value = new Long(in.read_longlong());
        } else if (type == Float.TYPE) {
            value = new Float(in.read_float());
        } else if (type == Double.TYPE) {
            value = new Double(in.read_double());
        } else if (type == Character.TYPE) {
            value = new Character(in.read_wchar());
        } else if (type.isArray()) {
            // []
            value = in.read_value(type);
        } else if (Java2IDLUtil.isRemoteInterface(type)) {
            // java.rmi.Remote
            value = in.read_Object(type);
        } else if (Java2IDLUtil.isAbstractInterface(type)) {
            // Non-remote Interface
            value = in.read_abstract_interface(type);
        } else {
            // java.io.Serializable
            value = in.read_value(type);
        }
        return value;
    }
}
