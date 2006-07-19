package org.apache.tuscany.databinding.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DataBindingTestCase extends TestCase {
    @SuppressWarnings("unused")
    @DataBinding(name = "sdo")
    private Object sdo;

    @DataBinding(name = "sdo")
    public Object m1(@DataBinding(name = "jaxb", context = { @DataContext(key = "contextPath", value = "com.example.ipo.jaxb") })
    Object input) {
        return null;
    }

    public void testDataBinding() throws Exception {
        Field field = getClass().getDeclaredField("sdo");
        DataBinding d = field.getAnnotation(DataBinding.class);
        Assert.assertEquals(d.name(), "sdo");
        Assert.assertEquals(d.context().length, 0);

        Method method = getClass().getMethod("m1", new Class[] { Object.class });
        Annotation[][] annotations = method.getParameterAnnotations();
        DataBinding d2 = (DataBinding) annotations[0][0];
        Assert.assertEquals(d2.name(), "jaxb");
        Assert.assertEquals(d2.context()[0].key(), "contextPath");
        Assert.assertEquals(d2.context()[0].value(), "com.example.ipo.jaxb");

        DataBinding d3 = method.getAnnotation(DataBinding.class);
        Assert.assertEquals(d3.name(), "sdo");
        Assert.assertEquals(d3.context().length, 0);
    }
}
