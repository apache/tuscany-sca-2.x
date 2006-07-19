package localwire.cdi;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import org.apache.tuscany.test.SCATestCase;

/**
 * @version $Rev: 421078 $ $Date: 2006-07-11 19:16:56 -0700 (Tue, 11 Jul 2006) $
 */
public class LocalWireTestCase extends SCATestCase {
    public void testMessage() {
        CompositeContext context = CurrentCompositeContext.getContext();
        assertNotNull(context);

        Source source = context.locateService(Source.class, "SourceComponent");
        assertNotNull(source);
        assertEquals("Echoing: Ciao", source.invoke("Ciao"));
    }
}
