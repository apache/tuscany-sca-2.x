package org.apache.tuscany.container.script.helper.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.script.helper.ScriptHelperInstance;
import org.apache.tuscany.container.script.helper.ScriptHelperInstanceFactory;

public class MockInstanceFactory extends ScriptHelperInstanceFactory<MockInstance> {

    public MockInstanceFactory(String scriptName, ClassLoader classLoader) {
        super(scriptName, classLoader);
    }

    @Override
    public MockInstance createInstance(List<Class> services, Map<String, Object> context) {
        return new MockInstance();
    }

}

class MockInstance implements ScriptHelperInstance {

    public Object invokeTarget(String operationName, Object[] args) throws InvocationTargetException {
        // TODO Auto-generated method stub
        return null;
    }

}
