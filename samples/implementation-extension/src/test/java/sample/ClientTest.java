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
import static sample.Xutil.elem;
import static sample.Xutil.elems;
import static sample.Xutil.print;
import static sample.Xutil.select;
import static sample.Xutil.text;
import static sample.Xutil.xdom;
import static sample.Xutil.xfilter;
import static sample.Xutil.xreduce;

import org.w3c.dom.Element;

import sample.api.Java;
import sample.api.WSDL;
import sample.api.WSDLReference;

/**
 * Sample component implementation that uses a mix of Java and WSDL interfaces.
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
        final Element hreq = xdom("http://sample", "hello", elem("name", text(s)));
        
        final Element hres = wello.call("hello", hreq);
        
        return xreduce(print, "", xfilter(select("result"), elems(hres))); 
    }
}
