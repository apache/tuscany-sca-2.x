package org.apache.tuscany.core.policy.async;

import javax.resource.spi.work.WorkManager;

import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.policy.TargetPolicyBuilder;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.OneWay;

/**
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class AsyncPolicyBuilder implements TargetPolicyBuilder {

    private PolicyBuilderRegistry builderRegistry;
    private WorkManager workManager;

    public AsyncPolicyBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.registerTargetBuilder(this);
    }

    @Autowire
    public void setBuilderRegistry(PolicyBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    @Autowire
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

    public void build(Service service, TargetWire<?> wire) throws BuilderException {
        for (TargetInvocationChain chain : wire.getInvocationChains().values()) {
            if (chain.getMethod().getAnnotation(OneWay.class) != null) {
                chain.addInterceptor(new AsyncInterceptor(workManager));
            }
        }
    }
}
