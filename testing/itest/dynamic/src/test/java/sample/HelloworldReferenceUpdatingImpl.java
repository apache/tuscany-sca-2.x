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

import org.apache.tuscany.sca.runtime.TuscanyComponentContext;
import org.apache.tuscany.sca.runtime.TuscanyServiceReference;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Reference;


public class HelloworldReferenceUpdatingImpl implements Helloworld {

    @Reference
    Helloworld ref;
    
    @Context
    public TuscanyComponentContext tuscanyComponentContext;
    
    public String sayHello(String name) {
        
        TuscanyServiceReference<Helloworld> tsr = tuscanyComponentContext.getServiceReference(Helloworld.class, "ref");
        tsr.setBindingURI("http://localhost:8080/testComponent/Helloworld");
        
        return "HelloworldReferenceImpl " + ref.sayHello(name);
    }
    

}
