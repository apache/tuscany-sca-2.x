package org.apache.tuscany.container.javascript;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Scope;

/**
 * Model object representing a JavaScript component type
 */
public class JavaScriptComponentType extends ComponentType {
    private Scope lifecycleScope;
    
    public Scope getLifecycleScope() {
        return lifecycleScope;
    }

    public void setLifecycleScope(Scope lifecycleScope) {
        this.lifecycleScope = lifecycleScope;
    }

}
