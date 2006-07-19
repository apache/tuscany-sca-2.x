package org.apache.tuscany.core.policy.async;

import javax.resource.spi.work.WorkManager;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.OneWay;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import static org.apache.tuscany.spi.policy.PolicyBuilderRegistry.INITIAL;
import org.apache.tuscany.spi.policy.TargetPolicyBuilder;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import org.apache.tuscany.core.monitor.NullMonitorFactory;

/**
 * A policy builder for handling the {@link OneWay} annotation
 *
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class AsyncPolicyBuilder implements TargetPolicyBuilder {

    private PolicyBuilderRegistry builderRegistry;
    private WorkManager workManager;
    private AsyncMonitor monitor;

    public AsyncPolicyBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.registerTargetBuilder(INITIAL, this);
        if (monitor == null) {
            monitor = new NullMonitorFactory().getMonitor(AsyncMonitor.class);
        }
    }

    @Autowire
    public void setBuilderRegistry(PolicyBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    @org.apache.tuscany.spi.annotation.Monitor
    public void setMonitor(AsyncMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowire
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

    public void build(ServiceDefinition serviceDefinition, InboundWire<?> wire) throws BuilderException {
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            // TODO fix this - it should be represented by the model and not through an annotation
            if (chain.getMethod().getAnnotation(OneWay.class) != null) {
                chain.addInterceptor(new AsyncInterceptor(workManager, monitor));
            }
        }
    }
}
