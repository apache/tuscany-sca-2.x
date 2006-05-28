package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.wire.BridgingHandler;
import org.apache.tuscany.core.wire.BridgingInterceptor;
import org.apache.tuscany.core.wire.BridgingResponseInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.MessageDispatcher;
import org.apache.tuscany.core.wire.RequestResponseInterceptor;
import org.apache.tuscany.core.wire.SourceAutowire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {

    public <T> void connect(Context<T> source) {
        CompositeContext parent = source.getParent();
        Scope scope = source.getScope();
        if (source instanceof AtomicContext) {
            AtomicContext<T> sourceContext = (AtomicContext<T>) source;
            for (List<SourceWire> sourceWires : sourceContext.getSourceWires().values()) {
                for (SourceWire<T> sourceWire : sourceWires) {
                    if (sourceWire instanceof SourceAutowire) {
                        continue;
                    }
                    try {
                        connect(sourceWire, parent, scope);
                    } catch (BuilderConfigException e) {
                        e.addContextName(source.getName());
                        e.addContextName(parent.getName());
                        throw e;
                    }
                }
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
        if (target instanceof AtomicContext) {
            AtomicContext<?> targetContext = (AtomicContext<?>) target;
            targetWire = targetContext.getTargetWire(targetName.getPortName());
            if (targetWire == null) {
                BuilderConfigException e = new BuilderConfigException("Target service not found for reference " + sourceWire.getReferenceName());
                e.setIdentifier(targetName.getPortName());
                throw e;
            }
            if (!sourceWire.getBusinessInterface().isAssignableFrom(targetWire.getBusinessInterface())) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(sourceWire, (TargetWire<T>) targetWire, target, isOptimizable(sourceScope, targetContext.getScope()));
        } else if (target instanceof ReferenceContext) {
            targetWire = ((ReferenceContext) target).getTargetWire();
            assert(targetWire != null);
            if (!sourceWire.getBusinessInterface().isAssignableFrom(targetWire.getBusinessInterface())) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(sourceWire, (TargetWire<T>) targetWire, target, isOptimizable(sourceScope, target.getScope()));
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid wire target type for reference " + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void connect(TargetWire<T> sourceWire, Context<?> targetContext) throws BuilderConfigException {
        TargetWire<T> targetWire;
        if (targetContext instanceof ComponentContext) {
            targetWire = ((ComponentContext) targetContext).getTargetWire(sourceWire.getServiceName());
        } else if (targetContext instanceof ReferenceContext) {
            targetWire = ((ReferenceContext) targetContext).getTargetWire();
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid target context type");
            e.setIdentifier(targetContext.getName());
            throw e;
        }
        // perform optimization, if possible
        if (sourceWire.getInvocationChains().isEmpty() && targetWire.getInvocationChains().isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            return;
        }
        for (TargetInvocationChain sourceChain : sourceWire.getInvocationChains().values()) {
            if (targetWire.getInvocationChains() != null || targetWire.getInvocationChains().isEmpty()) {
                if (sourceChain.getTailInterceptor() != null &&
                        !(sourceChain.getTailInterceptor() instanceof InvokerInterceptor)) {
                    sourceChain.getTailInterceptor().setNext(new InvokerInterceptor());
                } else {
                    // special case where we need to attach the invoker of the second chain
                    if (targetContext instanceof ComponentContext) {
                        sourceChain.setTargetInvoker(((ComponentContext) targetContext).createTargetInvoker(
                                sourceWire.getServiceName(), sourceChain.getMethod()));
                    } else if (targetContext instanceof ReferenceContext) {
                        sourceChain.setTargetInvoker(((ReferenceContext) targetContext).createTargetInvoker(
                                sourceWire.getServiceName(), sourceChain.getMethod()));
                    }
                }
            }
            TargetInvocationChain targetChain = targetWire.getInvocationChains().get(sourceChain.getMethod());
            if (sourceChain.getTailInterceptor() != null) {
                if (targetChain.getRequestHandlers() != null) {
                    MessageChannel requestChannel = new MessageChannelImpl(targetChain.getRequestHandlers());
                    MessageChannel responseChannel = new MessageChannelImpl(targetChain.getResponseHandlers());
                    sourceChain.getTailInterceptor().setNext(new RequestResponseInterceptor(null, requestChannel, null, responseChannel));
                } else if (targetChain.getResponseHandlers() != null) {
                    if (targetChain.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("Target chain must have an interceptor");
                        e.setIdentifier(targetChain.getMethod().getName());
                        throw e;
                    }
                    MessageChannel responseChannel = new MessageChannelImpl(targetChain.getResponseHandlers());
                    sourceChain.getTailInterceptor().setNext(new BridgingResponseInterceptor(targetChain.getHeadInterceptor(), responseChannel));
                } else {
                    if (targetChain.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("Target chain must have an interceptor");
                        e.setIdentifier(targetChain.getMethod().getName());
                        throw e;
                    }
                    sourceChain.getTailInterceptor().setNext(new BridgingInterceptor(targetChain.getHeadInterceptor()));
                }
            } else {
                // no target interceptor
                List<MessageHandler> sourceRequestHandlers = sourceChain.getRequestHandlers();
                List<MessageHandler> targetRequestHandlers = targetChain.getRequestHandlers();
                if (sourceRequestHandlers != null && !sourceRequestHandlers.isEmpty()) {
                    sourceRequestHandlers.add(new BridgingHandler(targetRequestHandlers.get(0)));
                }
                List<MessageHandler> sourceResponseHandlers = sourceChain.getResponseHandlers();
                List<MessageHandler> targetResponseHandlers = targetChain.getResponseHandlers();
                if (sourceResponseHandlers != null && !sourceResponseHandlers.isEmpty()) {
                    sourceResponseHandlers.add(new BridgingHandler(targetResponseHandlers.get(0)));
                }
            }
        }
    }

    /**
     * Public access set for unit testing
     */
    public <T> void connect(SourceWire<T> source, TargetWire<T> targetWire, Context<?> target, boolean optimizable) {
        Map<Method, TargetInvocationChain> targetInvocationConfigs = targetWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && source.getInvocationChains().isEmpty() && targetInvocationConfigs.isEmpty()) {
            source.setTargetWire(targetWire);
            return;
        }
        for (SourceInvocationChain sourceChain : source.getInvocationChains().values()) {
            // match wire chains
            TargetInvocationChain targetChain = targetInvocationConfigs.get(sourceChain.getMethod());
            if (targetChain == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible source and target chain interfaces for reference");
                e.setIdentifier(source.getReferenceName());
                throw e;
            }
            // if handlers are configured, add them
            if (targetChain.getRequestHandlers() != null || targetChain.getResponseHandlers() != null) {
                if (targetChain.getRequestHandlers() == null) {
                    // the target may not have request handlers, so bridge it on the source
                    if (targetChain.getHeadInterceptor() != null) {
                        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
                        handlers.add(new MessageDispatcher(targetChain.getHeadInterceptor()));
                        MessageChannel channel = new MessageChannelImpl(handlers);
                        sourceChain.setTargetRequestChannel(channel);
                    } else {
                        BuilderConfigException e = new BuilderConfigException("Target chain must have an interceptor");
                        e.setIdentifier(targetChain.getMethod().getName());
                        throw e;
                    }
                } else {
                    sourceChain.setTargetRequestChannel(new MessageChannelImpl(targetChain
                            .getRequestHandlers()));
                }
                sourceChain.setTargetResponseChannel(new MessageChannelImpl(targetChain
                        .getResponseHandlers()));
            } else {
                // no handlers, just connect interceptors
                if (targetChain.getHeadInterceptor() == null) {
                    BuilderConfigException e = new BuilderConfigException("No chain handler or interceptor for operation");
                    e.setIdentifier(targetChain.getMethod().getName());
                    throw e;
                }
                if (!(sourceChain.getTailInterceptor() instanceof InvokerInterceptor && targetChain
                        .getHeadInterceptor() instanceof InvokerInterceptor)) {
                    // check that we do not have the case where the only interceptors are invokers since we just need one
                    sourceChain.setTargetInterceptor(targetChain.getHeadInterceptor());
                }
            }
            sourceChain.build();
        }

        if (target instanceof ReferenceContext) {
            attachInvoker(targetWire.getServiceName(), source.getInvocationChains().values(), (ReferenceContext) target);
        } else {
            attachInvoker(targetWire.getServiceName(), source.getInvocationChains().values(), (ComponentContext) target);
        }
    }

    private void attachInvoker(String serviceName, Collection<SourceInvocationChain> chains, ComponentContext<?> target) {
        for (SourceInvocationChain chain : chains) {
            TargetInvoker invoker = target.createTargetInvoker(serviceName, chain.getMethod());
            // TODO fix cacheable attrivute
            //invoker.setCacheable(cacheable);
            chain.setTargetInvoker(invoker);
        }
    }

    private void attachInvoker(String serviceName, Collection<SourceInvocationChain> chains, ReferenceContext<?> target) {
        for (SourceInvocationChain chain : chains) {
            TargetInvoker invoker = target.createTargetInvoker(serviceName, chain.getMethod());
            // TODO fix cacheable attribute
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
