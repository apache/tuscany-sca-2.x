package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface WireSourceConfiguration {
    /**
     * Returns the qualified name of the target service specified by the wire
     */
    QualifiedName getTargetName();

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a
     * target service.
     */
    Map<Method, SourceInvocationConfiguration> getInvocationConfigurations();

    /**
     * Returns the name of the source reference
     */
    String getReferenceName();
}
