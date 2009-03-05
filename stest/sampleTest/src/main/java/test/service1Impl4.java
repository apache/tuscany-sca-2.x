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
package test;

import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

/**
 * Enhanced Java component implementation for business interface Service1,
 * where the implementation also has a single reference using the Service1
 * interface with multiplicity 0..n all of which and which get called when 
 * operation1 is invoked
 * @author MikeEdwards
 *
 */
@Service(Service1.class)
public class service1Impl4 implements Service1 {

    @Property
    public String serviceName = "service1";
    // Required = false + an array -> multiplicity 0..n
    @Reference(required = false)
    public Service1[] reference1 = null;

    public String operation1(String input) {
        String result = serviceName + " operation1 invoked";
        // Call each of the references in the array, concatenating the results
        for (int i = 0; i < reference1.length; i++) {
            result = result.concat(" ");
            result = result.concat(reference1[i].operation1(input));
        } // end for
        return result;
    }

}
