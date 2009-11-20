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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import javax.jws.WebService;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * Introspect the java class/interface to see if it has @WebService annotation
 * 
 * @version $Rev$ $Date$
 */
public class WebServiceInterfaceProcessor implements JavaInterfaceVisitor {

    public WebServiceInterfaceProcessor() {
        super();
    }

    public void visitInterface(JavaInterface contract) throws InvalidInterfaceException {

        final Class<?> clazz = contract.getJavaClass();
        WebService webService = clazz.getAnnotation(WebService.class);
        if (webService != null) {
            // Mark SEI as Remotable
            contract.setRemotable(true);
        }
    }

}
