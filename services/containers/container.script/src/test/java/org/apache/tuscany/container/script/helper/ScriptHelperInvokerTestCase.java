package org.apache.tuscany.container.script.helper;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperComponent;
import org.apache.tuscany.container.script.helper.ScriptHelperInstance;
import org.apache.tuscany.container.script.helper.ScriptHelperInvoker;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Scope;
import org.easymock.IAnswer;

public class ScriptHelperInvokerTestCase extends TestCase {

    private ScriptHelperComponent component;

    public void testInvokeTarget() throws InvocationTargetException {
        ScriptHelperInvoker invoker = new ScriptHelperInvoker("hello", component);
        assertEquals("hello petra", invoker.invokeTarget(null));
    }

    public void testInvokeTargetException() throws InvocationTargetException, SecurityException, NoSuchMethodException {
         ScriptHelperInvoker badInvoker = new ScriptHelperInvoker("bang", component);
         try {
             badInvoker.invokeTarget(null);
             fail();
         } catch (InvocationTargetException e) {
            // expected
         }
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();

        ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.MODULE;
            }
        });
        expect(scopeContainer.getInstance(isA(AtomicComponent.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return new ScriptHelperInstance(){
                    public Object invokeTarget(String operationName, Object[] args) throws InvocationTargetException {
                        if ("bang".equals(operationName)) {
                            throw new RuntimeException("bang");
                        }
                        return "hello petra";
                    }};
            }
        });
        replay(scopeContainer);

        this.component = new ScriptHelperComponent(null, null, null, null, null, scopeContainer, null, null, null);
    }
}
