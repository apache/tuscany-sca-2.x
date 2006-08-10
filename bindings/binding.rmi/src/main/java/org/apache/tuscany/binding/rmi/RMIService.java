/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi; 

import java.net.MalformedURLException; 
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import net.sf.cglib.proxy.Enhancer;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.wire.WireService;
 
/**
 * @version $Rev$ $Date$
 */
public class RMIService<T extends Remote> extends ServiceExtension<T> 
{
    public static final String URI_PREFIX = "//localhost";
    public static final String SLASH = "/";
    public static final String COLON = ":"; 
    private final String uri;
    private Class service = null;
    int rmiPort = 1099;

    public RMIService(String name,
                      CompositeComponent parent,
                      WireService wireService,
                      String uri,
                      Class<T> service) {
        super(name, service, parent, wireService);
        this.uri = uri;
        this.service = service;
    }

    public void start() {
        super.start();
        Remote rmiProxy = createProxy();//createRmiService();

        try 
        {
            startRMIRegistry();
            bindRmiService(uri, rmiProxy);
            
        } catch (AlreadyBoundException e) {
            throw new NoRemoteServiceException(e);
        } catch (RemoteException e) {
            throw new NoRemoteServiceException(e);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    public void stop() {
        try {
            Naming.unbind(uri);
        } catch (NotBoundException e) {
            // ignore
        } catch (RemoteException e) {
            throw new NoRemoteServiceException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        super.stop();
    }
    
    private Remote createRmiService() 
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        //Class remoteIfc = RemoteInterfaceGenerator.generateRemoteInterface(serviceInterface, cl);
        enhancer.setInterfaces(new Class[]{service});
        enhancer.setCallback(new RemoteMethodHandler(getHandler()));
        
        return (Remote)enhancer.create();
    }
    
    private void bindRmiService(String uri, Remote rmiService) throws MalformedURLException, 
                                                                        RemoteException,
                                                                        AlreadyBoundException
    {
        StringBuffer serverUri = new StringBuffer(URI_PREFIX);
        
        serverUri.append(COLON);
        serverUri.append(rmiPort);
        serverUri.append(SLASH);
        serverUri.append(uri);
        
        Naming.bind(uri.toString(), rmiService);
        System.out.println("RMI srevice started - " + uri.toString());
        
    }


    //how will you stop the registry... no APIs for that... 
    private void startRMIRegistry() throws RemoteException
    {
        try
        {
            Registry registry = LocateRegistry.createRegistry(rmiPort);
        }
        catch ( Exception e )
        {
            LocateRegistry.getRegistry(rmiPort);
        }
    }
    
    protected T createProxy() 
    {
        //InvocationHandler handler = new RMIInvocationHandler(getHandler());
        return interfaze.cast(createRmiService());
    }
    
    

    /*protected Remote createProxy() 
    {
=======
    protected T createProxy() {
>>>>>>> .r429120
        InvocationHandler handler = new RMIInvocationHandler(getHandler());
        return interfaze.cast(Proxy.newProxyInstance(interfaze.getClassLoader(), new Class[]{interfaze}, handler));
    }

    private static class RMIInvocationHandler implements InvocationHandler {
        private final WireInvocationHandler wireHandler;

        public RMIInvocationHandler(WireInvocationHandler wireHandler) {
            this.wireHandler = wireHandler;
        }

        public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
            return wireHandler.invoke(method, objects);
        }
    }*/

}
