package org.apache.tuscany.spi.extension;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.WireBuilder;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class WireBuilderExtension{//<T extends Context<?>> implements WireBuilder<T> {

//    private BuilderRegistry builderRegistry;
//
//    @Autowire
//    public void setBuilderRegistry(BuilderRegistry builderRegistry) {
//        this.builderRegistry = builderRegistry;
//    }
//
//
//    public WireBuilderExtension() {
//    }
//
//    @Init(eager = true)
//    public void init() throws Exception {
//        builderRegistry.register(this);
//    }
//
//    public void connect(SourceWire<?> sourceWire, TargetWire<?> targetWire, T context) throws BuilderConfigException {
//        for (SourceInvocationChain chain : sourceWire.getInvocationChains().values()) {
//            TargetInvoker invoker = createInvoker(sourceWire.getReferenceName(), chain.getMethod(), context);
//            chain.setTargetInvoker(invoker);
//        }
//    }
//
//    public void completeTargetChain(TargetWire<?> targetWire, T context) throws BuilderConfigException {
//        for (TargetInvocationChain chain : targetWire.getInvocationChains().values()) {
//            Method method = chain.getMethod();
//            TargetInvoker invoker = createInvoker(targetWire.getServiceName(), method, context);
//            chain.setTargetInvoker(invoker);
//        }
//
//    }
//
//    /**
//     * Callback to create the specific <code>TargetInvoker</code> type for dispatching to the target type
//     *
//     * @param targetName the fully qualified name of the wire target
//     * @param operation  the operation the invoker will be associated with
//     */
//    protected abstract TargetInvoker createInvoker(String targetName, Method operation,T context);

}
