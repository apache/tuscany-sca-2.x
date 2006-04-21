package org.apache.tuscany.binding.jsonrpc.mocks.tuscany;

import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.EventFilter;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.event.Event;

public class MockEntryPointContext implements EntryPointContext {

    private Object instance;

    public MockEntryPointContext(Object instance) {
        this.instance = instance;
    }

    public Object getHandler() throws TargetException {

        return null;
    }

    public String getName() {

        return null;
    }

    public void setName(String name) {

    }

    public int getLifecycleState() {

        return 0;
    }

    public void start() throws CoreRuntimeException {

    }

    public void stop() throws CoreRuntimeException {

    }

    public Object getInstance(QualifiedName qName) throws TargetException {

        return instance;
    }

    public void publish(Event object) {

    }

    public void addListener(RuntimeEventListener listener) {

    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {

    }

    public void removeListener(RuntimeEventListener listener) {

    }

}
