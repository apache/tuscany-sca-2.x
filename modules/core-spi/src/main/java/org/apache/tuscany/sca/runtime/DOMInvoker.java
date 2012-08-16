package org.apache.tuscany.sca.runtime;

import org.oasisopen.sca.annotation.Remotable;
import org.w3c.dom.Node;

@Remotable
public interface DOMInvoker {

    Node invoke(String operation, Node args);
    
}
