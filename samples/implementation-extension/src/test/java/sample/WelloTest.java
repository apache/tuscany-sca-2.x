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

import sample.api.WSDL;
import sample.api.WSDLReference;

/**
 * Sample component implementation.
 * 
 * @version $Rev$ $Date$
 */
@WSDL("http://sample#Hello")
public class WelloTest {

    @WSDL("http://sample#Upper")
    WSDLReference upper;

    public Element call(String op, Element e) {
        out.println("WelloTest." + op + "(" + Xutil.xml(e) + ")");
        final Element ureq = dom("http://sample", "upper", elem("s", text("Hello " + xpath("//name", e))));
        final Element ures = upper.call("upper", ureq);
        return dom("http://sample", "helloResponse", elem("result", text(xpath("//*", ures))));
    }
}
