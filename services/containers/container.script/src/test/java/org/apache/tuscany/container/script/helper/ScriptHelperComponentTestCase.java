package org.apache.tuscany.container.script.helper;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperComponent;
import org.apache.tuscany.container.script.helper.ScriptHelperInstance;
import org.apache.tuscany.container.script.helper.ScriptHelperInstanceFactory;
import org.apache.tuscany.container.script.helper.mock.MockInstanceFactory;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;
import org.easymock.IAnswer;

public class ScriptHelperComponentTestCase extends TestCase {
    
    private ScopeContainer scopeContainer;

    @SuppressWarnings("unchecked")
    public void testCreateTargetInvoker() {
        ScriptHelperComponent component = new ScriptHelperComponent(null,null, null, null, null, scopeContainer, null, null, null);
        
        Operation operation = new Operation("hashCode", null,null,null,false,null);
        ServiceContract contract = new ServiceContract(List.class){};
        operation.setServiceContract(contract);
        TargetInvoker invoker = component.createTargetInvoker("hashCode", operation);
        
        assertNotNull(invoker);
    }

    @SuppressWarnings("unchecked")
    public void testCreateInstance() throws IOException {
        ScriptHelperComponent pc = new ScriptHelperComponent(null,createBSFEasy(), new HashMap(), null, null, scopeContainer, null, null, null);
        Object o = pc.createInstance();
        assertNotNull(o);
        assertTrue(o instanceof ScriptHelperInstance);
    }

    @SuppressWarnings("unchecked")
    public void testCreateInstanceWithRef() throws IOException {
        WireService wireService = createMock(WireService.class);
        expect(wireService.createProxy(isA(RuntimeWire.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.MODULE;
            }
        });
       
        ScriptHelperComponent pc = new ScriptHelperComponent(null,createBSFEasy(), new HashMap(), null, null, scopeContainer, wireService, null, null);
        OutboundWire wire = new OutboundWireImpl();
        wire.setReferenceName("foo");
        pc.addOutboundWire(wire);
        Object o = pc.createInstance();
        assertNotNull(o);
        assertTrue(o instanceof ScriptHelperInstance);
    }

    @SuppressWarnings("unchecked")
    public void testGetServiceInstance() {
        WireService wireService = createMock(WireService.class);
        expect(wireService.createProxy(isA(RuntimeWire.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return "foo";
            }
        });
        replay(wireService);
        ScriptHelperComponent pc = new ScriptHelperComponent(null,null, null, null, null, scopeContainer, wireService, null, null);
        InboundWire wire = new InboundWireImpl();
        pc.addInboundWire(wire);
        assertEquals("foo", pc.getServiceInstance());
    }

    @SuppressWarnings("unchecked")
    public void testGetServiceInstanceFail() {
        ScriptHelperComponent pc = new ScriptHelperComponent(null,null, null, null, null, scopeContainer, null, null, null);
        try {
            pc.getServiceInstance();
            fail();
        } catch (TargetException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetproperties() {
        ScriptHelperComponent pc = new ScriptHelperComponent(null,null, new HashMap(), null, null, scopeContainer, null, null, null);
        assertNotNull(pc.getProperties());
    }

    @SuppressWarnings("unchecked")
    public void testGetServiceInterfaces() {
        List services = new ArrayList();
        ScriptHelperComponent pc = new ScriptHelperComponent(null,null,null, services, null, scopeContainer, null, null, null);
        assertEquals(services, pc.getServiceInterfaces());
    }

    @SuppressWarnings("unchecked")
    public void testCreateAsyncTargetInvoker() {
        ScriptHelperComponent pc = new ScriptHelperComponent(null,null,null, new ArrayList<Class<?>>(), null, scopeContainer, null, null, null);
        assertNotNull(pc.createAsyncTargetInvoker(null, new Operation("foo", null,null,null)));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        this.scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.MODULE;
            }
        });
    }

    public ScriptHelperInstanceFactory createBSFEasy() throws IOException {
//        URL scriptURL = getClass().getResource("foo.mock");
//        InputStream is = scriptURL.openStream();
//        StringBuilder sb = new StringBuilder();
//        int i = 0;
//        while ((i = is.read()) != -1) {
//            sb.append((char) i);
//        }
//        is.close();
//        String script = sb.toString();
        MockInstanceFactory bsfEasy = new MockInstanceFactory("foo.mock", null);
        return bsfEasy;
    }
}
