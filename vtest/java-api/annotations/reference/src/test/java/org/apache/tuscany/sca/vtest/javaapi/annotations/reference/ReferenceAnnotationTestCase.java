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

package org.apache.tuscany.sca.vtest.javaapi.annotations.reference;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test class tests the "@Reference" annotation described in section 1.8.14
 */
public class ReferenceAnnotationTestCase {

    protected static String compositeName = "ab.composite";
    protected static AService a;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            a = ServiceFinder.getService(AService.class, "AComponent");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

    /**
     * Temporary test unrelated to spec test effort. Remove after resolution of
     */
    @Test
    @Ignore
    // JIRA T-2145
    public void bogusComponentName() throws Exception {
        ServiceFinder.init(compositeName);
        try {
            AService a = ServiceFinder.getService(AService.class, "AReallyBogusComponentName");
            if (a == null)
                fail("Should have thrown an exception rather than return null");
            else
                fail("Should have thrown an exception rather than return a proxy");
        } finally {
            ServiceFinder.cleanup();
        }

    }

    /**
     * Lines 1404, 1405, 1406 <br>
     * The "@Reference" annotation type is used to annotate a Java class field
     * or a setter method that is used to inject a service that resolves the
     * reference. The interface of the service injected is defined by the type
     * of the Java class field or the type of the setter method input argument.
     * <p>
     * This tests the use of the three usages of the "@Reference" annotation<br>
     * B1 is injected via field injection <br>
     * B2 is injected via constructor parameter <br>
     * B3 is injected via setter method
     */
    @Test
    public void atReference1() throws Exception {

        Assert.assertEquals("BService", a.getB1Name());
        Assert.assertEquals("BService", a.getB2Name());
        Assert.assertEquals("BService", a.getB3Name());

    }

    /**
     * Lines 1407, 1408, 1409, 1410 <br>
     * References may also be injected via public setter methods even when the
     * "@Reference" annotation is not present. However, the "@Reference"
     * annotation must be used in order to inject a reference onto a non public
     * field. In the case where there is no "@Reference" annotation, the name of
     * the reference is the same as the name of the field or setter.
     * <p>
     * B4 is injected via field injection. Public, Non-annotated <br>
     * B5 is expected to fail field injection. Non-Public, Non-Annotated <br>
     * B6 is injected via setter injection. Public, Non-Annotated
     */
    @Test
    public void atReference2() throws Exception {
        AService anotherA = ServiceFinder.getService(AService.class, "AUnannotatedComponent");
        
        Assert.assertFalse(anotherA.isB4Null());
        Assert.assertTrue(anotherA.isB5Null());
        Assert.assertFalse(anotherA.isB6Null());
        
        Assert.assertEquals("BService", anotherA.getB4Name());
        try {
            anotherA.getB5Name();
            fail("getB5Name expected to fail with NPE");
        } catch (NullPointerException e) {
        }
        Assert.assertEquals("BService", anotherA.getB6Name());

    }

    /**
     * Lines 1411 <br>
     * Where there is both a setter method and a field for a reference, the
     * setter method is used.
     * <p>
     * B7 has both field and setter annotated. The setter must be called
     */
    @Test
    public void atReference3() throws Exception {
        Assert.assertTrue(a.isB7SetterCalled());
    }

    /**
     * Lines 1413, 1414, 1415 <br>
     * The "@Reference" annotation has the following attributes: <br> • name
     * (optional) – the name of the reference, defaults to the name of the field
     * of the Java class <br>
     * required (optional) – whether injection of service or services is
     * required. Defaults to true.
     * <p>
     * Reference and field have different names<br>
     * B8 is field injected<br>
     * B9 is setter injected
     */
    @Test
    public void atReference4() throws Exception {
        Assert.assertEquals("BService", a.getB8Name());
        Assert.assertEquals("BService", a.getB9Name());
    }

    /**
     * Lines 1457 to 1459<br>
     * If the reference is not an array or collection, then the implied
     * component type has a reference with a multiplicity of either 0..1 or 1..1
     * depending on the value of the
     * 
     * @Reference required attribute – 1..1 applies if required=true.<br>
     *            <p>
     *            B10 is field injected, required=false, and multiplicity="0..1"<br>
     *            B11 is field injected, required=false, and multiplicity="1..1"<br>
     *            B12 is setter injected, required=true, and multiplicity="1..1"
     */
    @Test
    public void atReference5() throws Exception {
        Assert.assertEquals("BService", a.getB10Name());
        Assert.assertEquals("BService", a.getB11Name());
        Assert.assertEquals("BService", a.getB12Name());
    }

    /**
     * Lines 1461 to 1463<br>
     * If the reference is defined as an array or as a java.util.Collection,
     * then the implied component type has a reference with a multiplicity of
     * either 1..n or 0..n, depending on whether the required attribute of the
     * "@Reference" annotation is set to true or false – 1..n applies if
     * required=true.<br>
     * <p>
     * B13 is a java.util.List, field injected, required=false,
     * multiplicity="0..n", and no target<br>
     * B14 is a java.util.List, setter injected, required=true,
     * multiplicity="1..n", and one target<br>
     * B15 is an array, field injected, required=true, multiplicity="1..n", and
     * two targets
     */
    @Test
    public void atReference6() throws Exception {
        Assert.assertEquals(0, a.getB13Size());
        Assert.assertEquals("BService", a.getB14Name(0));
        Assert.assertEquals(1, a.getB14Size());
        Assert.assertEquals("BService", a.getB15Name(0));
        Assert.assertEquals("BService", a.getB15Name(1));
        Assert.assertEquals(2, a.getB15Size());
    }

    /**
     * Lines 1415 <br>
     * required (optional) - whether injection of service or services is
     * required. Defaults to true.
     * <p>
     * b16 and b17 is defined as "@Reference(required=false)" and AComponent
     * does not define reference for them
     */
    @Test
    public void atReference7() throws Exception {
        Assert.assertTrue(a.isB16Null());
        Assert.assertTrue(a.isB17Null());
    }
    
    /**
     * Java Component Implementation Spec
     * Section 1.2.7
     * Line 361 when @Property and @Reference annotations are present
     * then unannotated fields are ignored
     */
    @Test
    public void atReference8() throws Exception {
        Assert.assertTrue(a.isB4Null());
        Assert.assertTrue(a.isB5Null());
        Assert.assertTrue(a.isB6Null());
    }
}
