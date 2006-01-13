package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.model.assembly.AssemblyModelObject;

/**
 * Implementations are responsible for generating a runtime configuration model
 * from a logical configuration model. The logical configuration model (LCM) is
 * decorated with the runtime configuration model (RCM).
 *
 * @version $Rev$ $Date$
 * @param <T>
 * @see RuntimeConfiguration
 */
public interface RuntimeConfigurationBuilder<Y extends Context> {

    /**
     * Sets the logical configuration model node to visit
     */
    public void setModelObject(AssemblyModelObject object);

    /**
     * Sets the parent context of the context type the current builder produces
     */
    public void setParentContext(Y context);

    /**
     * Compiles the runtime configuration model and decorates the LCM with it
     *
     * @throws BuilderException
     */
    public void build() throws BuilderException;

}
