package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import junit.framework.TestCase;
import org.apache.tuscany.container.groovy.mock.Greeting;
import static org.apache.tuscany.test.ArtifactFactory.createWireService;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScriptInvokeTestCase extends TestCase {

    private static final String SCRIPT = "def greet(name) { return name }";

    private Class<? extends GroovyObject> implClass;
    private ScopeContainer scopeContainer;

    /**
     * Tests the invocation of a Groovy "script" as opposed to a class
     */
    public void testBasicScriptInvocation() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName("source");
        configuration.setGroovyClass(implClass);
        configuration.setServices(services);
        configuration.setScopeContainer(scopeContainer);
        configuration.setWireService(createWireService());
        GroovyAtomicComponent<GroovyObject> context = new GroovyAtomicComponent<GroovyObject>(configuration);
        GroovyObject object = context.getServiceInstance();
        assertEquals("foo", object.invokeMethod("greet", "foo"));
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        GroovyClassLoader cl = new GroovyClassLoader(getClass().getClassLoader());
        implClass = cl.parseClass(SCRIPT);
        scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getInstance(isA(AtomicComponent.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return ((AtomicComponent) getCurrentArguments()[0]).createInstance();
            }
        });
        replay(scopeContainer);
    }
}
