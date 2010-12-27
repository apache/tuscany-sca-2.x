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

package sampleasync.impl;

import static java.lang.System.out;
import static sample.Xutil.elem;
import static sample.Xutil.text;
import static sample.Xutil.xdom;

import org.w3c.dom.Element;

import sample.api.WSDL;

/**
 * Sample component implementation that uses Java interfaces.
 * 
 * @version $Rev$ $Date$
 */
@WSDL("http://sample/upper#Upper")
public class UpperSampleAsyncServiceImpl {

    public Element call(String op, Element e) {
        String input = e.getTextContent();
        out.println("UpperSampleAsyncServiceImpl.upper(" + input + ")");
        String output = input.toUpperCase();
        return xdom("http://sample/upper", "upperResponse", elem("result", text(output)));
    }
}
