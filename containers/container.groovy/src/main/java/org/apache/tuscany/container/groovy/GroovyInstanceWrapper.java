package org.apache.tuscany.container.groovy;

import groovy.lang.GroovyObject;
import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.context.InstanceWrapper;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GroovyInstanceWrapper extends AbstractLifecycle implements InstanceWrapper {

    private int lifecycleState = UNINITIALIZED;
    private GroovyAtomicContext context;
    private GroovyObject groovyObject;


    public GroovyInstanceWrapper(GroovyAtomicContext context, GroovyObject groovyObject) {
        this.context = context;
        this.groovyObject = groovyObject;
    }

    public Object getInstance() {
        return groovyObject;
    }

    public int getLifecycleState() {
        return lifecycleState;
    }

    public void start() throws CoreRuntimeException {
        try {
            context.init(groovyObject);
            lifecycleState = STARTED;
        } catch (ObjectCreationException e) {
            lifecycleState = ERROR;
            throw e;
        }
    }

    public void stop() throws CoreRuntimeException {
        groovyObject = null;
        lifecycleState = STOPPED;
    }

}
