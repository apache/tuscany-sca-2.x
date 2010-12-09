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
import static sample.Xutil.text;
import static sample.Xutil.xdom;

import org.w3c.dom.Element;

import sample.api.Java;
import sample.api.WSDL;
import sample.api.WSDLReference;

/**
 * Sample component implementation that uses Java interfaces.
 * 
 * @version $Rev$ $Date$
 */
@Java(Upper.class)
public class UpperSampleAsyncReferenceImpl {
    
    @WSDL("http://sample/upper-async#Upper")
    WSDLReference upper;
    
    Element response;
    
    public String upper(String s) {
        out.println("UpperSampleAsyncReferenceImpl.upper(" + s + ")");
        
        // TODO - I'm passing in the non-wrapped version of the parameter
        //        here which doesn't seem right. Need to test that databinding
        //        wraps it correctly
        final Element ureq = xdom("http://sample/upper-async", "s", text(s));
        upper.callAsync("upper", ureq);
        
        try {
            Thread.sleep(500);
        } catch (Exception ex) {
            // do nothing
        }
        
        return response.getTextContent();
    }
    
    /**
     *  In this implementation the convention is that the 
     *  async callback arrives at an operation named
     *  operationName + Callback
     */
    public void upperCallback(Element response) {
        out.println("UpperSampleAsyncReferenceImpl.upperCallback(" + response.getTextContent() + ")");
        this.response = response;
    }
}
