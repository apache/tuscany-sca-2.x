package org.apache.tuscany.spi.model;

import java.util.Collection;
/**
*
* Represents capability of being attached with Intent and PolicySet.
*
*/
public interface PolicyAttachable {
    /**
     * Get the name of PolicySet attached
     * @return the name of PolicySet
     */
    String getPolicySet();
    /**
     * Get collection contains <code>IntentName</code> required.
     * @return collection contains <code>IntentName</code> required.
     */
    Collection<IntentName> getRequiredIntents();

}