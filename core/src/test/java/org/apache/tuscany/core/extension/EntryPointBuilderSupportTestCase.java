package org.apache.tuscany.core.extension;

import junit.framework.TestCase;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;

/**
 * @version $$Rev$$ $$Date$$
 */
public class EntryPointBuilderSupportTestCase extends TestCase {

    public void testGenericReflection() throws Exception {
        EntryPointBuilderSupportTestCase.TestEntryPointBuilder b = new EntryPointBuilderSupportTestCase.TestEntryPointBuilder();
        assertEquals(EntryPointBuilderSupportTestCase.TestBinding.class, b.getImplementationClass());
    }

    public void testNegativeGenericReflection() throws Exception {
        try {
            new EntryPointBuilderSupportTestCase.NonGenericFactoryBuilder();
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


    private class TestEntryPointBuilder extends EntryPointBuilderSupport<EntryPointBuilderSupportTestCase.TestBinding> {

        public Class getImplementationClass() {
            return bindingClass;
        }

        protected EntryPointContextFactory createEntryPointContextFactory(EntryPoint entryPoint, MessageFactory msgFactory) {
            return null;
        }
    }

    private class NonGenericFactoryBuilder extends EntryPointBuilderSupport {

        public Class getImplementationClass() {
            return bindingClass;
        }

        protected EntryPointContextFactory createEntryPointContextFactory(EntryPoint entryPoint, MessageFactory msgFactory) {
            return null;
        }
    }

    private interface TestBinding extends Binding {

    }
}
