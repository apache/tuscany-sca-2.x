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

package helloworld;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import util.OSGiTestUtil;


public class PassByRefTestCase extends TestCase {

    private SCADomain scaDomain;
    private Greetings greetingsJava;
    private Greetings greetingsOSGi;
    private String[] origNames = {"world"};
    private String[] names;

    protected void setUp() throws Exception {
        OSGiTestUtil.setUpOSGiTestRutime();
        
        scaDomain = SCADomain.newInstance("sca/passbyref-test.composite");
        greetingsJava = scaDomain.getService(Greetings.class, "JavaGreetingsComponent");
        greetingsOSGi = scaDomain.getService(Greetings.class, "OSGiGreetingsComponent");
        
        names = new String[origNames.length];
        System.arraycopy(origNames, 0, names, 0, names.length);
    }
    
    protected void tearDown() throws Exception {
        scaDomain.close();
        OSGiTestUtil.shutdownOSGiRuntime();
    }
    
    public void test() throws Exception {
        
        javaOSGiPassByValue();
        osgiJavaPassByValue();
        javaOSGiPassByRef();
        osgiJavaPassByRef();
        
    }
    

    public void javaOSGiPassByValue() throws Exception {
        
        String[] greetings = greetingsJava.getGreetingsFromJava(names);
        for (int i = 0; i < origNames.length; i++) {
            assertEquals(origNames[i], names[i]);
        }
        for (int i = 0; i < origNames.length; i++) {

            System.out.println(greetings[i]);
            
            assertEquals(greetings[i], 
                    "Hello " + origNames[i] + "(From Java)(From OSGi)");
        }
        
    }
    
    public void osgiJavaPassByValue() throws Exception {
        String[] names = {
                "world"
        };
        String[] greetings = greetingsOSGi.getGreetingsFromOSGi(names);
        
        for (int i = 0; i < origNames.length; i++) {
            assertEquals(origNames[i], names[i]);
        }
        for (int i = 0; i < origNames.length; i++) {

            System.out.println(greetings[i]);
            
            assertEquals(greetings[i], 
                    "Hello " + origNames[i] + "(From OSGi)(From Java)");
        }
        


    }
    
    public void javaOSGiPassByRef() throws Exception {
        String[] names = {
                "world"
        };
        String[] greetings = greetingsJava.getModifiedGreetingsFromJava(names);
        for (int i = 0; i < origNames.length; i++) {
            assertEquals("Hello " + origNames[i] + "(From Java)(From OSGi)", names[i]);
        }
        for (int i = 0; i < origNames.length; i++) {

            System.out.println(greetings[i]);
            
            assertEquals(greetings[i], 
                    "Hello " + origNames[i] + "(From Java)(From OSGi)");
        }
    }
    
    public void osgiJavaPassByRef() throws Exception {
        String[] names = {
                "world"
        };
        String[] greetings = greetingsOSGi.getModifiedGreetingsFromOSGi(names);
        for (int i = 0; i < origNames.length; i++) {
            assertEquals("Hello " + origNames[i] + "(From OSGi)(From Java)", names[i]);
        }
        for (int i = 0; i < origNames.length; i++) {

            System.out.println(greetings[i]);
            
            assertEquals(greetings[i], 
                    "Hello " + origNames[i] + "(From OSGi)(From Java)");
        }
    }

    
}
