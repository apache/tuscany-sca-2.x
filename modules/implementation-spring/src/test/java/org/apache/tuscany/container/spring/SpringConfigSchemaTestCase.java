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
package org.apache.tuscany.container.spring;

import junit.framework.TestCase;

/**
 * Tests the SCA extensible schema elements for Spring's XML configuration files
 *
 * @version $$Rev$$ $$Date$$
 */

public class SpringConfigSchemaTestCase extends TestCase {

//    private ConfigurableApplicationContext applicationContext;
//
//    public void setUp() {
//        applicationContext =
//            new ScaApplicationContext(null,
//                new ClassPathResource("org/apache/tuscany/container/spring/SpringConfigSchemaTest.xml"));
//    }
//
    public void testSCAService() {
//        ScaServiceExporter service = (ScaServiceExporter) applicationContext.getBean("fooService");
//        // FIXME andyp -- this is not really right.
////        TestBean service = (TestBean) applicationContext.getBean("fooService");
////        assertEquals("call me", service.echo("call me"));
    }
//
//    public void testSCAReference() {
//        ScaServiceProxyFactoryBean pf = (ScaServiceProxyFactoryBean) applicationContext.getBean("&fooReference");
//        assertEquals("fooReference", pf.getReferenceName());
//        TestReference ref = (TestReference) applicationContext.getBean("fooReference");
////      assertNotNull(ref);
//    }
}
