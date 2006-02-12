package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.model.assembly.AssemblyModelObject;

/**
 * Implementations are responsible for generating a runtime configuration model
 * from a logical configuration model. The logical configuration model (LCM) is
 * decorated with the runtime configuration model (RCM).
 *
 * @version $Rev$ $Date$
 * @see RuntimeConfiguration
 */
public interface RuntimeConfigurationBuilder<Y extends Context> {
    /**
     * Builds a runtime configuration for the supplied model object for registration
     * under the supplied context.
     *
     * @param object the logical configuration model node
     * @param context the context that will be the parent of the built context
     * @throws BuilderException
     */
    public void build(AssemblyModelObject object, Y context) throws BuilderException;

}
