package org.apache.tuscany.core.extension;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.impl.ExternalServiceContextFactory;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ExternalService;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ExternalServiceBuilderSupportTestCase extends TestCase {

    public void testGenericReflection() throws Exception {
        ExternalServiceBuilderSupportTestCase.TestExternalServiceBuilder b = new ExternalServiceBuilderSupportTestCase.TestExternalServiceBuilder();
        assertEquals(ExternalServiceBuilderSupportTestCase.TestBinding.class, b.getImplementationClass());
    }

    public void testNegativeGenericReflection() throws Exception {
        try {
            new ExternalServiceBuilderSupportTestCase.NonGenericFactoryBuilder();
            fail("AssertionError expected on non-genericized subclass of " + ContextFactoryBuilderSupport.class.getName());
        } catch (AssertionError e) {
            // indicates success
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    private class TestExternalServiceBuilder extends ExternalServiceBuilderSupport<ExternalServiceBuilderSupportTestCase.TestBinding> {


        public Class getImplementationClass() {
            return bindingClass;
        }

        protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) {
            return null;
        }
    }

    private class NonGenericFactoryBuilder extends ExternalServiceBuilderSupport {


        public Class getImplementationClass() {
            return bindingClass;
        }


        protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) {
            return null;
        }
    }

    private interface TestBinding extends Binding {

    }
}
