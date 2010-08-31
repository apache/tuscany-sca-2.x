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
import static sample.Xutil.xml;
import static sample.Xutil.xreduce;

import org.w3c.dom.Element;

import sample.api.WSDL;
import sample.api.WSDLReference;

/**
 * Sample component implementation that uses WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
@WSDL("http://sample#Hello")
public class WelloTest {

    @WSDL("http://sample#Upper")
    WSDLReference upper;

    public Element call(String op, Element e) {
        out.println("WelloTest." + op + "(" + xml(e) + ")");
        final String name = xreduce(print, "", xfilter(select("name"), elems(e)));

        final Element ureq = xdom("http://sample", "upper", elem("s", text("Hello " + name)));
        final Element ures = upper.call("upper", ureq);
        
        final String s = xreduce(print, "", xfilter(select("result"), elems(ures))); 
        return xdom("http://sample", "helloResponse", elem("result", text(s)));
    }
}
