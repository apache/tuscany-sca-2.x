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

import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Node;

/**
 * @version $Rev$ $Date$
 */
@AllowsPassByReference
public class ComponentBImpl implements Interface2 {

    private Echo echoReference;

    @Constructor
    public ComponentBImpl(@Reference(name = "echoReference", required = true)
    Echo echoReference) {
        this.echoReference = echoReference;
    }

    public Node call(Node msg) {
        String request = msg + " [" + msg.getClass().getName() + "]";
        System.out.println("ComponentB --> Received message: " + request);
        Node ret = (Node) echoReference.echo(msg);
        String response = ret + " [" + ret.getClass().getName() + "]";
        System.out.println("ComponentB --> Returned message: " + response);
        return ret;
    }
    
    public XMLStreamReader call1(XMLStreamReader msg) {
        String request = msg + " [" + msg.getClass().getName() + "]";
        System.out.println("ComponentB --> Received message: " + request);
//        XMLStreamReader ret = (XMLStreamReader) echoReference.echo(msg);
        String response = request;
        System.out.println("ComponentB --> Returned message: " + response);
        return msg;
    }    
}
