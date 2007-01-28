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
package helloworldOM;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import static java.lang.System.out;


/**
 * This client program shows how to create an SCA runtime, start it,
 * locate the HelloWorld service and invoke it.
 */



public class HelloWorldClient {
   
    private static final String TARGET_NAMESPACE = "http://helloworldOM";

    public  final static void main(String[] args) throws Exception {
        
        // Invoke the HelloWorld service
        CompositeContext compositeContext = CurrentCompositeContext.getContext();
        HelloWorldService helloWorldService= compositeContext.locateService(HelloWorldService.class, "HelloWorldServiceComponent");
        OMFactory fac= OMAbstractFactory.getOMFactory();
        
        //create operation
        OMElement opE = fac.createOMElement("getGreetings", TARGET_NAMESPACE, "helloworld");
        //create parm
        OMElement parmE = fac.createOMElement("name", TARGET_NAMESPACE, "helloworld");
        //and value.
        opE.addChild(parmE);
        StringBuilder sb= new StringBuilder(1000);
        for(String s : args){
            sb.append(s);
        }
        parmE.addChild(fac.createOMText(sb.toString()));
        
        OMElement value = helloWorldService.getGreetings(parmE);
        printTRee(value);
        
        out.println(value);
        out.flush();

    }

    private static void printTRee(OMElement value) {

      out.println(value);
      for(OMNode n = value.getFirstOMChild(); n != null; n= n.getNextOMSibling()){
          if(n instanceof OMElement) printTRee((OMElement) n);
          else out.println(n);
      }
      out.flush();
        
    }
}
