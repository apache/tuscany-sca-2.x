package org.apache.tuscany.spi.extension;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class AtomicComponentExtensionTestCase extends TestCase {

    public void testIsEagerInit() throws Exception {
        TestExtension ext = new TestExtension();
        ext.isEagerInit();
    }

    public void testPrepare() throws Exception {
        TestExtension ext = new TestExtension();
        ext.prepare();
    }

    public void testInit() throws Exception {
        TestExtension ext = new TestExtension();
        ext.init(null);
    }

    public void testDestroy() throws Exception {
        TestExtension ext = new TestExtension();
        ext.destroy(null);
    }

    public void testInboundWire() throws Exception {
        TestExtension ext = new TestExtension();
        ext.getInboundWire(null);
    }

    private class TestExtension extends AtomicComponentExtension {
        public TestExtension() {
            super(null, null, null, null);
        }

        public Object getServiceInstance() throws TargetException {
            return null;
        }

        public Object createInstance() throws ObjectCreationException {
            return null;
        }

        public Object getServiceInstance(String name) throws TargetException {
            return null;
        }

        public List getServiceInterfaces() {
            return null;
        }

        public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
            return null;
        }
    }
}
