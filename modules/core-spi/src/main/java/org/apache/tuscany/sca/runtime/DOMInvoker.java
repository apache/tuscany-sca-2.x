package org.apache.tuscany.sca.runtime;

import org.w3c.dom.Node;

public interface DOMInvoker {

    Node invoke(String operation, Node args);
    
}
