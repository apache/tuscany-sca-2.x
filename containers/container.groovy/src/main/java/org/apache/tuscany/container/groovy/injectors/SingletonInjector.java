package org.apache.tuscany.container.groovy.injectors;

import groovy.lang.GroovyObject;
import org.apache.tuscany.container.groovy.InjectionException;
import org.apache.tuscany.container.groovy.PropertyInjector;

/**
 * Implements a simple injector that just returns an object
 *
 * @version $$Rev$$ $$Date$$
 */
public class SingletonInjector implements PropertyInjector {
    private String name;
    private Object val;

    public SingletonInjector(String name, Object val) {
        this.name = name;
        this.val = val;
    }

    public void inject(GroovyObject instance) throws InjectionException {
        instance.setProperty(name, val);
    }
}
