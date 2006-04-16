package org.apache.tuscany.core.wire;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.message.MessageFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represents the source side of a wire. When a client component implementation is injected with a service proxy , source- and
 * target-side proxy configurations are "bridged" together. This concatenated configuration may then be used to generate a proxy
 * implementing the particular business interface required by the client.
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public class WireSourceConfiguration extends WireConfiguration {

    protected String referenceName;

    /**
     * Creates the source side of a wire
     *
     * @param referenceName     the name of the reference the wire is associated with
     * @param targetName        the qualified name of the service represented by this configuration
     * @param invocationConfigs a collection of operation-to-wire configuration mappings for the service
     * @param proxyClassLoader  the classloader to use when creating a proxy
     * @param messageFactory    the factory used to create wire messages
     */
    public WireSourceConfiguration(String referenceName, QualifiedName targetName,
                                   Map<Method, InvocationConfiguration> invocationConfigs, ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        super(targetName, invocationConfigs, proxyClassLoader, messageFactory);
        assert (referenceName != null) : "No wire reference name specified";
        this.referenceName = referenceName;
    }


    /**
     * Returns the name of the source reference for the wire
     */
    public String getReferenceName() {
        return referenceName;
    }
}
