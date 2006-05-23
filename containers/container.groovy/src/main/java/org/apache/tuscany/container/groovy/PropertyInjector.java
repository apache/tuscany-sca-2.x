package org.apache.tuscany.container.groovy;

import groovy.lang.GroovyObject;

/**
 * Injects a property on a {@link groovy.lang.GroovyObject}
 * @version $$Rev$$ $$Date$$
 */
public interface PropertyInjector {

    public void inject(GroovyObject instance) throws InjectionException;

}
