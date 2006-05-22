package org.apache.tuscany.container.groovy;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.ContextRuntimeException;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GroovyInstanceWrapper extends AbstractLifecycle implements InstanceWrapper {

    private int lifecycleState = UNINITIALIZED;
    private URI script; // pointer to the script
    private GroovyObject groovyObject;

    public GroovyInstanceWrapper(String name, URI script) {
        super(name);
        this.script = script;
    }

    public Object getInstance() {
        return groovyObject;
    }

    public int getLifecycleState() {
        return lifecycleState;
    }

    public void start() throws CoreRuntimeException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        try {
            Class groovyClass = loader.parseClass(new File(script));
            groovyObject = (GroovyObject) groovyClass.newInstance();
            lifecycleState = STARTED;
        } catch (CompilationFailedException e) {
            lifecycleState = ERROR;
            throw new ContextRuntimeException(e);
        } catch (IOException e) {
            lifecycleState = ERROR;
            throw new ContextRuntimeException(e);
        } catch (IllegalAccessException e) {
            lifecycleState = ERROR;
            throw new ContextRuntimeException(e);
        } catch (InstantiationException e) {
            lifecycleState = ERROR;
            throw new ContextRuntimeException(e);
        }

    }

    public void stop() throws CoreRuntimeException {
        groovyObject = null;
        lifecycleState = STOPPED;
    }

}
