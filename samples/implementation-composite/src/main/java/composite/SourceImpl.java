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
package composite;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;


@Service(Source.class)
@Scope("COMPOSITE")
public class SourceImpl implements Source, SourceCallback {

    private Target targetReference;
    private Target targetReference2;

    @Reference
    public void setTargetReference(Target target) {
        this.targetReference = target;
    }

    @Reference
    public void setTargetReference2(Target target) {
        this.targetReference2 = target;
    }

    public void clientMethod(String arg) {
        System.out.println("Source: " + arg + " -> Source.clientMethod");
        targetReference.someMethod(arg + " -> Source.clientMethod");

        System.out.println("Source: " + arg + " => Source.clientMethod2");
        targetReference2.someMethod(arg + " => Source.clientMethod2");
    }

    public void receiveResult(String result) {
        System.out.println("Work thread " + Thread.currentThread());
        System.out.println("Result: " + result);
    }
}
