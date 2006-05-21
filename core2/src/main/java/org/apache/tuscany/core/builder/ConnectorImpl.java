package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Collection;

import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.SourceAutowire;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {

    public <T> void connect(Context<T> source, CompositeContext parent) {
        if (source instanceof ComponentContext) {
            ComponentContext<T> sourceContext = (ComponentContext<T>) source;
            for (SourceWire<T> sourceWire : sourceContext.getSourceWires()) {
                if (sourceWire instanceof SourceAutowire) {
                    continue;
                }
                try {
                    connect(sourceWire, parent, sourceContext.getScope());
                } catch (BuilderConfigException e) {
                    e.addContextName(sourceContext.getName());
                    e.addContextName(parent.getName());
                    throw e;
                }
            }
        } else if (source instanceof ServiceContext) {
            ServiceContext<T> sourceContext = (ServiceContext<T>) source;
            SourceWire<T> sourceWire = sourceContext.getSourceWire();
            try {
                connect(sourceWire, parent, sourceContext.getScope());
            } catch (BuilderConfigException e) {
                e.addContextName(sourceContext.getName());
                e.addContextName(parent.getName());
                throw e;
            }
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid source context type");
            e.setIdentifier(source.getName());
            e.addContextName(parent.getName());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void connect(SourceWire<T> sourceWire, CompositeContext<?> parent, Scope sourceScope) throws BuilderConfigException {
        assert(sourceScope != null): "Source scope was null";
        assert(sourceWire.getTargetName() != null): "Wire target name was null";
        QualifiedName targetName = sourceWire.getTargetName();
        Context<?> target = parent.getContext(targetName.getPartName());
        if (target == null) {
            BuilderConfigException e = new BuilderConfigException("Target not found for reference" + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }
        TargetWire<?> targetWire;
        if (target instanceof ComponentContext) {
            ComponentContext<?> targetContext = (ComponentContext<?>) target;
            targetWire = targetContext.getTargetWires().get(targetName.getPortName());
            if (targetWire == null) {
                BuilderConfigException e = new BuilderConfigException("Target service not found for reference " + sourceWire.getReferenceName());
                e.setIdentifier(targetName.getPortName());
                throw e;
            }
            if (!sourceWire.getBusinessInterface().isAssignableFrom(targetWire.getBusinessInterface())){
               throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(sourceWire, (TargetWire<T>)targetWire, target, isOptimizable(sourceScope, targetContext.getScope()));
        } else if (target instanceof ReferenceContext) {
            targetWire = ((ReferenceContext)target).getTargetWire();
            assert(targetWire != null);
            if (!sourceWire.getBusinessInterface().isAssignableFrom(targetWire.getBusinessInterface())){
               throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(sourceWire, (TargetWire<T>)targetWire, target, isOptimizable(sourceScope, target.getScope()));
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid wire target type for reference " + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
        }


    }

    private <T>  void connect(SourceWire<T> source, TargetWire<T> targetWire, Context<?> target, boolean optimizable) {
        Map<Method, TargetInvocationChain> targetInvocationConfigs = targetWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && source.getInvocationChains().isEmpty() && targetInvocationConfigs.isEmpty()) {
            source.setTargetWire(targetWire);
            return;
        }
        for (SourceInvocationChain sourceInvocationConfig : source.getInvocationChains().values()) {
            // match wire chains
            TargetInvocationChain targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
            if (targetInvocationConfig == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible source and targetWire interface types for reference");
                e.setIdentifier(source.getReferenceName());
                throw e;
            }
            // if handler is configured, add that
            if (targetInvocationConfig.getRequestHandlers() != null) {
                sourceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig
                        .getRequestHandlers()));
                sourceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig
                        .getResponseHandlers()));
            } else {
                // no handlers, just connect interceptors
                if (targetInvocationConfig.getHeadInterceptor() == null) {
                    BuilderConfigException e = new BuilderConfigException("No targetWire handler or interceptor for operation");
                    e.setIdentifier(targetInvocationConfig.getMethod().getName());
                    throw e;
                }
                if (!(sourceInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                        .getHeadInterceptor() instanceof InvokerInterceptor)) {
                    // check that we do not have the case where the only interceptors are invokers since we just need one
                    sourceInvocationConfig.setTargetInterceptor(targetInvocationConfig.getHeadInterceptor());
                }
            }
        }

        if (target instanceof ReferenceContext){
            attachInvoker(targetWire.getServiceName(),source.getInvocationChains().values(),(ReferenceContext)target);
        }else{
            attachInvoker(targetWire.getServiceName(),source.getInvocationChains().values(),(ComponentContext)target);
        }
    }

    private void attachInvoker(String serviceName, Collection<SourceInvocationChain> chains, ComponentContext<?> target){
        for (SourceInvocationChain chain : chains) {
            TargetInvoker invoker = target.createTargetInvoker(serviceName, chain.getMethod());
            // TODO fix cacheable attrivute
            //invoker.setCacheable(cacheable);
            chain.setTargetInvoker(invoker);
        }
    }

    private void attachInvoker(String serviceName, Collection<SourceInvocationChain> chains, ReferenceContext<?> target){
        for (SourceInvocationChain chain : chains) {
            TargetInvoker invoker = target.createTargetInvoker(serviceName, chain.getMethod());
            // TODO fix cacheable attrivute
            //invoker.setCacheable(cacheable);
            chain.setTargetInvoker(invoker);
        }
    }

    private boolean isOptimizable(Scope pReferrer, Scope pReferee) {
        if (pReferrer == Scope.UNDEFINED || pReferee == Scope.UNDEFINED) {
            return false;
        }
        if (pReferee == pReferrer) {
            return true;
        } else if (pReferrer == Scope.STATELESS) {
            return true;
        } else if (pReferee == Scope.STATELESS) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SESSION) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.MODULE) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.MODULE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.COMPOSITE && pReferee == Scope.MODULE) {
            // case where a service context points to a module scoped component
            return true;
        } else return pReferrer == Scope.MODULE && pReferee == Scope.COMPOSITE;
    }

}
