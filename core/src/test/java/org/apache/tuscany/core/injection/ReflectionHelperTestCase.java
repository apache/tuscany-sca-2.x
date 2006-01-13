package org.apache.tuscany.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.config.JavaIntrospectionHelper;

public class ReflectionHelperTestCase extends TestCase {

    public ReflectionHelperTestCase() {
        super();
    }

    public ReflectionHelperTestCase(String arg0) {
        super(arg0);
    }

    public void testGetSuperAllFields() throws Exception {
        Set<Field> superBeanFields = JavaIntrospectionHelper.getAllFields(SuperBean.class);
        Assert.assertEquals(SuperBean.ALL_SUPER_FIELDS, superBeanFields.size());
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
                method.invoke(new Bean1(), new Object[]{"foo"});
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

}
