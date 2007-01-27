package org.apache.tuscany.container.script.mock;

import org.apache.bsf.BSFException;
import org.apache.bsf.util.BSFEngineImpl;

public class MockBSFEngine extends BSFEngineImpl {

    public Object call(Object object, String name, Object[] args) throws BSFException {
        if ("bang".equals(name)) {
            throw new RuntimeException(name);
        }
        
        String resp = name + ":";
        if (args != null) {
            for (Object o : args) {
                resp += " " + String.valueOf(o);
            }
        }
        return resp;
    }

    public Object eval(String source, int lineNo, int columnNo, Object expr) throws BSFException {
        // not used for the mock tests
        return null;
    }

}
