package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("REQUEST")
public class RequestScopeInitOnlyComponent extends SessionScopeComponentImpl {

    boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    @Init
    public void init() {
        initialized = true;
    }


}
