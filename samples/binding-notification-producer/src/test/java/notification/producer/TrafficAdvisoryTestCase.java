package notification.producer;

import notification.producer.TestCaseProducer;

import org.apache.tuscany.sca.host.embedded.SCADomain;
//import org.apache.tuscany.sca.notification.remoteProducer.TestCaseProducer;

import junit.framework.TestCase;

public class TrafficAdvisoryTestCase extends TestCase {

    private SCADomain domain;
    
    public void testTrafficAdvisoryNotification() throws Exception {
        System.out.println("Only instantiating and closing domain ...");
    }

    protected void setUp() throws Exception {
        try {
            domain = SCADomain.newInstance("TrafficAdvisoryNotification.composite");
            domain.getService(TestCaseProducer.class, "TrafficAdvisoryProducer");
        } catch(Throwable e) {
            e.printStackTrace();
            if (e instanceof Exception) {
                throw (Exception)e;
            }
            else {
                throw new Exception(e);
            }
        }
    }
    
    protected void tearDown() throws Exception {
        domain.close();
    }
}
