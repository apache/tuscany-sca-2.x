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

package sample;

import static java.lang.System.out;
import static sample.Xutil.dom;
import static sample.Xutil.elem;
import static sample.Xutil.text;
import static sample.Xutil.xpath;

import org.w3c.dom.Element;

import sample.api.Java;
import sample.api.WSDL;
import sample.api.WSDLReference;

/**
 * Sample component implementation.
 * 
 * @version $Rev$ $Date$
 */
@Java(Client.class)
public class ClientTest {

    @Java(Hello.class)
    Hello jello;

    @WSDL("http://sample#Hello")
    WSDLReference wello;

    public String jello(String s) {
        out.println("ClientTest.jello(" + s + ")");
        return jello.hello(s);
    }

    public String wello(String s) {
        out.println("ClientTest.wello(" + s + ")");
        final Element hreq = dom("http://sample", "hello", elem("name", text(s)));
        final Element hres = wello.call("hello", hreq);
        return xpath("//*", hres);
    }
}
