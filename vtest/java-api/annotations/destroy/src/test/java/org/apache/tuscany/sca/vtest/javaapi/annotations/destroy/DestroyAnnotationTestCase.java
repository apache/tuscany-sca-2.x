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

package org.apache.tuscany.sca.vtest.javaapi.annotations.destroy;

import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.Test;
import org.osoa.sca.ServiceRuntimeException;

/**
 * This test class tests the "@Destroy" annotation described in section 1.8.8.
 */
public class DestroyAnnotationTestCase {

    /**
     * Lines 1225, 1226, 1227<br>
     * The "@Destroy" annotation type is used to annotate a Java class method
     * that will be called when the scope defined for the local service
     * implemented by the class ends. The method must have a void return value
     * and no arguments. The annotated method must be public.
     * <p>
     * This method tests a proper destroy method. i.e., public, no arguments and
     * with void return type.
     */
    @Test
    public void atDestroyProper() throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCANode node = nodeFactory.createSCANode(new File("src/main/resources/proper/AService.composite").toURL().toString(),
                new SCAContribution("TestContribution", 
                                    new File("src/main/resources/proper").toURL().toString()));
        node.start();
        AService aService = ((SCAClient)node).getService(AService.class, "AComponent");
        Assert.assertEquals("Hello Pandu", aService.getGreetings("Pandu"));
        node.stop();
    }

    /**
     * Lines 1225, 1226, 1227<br>
     * The "@Destroy" annotation type is used to annotate a Java class method
     * that will be called when the scope defined for the local service
     * implemented by the class ends. The method must have a void return value
     * and no arguments. The annotated method must be public.
     * <p>
     * This method tests that an exception is thrown when a protected method is
     * annotated with "@Destroy".
     */
    @Test
    public void atDestroyProtectedMethod() throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        try {
            SCANode node = nodeFactory.createSCANode(new File("src/main/resources/err1/AServiceErr1.composite").toURL().toString(),
                    new SCAContribution("TestContribution", 
                                        new File("src/main/resources/err1").toURL().toString()));
            Assert.fail();
            node.stop();
        } catch(ServiceRuntimeException e) {
            //expected
            Assert.assertNotSame(-1, e.getMessage().indexOf("Destructor must be a public method."));
        }
    }

    /**
     * Lines 1225, 1226, 1227<br>
     * The "@Destroy" annotation type is used to annotate a Java class method
     * that will be called when the scope defined for the local service
     * implemented by the class ends. The method must have a void return value
     * and no arguments. The annotated method must be public.
     * <p>
     * This method tests that an exception is thrown when a private method is
     * annotated with "@Destroy".
     */
    @Test
    public void atDestroyPrivateMethod() throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        try {
            SCANode node = nodeFactory.createSCANode(new File("src/main/resources/err2/AServiceErr2.composite").toURL().toString(),
                    new SCAContribution("TestContribution", 
                                        new File("src/main/resources/err2").toURL().toString()));
            Assert.fail();
            node.stop();
        } catch(ServiceRuntimeException e) {
            //expected
            Assert.assertNotSame(-1, e.getMessage().indexOf("Destructor must be a public method."));
        }
    }

    /**
     * Lines 1225, 1226, 1227<br>
     * The "@Destroy" annotation type is used to annotate a Java class method
     * that will be called when the scope defined for the local service
     * implemented by the class ends. The method must have a void return value
     * and no arguments. The annotated method must be public.
     * <p>
     * This method tests that an exception is thrown when a method with non-void
     * return type is annotated with "@Destroy".
     */
    @Test
    public void atDestroyNonVoidReturnType() throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        try {
            SCANode node = nodeFactory.createSCANode(new File("src/main/resources/err3/AServiceErr3.composite").toURL().toString(),
                    new SCAContribution("TestContribution", 
                                        new File("src/main/resources/err3").toURL().toString()));
            Assert.fail();
            node.stop();
        } catch(ServiceRuntimeException e) {
            //expected
            Assert.assertNotSame(-1, e.getMessage().indexOf("Destructor must return void."));
        }
    }

    /**
     * Lines 1225, 1226, 1227<br>
     * The "@Destroy" annotation type is used to annotate a Java class method
     * that will be called when the scope defined for the local service
     * implemented by the class ends. The method must have a void return value
     * and no arguments. The annotated method must be public.
     * <p>
     * This method tests that an exception is thrown when a method with arguments is
     * annotated with "@Destroy".
     */
    @Test
    public void atDestroyMethodWithArgs() throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        try {
            SCANode node = nodeFactory.createSCANode(new File("src/main/resources/err4/AServiceErr4.composite").toURL().toString(),
                    new SCAContribution("TestContribution", 
                                        new File("src/main/resources/err4").toURL().toString()));
            Assert.fail();
            node.stop();
        } catch(ServiceRuntimeException e) {
            //expected
            Assert.assertNotSame(-1, e.getMessage().indexOf("Destructor must not have argments"));
        }
    }
}
