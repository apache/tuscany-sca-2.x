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
package launcher;

import org.junit.Test;

/**
 * Test sample contributions.
 */
public class LauncherTestCase {
    
    @Test
    public void testBindingJSONRPCCalculator() throws Exception {
        JSELauncherBindingSCACalculator.main(null);
    }    

    @Test
    public void testBindingSCACalculator() throws Exception {
        JSELauncherBindingSCACalculator.main(null);
    }
       
    @Test
    public void testBindingWSCalculator() throws Exception {
        JSELauncherBindingWSCalculator.main(null);
    }     
    
    @Test
    public void testBindingRMICalculator() throws Exception {
    	JSELauncherBindingRMICalculator.main(null);
    } 
    
    @Test
    public void testImplementationJavaCalculator() throws Exception {
    	JSELauncherImplementationJavaCalculator.main(null);
    }   
    
    @Test
    public void testImplementationScriptCalculator() throws Exception {
        JSELauncherImplementationScriptCalculator.main(null);
    }     
}
