/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.config;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.Target;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaIntrospectionHelperTestCase extends TestCase {

    public JavaIntrospectionHelperTestCase() {
        super();
    }

    public JavaIntrospectionHelperTestCase(String arg0) {
        super(arg0);
    }

    public void testGetSuperAllFields() throws Exception {
        Set<Field> superBeanFields = JavaIntrospectionHelper.getAllFields(SuperBean.class);
        Assert.assertEquals(SuperBean.ALL_SUPER_FIELDS, superBeanFields.size());
    }

    public void testBean1AllPublicProtectedFields() throws Exception {
        Set<Field> beanFields = JavaIntrospectionHelper.getAllPublicAndProtectedFields(Bean1.class);
        Assert.assertEquals(4, beanFields.size());                //Bean1.ALL_BEAN1_PUBLIC_PROTECTED_FIELDS
    }

    public void testBean1AllFields() throws Exception {
        Set<Field> beanFields = JavaIntrospectionHelper.getAllFields(Bean1.class);
        Assert.assertEquals(Bean1.ALL_BEAN1_FIELDS, beanFields.size());
    }

    public void testGetSuperAllMethods() throws Exception {
        Set<Method> superBeanMethods = JavaIntrospectionHelper.getAllUniqueMethods(SuperBean.class);
        Assert.assertEquals(SuperBean.ALL_SUPER_METHODS, superBeanMethods.size());
    }

    public void testGetBean1AllMethods() throws Exception {
        Set<Method> beanMethods = JavaIntrospectionHelper.getAllUniqueMethods(Bean1.class);
        Assert.assertEquals(Bean1.ALL__BEAN1_METHODS, beanMethods.size());
    }

    public void testOverrideMethod() throws Exception {
        Set<Method> beanFields = JavaIntrospectionHelper.getAllUniqueMethods(Bean1.class);
        boolean invoked = false;
        for (Method method : beanFields) {
            if (method.getName().equals("override")) {
                method.invoke(new Bean1(), "foo");
                invoked = true;
            }
        }
        if (!invoked) {
            throw new Exception("Override never invoked");
        }
    }

    public void testNoOverrideMethod() throws Exception {
        Set<Method> beanFields = JavaIntrospectionHelper.getAllUniqueMethods(Bean1.class);
        boolean found = false;
        for (Method method : beanFields) {
            if (method.getName().equals("noOverride") && method.getParameterTypes().length == 0) {
                found = true;
            }
        }
        if (!found) {
            throw new Exception("No override not found");
        }
    }

    public void testGetBean1AllFields() throws Exception {
        Set<Field> bean1 = JavaIntrospectionHelper.getAllFields(Bean1.class);
        Assert.assertEquals(Bean1.ALL_BEAN1_FIELDS, bean1.size());
    }

    public void testDefaultConstructor() throws Exception {
        Constructor ctr = JavaIntrospectionHelper.getDefaultConstructor(Bean2.class);
        Assert.assertEquals(ctr, Bean2.class.getConstructor());
        Assert.assertTrue(Bean2.class == ctr.newInstance((Object[]) null).getClass());
    }
    
    
    public void testFindMultiplicityByFieldName() throws Exception{
        Set<Field> fields = JavaIntrospectionHelper.getAllFields(getClass());
        Set<Method> methods = JavaIntrospectionHelper.getAllUniqueMethods(getClass());
        
        Assert.assertNotNull(JavaIntrospectionHelper.findMultiplicityFieldByName("testList",fields));
        Assert.assertNotNull(JavaIntrospectionHelper.findMultiplicityMethodByName("fooMethod",methods));

        // this array is not an interface
        Assert.assertNull(JavaIntrospectionHelper.findMultiplicityFieldByName("testStringArray",fields));
        Assert.assertNotNull(JavaIntrospectionHelper.findMultiplicityFieldByName("testArray",fields));
        Assert.assertNotNull(JavaIntrospectionHelper.findMultiplicityMethodByName("setTestArray",methods));
    }
    
    /**
     * Tests generics introspection capabilities
     */
    public void testGenerics() throws Exception{
       
        List classes = JavaIntrospectionHelper.getGenerics(getClass().getDeclaredField("testList").getGenericType());
        Assert.assertEquals(1,classes.size());
        Assert.assertEquals(String.class,classes.get(0));

        classes = JavaIntrospectionHelper.getGenerics(getClass().getDeclaredField("testNoGenericsList").getGenericType());
        Assert.assertEquals(0,classes.size());
        
        classes = JavaIntrospectionHelper.getGenerics(getClass().getDeclaredField("testMap").getGenericType());
        Assert.assertEquals(2,classes.size());
        Assert.assertEquals(String.class,classes.get(0));
        Assert.assertEquals(Bean1.class,classes.get(1));

        classes = JavaIntrospectionHelper.getGenerics(getClass().getDeclaredMethod("fooMethod", Map.class).getGenericParameterTypes()[0]);
        Assert.assertEquals(2,classes.size());
        Assert.assertEquals(String.class,classes.get(0));
        Assert.assertEquals(Bean1.class,classes.get(1));

        classes = JavaIntrospectionHelper.getGenerics(getClass().getDeclaredMethod("fooMethod", List.class).getGenericParameterTypes()[0]);
        Assert.assertEquals(1,classes.size());
        Assert.assertEquals(String.class,classes.get(0));
    
    }

    private List testNoGenericsList;

    private List<String> testList;

    private Map<String,Bean1> testMap;

    private void fooMethod(List<String> foo){

    }

    private void fooMethod(Map<String, Bean1> foo){

    }

    private Target[] testArray;
    private String[] testStringArray;

    public void setTestArray(Target[] array){}
}
