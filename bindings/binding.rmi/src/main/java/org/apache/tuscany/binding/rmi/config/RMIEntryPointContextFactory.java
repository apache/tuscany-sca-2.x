/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.rmi.config;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import net.sf.cglib.proxy.Enhancer;

import org.apache.tuscany.binding.rmi.assembly.RMIBinding;
import org.apache.tuscany.binding.rmi.entrypoint.RMIEntryPointClassLoader;
import org.apache.tuscany.binding.rmi.entrypoint.RemoteMethodHandler;
import org.apache.tuscany.binding.rmi.util.RemoteInterfaceGenerator;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * Creates instances of {@link org.apache.tuscany.core.context.EntryPointContext} configured with the appropriate wire chains and bindings. This
 * implementation serves as a marker for
 * 
 * @version $Rev$ $Date$
 */
public class RMIEntryPointContextFactory extends EntryPointContextFactory 
{
    public static final String URI_PREFIX = "//localhost";
    public static final String SLASH = "/";
    public static final String COLON = ":";
    private RMIBinding rmiBinding;

    public RMIEntryPointContextFactory(String name, MessageFactory messageFactory, RMIBinding rmiBinding) 
    {
        super(name, messageFactory);
        this.rmiBinding = rmiBinding;
    }

    public EntryPointContext createContext() throws ContextCreationException {
        EntryPointContext epc = super.createContext();
        initRMIServer(epc);
        return epc;
    }

    private Remote createRmiService(EntryPointContext entryPointContext, RMIBinding rmiBinding) 
    {
        Class serviceInterface = entryPointContext.getServiceInterface();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UnicastRemoteObject.class);
        //Class remoteIfc = RemoteInterfaceGenerator.generateRemoteInterface(serviceInterface, cl);
        enhancer.setInterfaces(new Class[]{serviceInterface});
        enhancer.setCallback(new RemoteMethodHandler(entryPointContext));
        
        return (Remote)enhancer.create();
    }
    
    private void bindRmiService(EntryPointContext epc, Remote rmiService) throws Exception
    {
        StringBuffer uri = new StringBuffer(URI_PREFIX);
        if ( rmiBinding.getRMIPort() != null && rmiBinding.getRMIPort().length() > 0 )
        {
            uri.append(COLON);
            uri.append(rmiBinding.getRMIPort());
        }
        uri.append(SLASH);
        uri.append(epc.getName());
        
        startRMIRegistry();
        
        Naming.rebind(uri.toString(), rmiService);
        System.out.println("RMI srevice started - " + uri.toString());
        
    }

    private void initRMIServer(EntryPointContext epc) 
    {
        Remote rmiService;
        try 
        { 
            rmiService = createRmiService(epc, rmiBinding);
            bindRmiService(epc, rmiService);
        } 
        catch (Exception e) 
        {
            throw new BuilderConfigException(e);
        }
    }

    private void startRMIRegistry() throws Exception
    {
        //to start rmi registry programmatically
    }
}
