package org.apache.tuscany.equinox;

import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor;
import org.osgi.framework.Bundle;
import junit.framework.TestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class EquinoxStartTestCase extends TestCase {

    public void testStart() throws Exception {
        FrameworkAdaptor adaptor = new DefaultAdaptor(new String[]{});
        OSGi osgi = new OSGi(adaptor);
        osgi.launch();
        for (Bundle bundle : osgi.getBundleContext().getBundles()) {

        }
    }
}
