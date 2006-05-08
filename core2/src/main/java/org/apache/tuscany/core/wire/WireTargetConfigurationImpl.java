package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.WireTargetConfiguration;
import org.apache.tuscany.spi.wire.TargetInvocationConfiguration;

/**
 * Contains configuration for the target side of a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireTargetConfigurationImpl implements WireTargetConfiguration {

    protected Map<Method, TargetInvocationConfiguration> configurations;
    protected String targetName;


    /**
     * Creates the source side of a wire
     *
     * @param targetName        the name of the target service specified by the wire
     * @param invocationConfigs a collection of target service operation-to-invocation chain mappings
     */
    public WireTargetConfigurationImpl(String targetName, Map<Method, TargetInvocationConfiguration> invocationConfigs) {
        assert (invocationConfigs != null) : "No wire configuration map specified";
        this.targetName = targetName;
        configurations = invocationConfigs;

    }

    public String getServiceName() {
        return targetName;
    }

    public Map<Method, TargetInvocationConfiguration> getInvocationConfigurations() {
        return configurations;
    }


}
