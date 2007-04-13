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
package echo;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.tuscany.core.util.PojoWorkContextTunnel;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class EchoService extends ServiceBindingExtension {
    
    public EchoService(URI name) throws CoreRuntimeException {
        super(name);
        
        // Register with the hosting server
        EchoServer.getServer().register(this, name);
    }

    public QName getBindingType() {
        return EchoConstants.BINDING_ECHO;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) throws TargetInvokerCreationException {
        //TODO Show support for callbacks in this sample
        throw new UnsupportedOperationException();
    }

    String sendReceive(String input) throws InvocationTargetException {
        
        // Get the invocation chain for the first operation in the service interface
        InvocationChain chain = wire.getInvocationChains().get(0);
        Interceptor headInterceptor = chain.getHeadInterceptor();
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        if (headInterceptor == null) {
            // short-circuit the dispatch and invoke the target directly
            TargetInvoker targetInvoker = chain.getTargetInvoker();
            return (String)targetInvoker.invokeTarget(new Object[]{input}, TargetInvoker.NONE, workContext);
        } else {

            Message msg = new MessageImpl();
            msg.setTargetInvoker(chain.getTargetInvoker());
            msg.setBody(new Object[]{input});
            msg.setWorkContext(workContext);
            Message resp;

            // dispatch and get the response
            resp = headInterceptor.invoke(msg);
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw new InvocationTargetException((Throwable) body);
            }
            return (String)body;
        }
    }
    
}
