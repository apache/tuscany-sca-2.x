package org.apache.tuscany.container.groovy;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentType extends ComponentType {
    private Scope lifecycleScope;
    
    public Scope getLifecycleScope() {
        return lifecycleScope;
    }

    public void setLifecycleScope(Scope lifecycleScope) {
        this.lifecycleScope = lifecycleScope;
    }

}
