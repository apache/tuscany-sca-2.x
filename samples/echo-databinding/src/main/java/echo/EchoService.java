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

import static org.osoa.sca.Constants.SCA_NS;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;

/**
 * @version $Rev$ $Date$
 */
public class EchoService extends ServiceBindingExtension {
    private static final QName BINDING_ECHO = new QName(SCA_NS, "binding.echo");

    public EchoService(String name, CompositeComponent parent) throws CoreRuntimeException {
        super(name, parent);
        // do nothing, but this could register with the host environment
    }


    public QName getBindingType() {
        return BINDING_ECHO;
    }
}
