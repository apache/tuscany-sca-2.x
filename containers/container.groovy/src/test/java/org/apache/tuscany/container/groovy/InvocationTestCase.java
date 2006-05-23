package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.GroovyObject;
import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.SourceWire;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InvocationTestCase extends MockObjectTestCase {

    private String script1 = "import org.apache.tuscany.container.groovy.mock.Greeting;" +
            "class Foo implements Greeting{" +
            "   Greeting wire;" +
            "   public String greet(String name){" +
            "       return wire.greet(name);  " +
            "   }" +
            "}";

    private String script2 = "def greet(name) { return name }";

    public void testBasicClassAndWireInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicContext<Greeting> context = new GroovyAtomicContext<Greeting>("source", script1, services, Scope.MODULE, null);
        context.setScopeContext(scope);
        Mock mock = mock(SourceWire.class);
        mock.expects(atLeastOnce()).method("getTargetService").will(
                returnValue(new Greeting() {
                    public String greet(String name) {
                        return name;
                    }
                }));
        mock.stubs().method("getReferenceName").will(returnValue("wire"));
        SourceWire<Greeting> wire = (SourceWire<Greeting>) mock.proxy();
        context.addSourceWire(wire);
        Greeting greeting = context.getService();
        assertEquals("foo", greeting.greet("foo"));
        scope.stop();
    }


    public void testBasicScriptInvocation() throws Exception {
        ModuleScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyAtomicContext<GroovyObject> context = new GroovyAtomicContext<GroovyObject>("source", script2, services, Scope.MODULE, null);
        context.setScopeContext(scope);
        GroovyObject object = context.getService();
        assertEquals("foo", object.invokeMethod("greet", "foo"));
        scope.stop();
    }

}
