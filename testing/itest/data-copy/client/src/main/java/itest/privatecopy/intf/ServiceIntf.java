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

package itest.privatecopy.intf;

import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.oasisopen.sca.annotation.Remotable;
import org.w3c.dom.Node;

import itest.privatecopy.types.Name;
 
@Remotable
public interface ServiceIntf {    
    Name greet(Name name);
    boolean areNamesTheSameObjects(Name name1, Name name2);
    
    // Use JAXB on input parm on the client side
    String greetJSON(Name name);

//    @RequestWrapper(localName = "greetDOM", targetNamespace = "http://intf/internal/itest/", className = "org.w3c.dom.Node")
//    @ResponseWrapper(localName = "greetDOMResponse", targetNamespace = "http://intf/internal/itest/", className = "org.w3c.dom.Node")
//    @DataBinding("org.w3c.dom.Node")  

    // Use JAXB on input parm on the client side
    String greetDOM(Name name);    
    
    Node returnDOM(Node name);
}
