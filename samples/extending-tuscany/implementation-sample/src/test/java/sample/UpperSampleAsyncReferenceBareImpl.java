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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;

import sample.Xutil.NodeBuilder;
import sample.api.Java;
import sample.api.WSDL;
import sample.api.WSDLReference;

/**
 * Sample component implementation that uses Java interfaces.
 * 
 * @version $Rev$ $Date$
 */
@Java(Upper.class)
public class UpperSampleAsyncReferenceBareImpl {
    
    @WSDL("http://sample/upper-async-bare#UpperBare")
    WSDLReference upper;
    
    Element response;
    CountDownLatch latch = new CountDownLatch( 1 );
    
    public String upper(String s) {
        out.println("UpperSampleAsyncReferenceImpl.upper(" + s + ")");
        
        // I'm passing in the non-wrapped version of the parameter
        // here to test what happens with different WSDL styles 
        // at the implementation and binding
        final Element arg0 = xdom("http://sample/upper-async", "arg0", text("arg0_" + s));
        final Element arg1 = xdom("http://sample/upper-async", "arg1", text("arg1_" + s));

        // TODO - intended to do an async call here but using the the sync version
        //        while I look at databinding. 
        Element response = upper.callBare("upper", arg0, arg1);
        
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
        latch.countDown();
    }
}
