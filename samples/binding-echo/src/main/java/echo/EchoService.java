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

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.WorkContextTunnel;
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
public class EchoService {
    private Interceptor interceptor;

    public EchoService(URI name, Interceptor interceptor) throws CoreRuntimeException {
        super();
        this.interceptor = interceptor;
    }

    public QName getBindingType() {
        return EchoConstants.BINDING_ECHO;
    }

    String sendReceive(String input) throws InvocationTargetException {

        WorkContext workContext = WorkContextTunnel.getThreadWorkContext();

        Message msg = new MessageImpl();
        msg.setBody(new Object[] {input});
        msg.setWorkContext(workContext);
        Message resp;

        // dispatch and get the response
        resp = interceptor.invoke(msg);
        Object body = resp.getBody();
        if (resp.isFault()) {
            throw new InvocationTargetException((Throwable)body);
        }
        return (String)body;
    }

}
