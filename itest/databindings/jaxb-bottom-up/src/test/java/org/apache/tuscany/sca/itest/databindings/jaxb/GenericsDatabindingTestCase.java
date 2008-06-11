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

package org.apache.tuscany.sca.itest.databindings.jaxb;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.itest.databindings.jaxb.impl.GenericsTransformer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Databinding tests for generics, parameterized and polymorphic types.
 * 
 * @version $Rev$ $Date$
 */
public class GenericsDatabindingTestCase {
    private static SCADomain domain;
 
    /**
     * Runs before each test method
     */
    @BeforeClass
    public static void setUp() throws Exception {
        try { 
            domain = SCADomain.newInstance("generics-service.composite");
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs after each test method
     */
    @AfterClass
    public static void tearDown() {
        domain.close();
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getTypeExplicit.
     */
    @Test
    public void testSCATypeExplicit() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestTypeExplicit(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getTypeUnbound.
     */
    @Test
    public void testSCATypeUnbound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestTypeUnbound(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getTypeExtends.
     */
    @Test
    public void testSCATypeExtends() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestTypeExtends(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getRecursiveTypeBound.
     */
    @Test
    public void testSCARecursiveTypeBound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestRecursiveTypeBound(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getWildcardUnbound.
     */
    @Test
    public void testSCAWildcardUnbound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestWildcardUnbound(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getWildcardSuper.
     */
    @Test
    public void testSCAWildcardSuper() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestWildcardSuper(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getWildcardExtends.
     */
    @Test
    public void testSCAWildcardExtends() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestWildcardExtends(serviceClient);
    }

    /**
     * Invokes the GenericsService service using SCA binding.
     * Service method invoked is getPolymorphic.
     */
    @Test
    public void testSCAPolymorphic() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientSCAComponent");
        performTestPolymorphic(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getTypeExplicit.
     */
    @Test
    public void testWSTypeExplicit() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestTypeExplicit(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getTypeUnbound.
     */
    @Test
    public void testWSTypeUnbound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestTypeUnbound(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getTypeExtends.
     */
    @Test
    public void testWSTypeExtends() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestTypeExtends(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getRecursiveTypeBound.
     */
    @Test
    public void testWSRecursiveTypeBound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestRecursiveTypeBound(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getWildcardUnbound.
     */
    @Test
    public void testWSWildcardUnbound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestWildcardUnbound(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getWildcardSuper.
     */
    @Test
    public void testWSWildcardSuper() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestWildcardSuper(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getWildcardExtends.
     */
    @Test
    public void testWSWildcardExtends() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestWildcardExtends(serviceClient);
    }

    /**
     * Invokes the GenericsService service using WS binding.
     * Service method invoked is getPolymorphic.
     */
    @Test
    public void testWSPolymorphic() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsServiceClientWSComponent");
        performTestPolymorphic(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getTypeExplicit.
     */
    @Test
    public void testSCALocalTypeExplicit() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestTypeExplicit(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getTypeUnbound.
     */
    @Test
    public void testSCALocalTypeUnbound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestTypeUnbound(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getTypeExtends.
     */
    @Test
    public void testSCALocalTypeExtends() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestTypeExtends(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getRecursiveTypeBound.
     */
    @Test
    public void testSCALocalRecursiveTypeBound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestRecursiveTypeBound(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getWildcardUnbound.
     */
    @Test
    public void testSCALocalWildcardUnbound() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestWildcardUnbound(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getWildcardSuper.
     */
    @Test
    public void testSCALocalWildcardSuper() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestWildcardSuper(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getWildcardExtends.
     */
    @Test
    public void testSCALocalWildcardExtends() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestWildcardExtends(serviceClient);
    }

    /**
     * Invokes the GenericsLocalService service using SCA binding.
     * Service method invoked is getPolymorphic.
     */
    @Test
    public void testSCALocalPolymorphic() throws Exception {
        GenericsServiceClient serviceClient = domain.getService(GenericsServiceClient.class, "GenericsLocalServiceClientSCAComponent");
        performTestPolymorphic(serviceClient);
    }

    private void performTestTypeExplicit(GenericsServiceClient serviceClient) {
        Bean1<String> args[] = new Bean1[2];
        args[0] = new Bean1<String>("Me");
        args[1] = new Bean1<String>();
        for(int i = 0; i < args.length; ++i) {
            Bean1<String> arg = args[i];
            Bean1<String> expected = GenericsTransformer.getTypeExplicit(arg);
            Bean1<String> actual = serviceClient.getTypeExplicitForward(arg);
            Assert.assertEquals(expected, actual);
        }
    }

    private void performTestTypeUnbound(GenericsServiceClient serviceClient) {
        {   // String
            String[] args = { "Me", "You", "Him" };
            Bean1<String> expected = GenericsTransformer.getTypeUnbound(args);
            Bean1<String> actual = serviceClient.getTypeUnboundForward(args);
            // Assert.assertEquals(expected, actual);
        }
        {   // Integer
            Integer[] args = new Integer[3];
            args[0] = -10;
            args[1] = 0;
            args[2] = 10;
            Bean1<Integer> expected = GenericsTransformer.getTypeUnbound(args);
            Bean1<Integer> actual = serviceClient.getTypeUnboundForward(args);
            // Assert.assertEquals(expected, actual);
        }
        {   // Object
            Object[] args = new Object[3];
            args[0] = "Me";
            args[1] = 10;
            args[2] = "Him";
            Bean1<Object> expected = GenericsTransformer.getTypeUnbound(args);
            Bean1<Object> actual = serviceClient.getTypeUnboundForward(args);
            Assert.assertEquals(expected, actual);
        }
    }
    
    private void performTestTypeExtends(GenericsServiceClient serviceClient) {
        {   // Bean2
            Bean2[] args = new Bean2[3];
            for(int i = 0; i < args.length; ++i) {
                args[i] = new Bean2();
                args[i].setName("Name"+i);
            }
            
            Bean1<Bean2> expected = GenericsTransformer.getTypeExtends(args);
            Bean1<Bean2> actual = serviceClient.getTypeExtendsForward(args);
            Assert.assertEquals(expected, actual);
        }
        {   // Bean3 extends Bean2
            Bean3[] args = new Bean3[3];
            for(int i = 0; i < args.length; ++i) {
                args[i] = new Bean3();
                args[i].setName("Name"+i);
                args[i].setAddress("Address"+i);
            }
            
            Bean1<Bean3> expected = GenericsTransformer.getTypeExtends(args);
            Bean1<Bean3> actual = serviceClient.getTypeExtendsForward(args);
            // Assert.assertEquals(expected, actual);
        }
        {   //Bean31 extends Bean2
            Bean31[] args = new Bean31[3];
            for(int i = 0; i < args.length; ++i) {
                args[i] = new Bean31();
                args[i].setName("Name"+i);
                args[i].setAddress("Address"+i);
            }
            
            Bean1<Bean31> expected = GenericsTransformer.getTypeExtends(args);
            Bean1<Bean31> actual = serviceClient.getTypeExtendsForward(args);
            // Assert.assertEquals(expected, actual);
        }
    }

    private void performTestRecursiveTypeBound(GenericsServiceClient serviceClient) {
        {   // Bean1<String>
            Bean1<String>[] args = new Bean1[3];
            for(int i = 0; i < args.length; ++i) {
                args[i] = new Bean1<String>();
                args[i].setItem("Bean."+i);
            }
            Bean1<Bean1<String>> expected = GenericsTransformer.getRecursiveTypeBound(args);
            Bean1<Bean1<String>> actual = serviceClient.getRecursiveTypeBoundForward(args);
            Assert.assertEquals(expected, actual);
        }
        {   // Bean10 extends Bean1<String>
            Bean10[] args = new Bean10[3];
            for(int i = 0; i < args.length; ++i) {
                args[i] = new Bean10();
                args[i].setItem("Bean10."+i);
            }
            Bean1<Bean10> expected = GenericsTransformer.getRecursiveTypeBound(args);
            Bean1<Bean10> actual = serviceClient.getRecursiveTypeBoundForward(args);
            // Assert.assertEquals(expected, actual);
        }
        {   // Bean11 extends Bean1<String>
            Bean11[] args = new Bean11[3];
            for(int i = 0; i < args.length; ++i) {
                args[i] = new Bean11();
                args[i].setItem("Bean11."+i);
            }
            Bean1<Bean11> expected = GenericsTransformer.getRecursiveTypeBound(args);
            Bean1<Bean11> actual = serviceClient.getRecursiveTypeBoundForward(args);
            // Assert.assertEquals(expected, actual);
        }
    }
    
    private void performTestWildcardUnbound(GenericsServiceClient serviceClient) {
        {
            Bean1<?> arg = new Bean1<String>("Me");
            Bean1<?> expected = GenericsTransformer.getWildcardUnbound(arg);
            Bean1<?> actual = serviceClient.getWildcardUnboundForward(arg);
            Assert.assertEquals(expected, actual);
        }
        {
            Bean1<?> arg = new Bean1<Integer>(1);
            Bean1<?> expected = GenericsTransformer.getWildcardUnbound(arg);
            Bean1<?> actual = serviceClient.getWildcardUnboundForward(arg);
            Assert.assertEquals(expected, actual);
        }
    }
    
    private void performTestWildcardSuper(GenericsServiceClient serviceClient) {
        Bean1<? super Bean3> arg = new Bean1<Bean2>();
        Bean3 item = new Bean3();
        item.setName("Name");
        item.setAddress("Address");
        arg.setItem(item);
        Bean1<? super Bean3> expected = GenericsTransformer.getWildcardSuper(arg);
        Bean1<? super Bean3> actual = serviceClient.getWildcardSuperForward(arg);
        Assert.assertEquals(expected, actual);
    }

    private void performTestWildcardExtends(GenericsServiceClient serviceClient) {
        {   // Bean2
            Bean2 temp = new Bean2();
            temp.setName("Me");
            Bean1<? extends Bean2> arg = new Bean1<Bean2>(temp);
            Bean1<? extends Bean2> expected = GenericsTransformer.getWildcardExtends(arg);
            Bean1<? extends Bean2> actual = serviceClient.getWildcardExtendsForward(arg);
            Assert.assertEquals(expected, actual);
        }
        {   // Bean3 extends Bean2
            Bean3 temp = new Bean3();
            temp.setName("Me");
            temp.setAddress("My address");
            Bean1<? extends Bean2> arg = new Bean1<Bean3>(temp);
            Bean1<? extends Bean2> expected = GenericsTransformer.getWildcardExtends(arg);
            Bean1<? extends Bean2> actual = serviceClient.getWildcardExtendsForward(arg);
            // The Bean3 will be unmarshalled into Bean2
            // Assert.assertEquals(expected, actual);
            Assert.assertTrue(actual.getItem() instanceof Bean2);
        }
        {   // Bean31 extends Bean2
            Bean31 temp = new Bean31();
            temp.setName("Me1");
            temp.setAddress("My address1");
            Bean1<? extends Bean2> arg = new Bean1<Bean31>(temp);
            Bean1<? extends Bean2> expected = GenericsTransformer.getWildcardExtends(arg);
            Bean1<? extends Bean2> actual = serviceClient.getWildcardExtendsForward(arg);
            // The Bean31 will be unmarshalled into Bean2
            // Assert.assertEquals(expected, actual);
            Assert.assertTrue(actual.getItem() instanceof Bean2);
        }
    }
    
    private void performTestPolymorphic(GenericsServiceClient serviceClient) {
        {   // Bean2
            Bean2 arg = new Bean2();
            arg.setName("Me");
            Bean2 expected = GenericsTransformer.getPolymorphic(arg);
            Bean2 actual = serviceClient.getPolymorphicForward(arg);
            Assert.assertEquals(expected, actual);
        }
        {   // Bean3 extends Bean2
            Bean3 arg = new Bean3();
            arg.setName("Me");
            arg.setAddress("My address");
            Bean2 expected = GenericsTransformer.getPolymorphic(arg);
            Bean2 actual = serviceClient.getPolymorphicForward(arg);
            Assert.assertEquals(expected.getName(), actual.getName());
        }
    }
}
