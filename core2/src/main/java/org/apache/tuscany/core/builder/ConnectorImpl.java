package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.wire.BridgingHandler;
import org.apache.tuscany.core.wire.BridgingInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.MessageDispatcher;
import org.apache.tuscany.core.wire.RequestResponseInterceptor;
import org.apache.tuscany.core.wire.ReferenceAutowire;
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
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.ServiceWire;

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
            for (List<ReferenceWire> referenceWires : sourceContext.getReferenceWires().values()) {
                for (ReferenceWire<T> referenceWire : referenceWires) {
                    if (referenceWire instanceof ReferenceAutowire) {
                        continue;
                    }
                    try {
                        connect(referenceWire, parent, scope);
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
    private <T> void connect(ReferenceWire<T> referenceWire, CompositeContext<?> parent, Scope sourceScope) throws BuilderConfigException {
        assert(sourceScope != null): "Source scope was null";
        assert(referenceWire.getTargetName() != null): "Wire target name was null";
        QualifiedName targetName = referenceWire.getTargetName();
        Context<?> target = parent.getContext(targetName.getPartName());
        if (target == null) {
            BuilderConfigException e = new BuilderConfigException("Target not found for reference" + referenceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }
        ServiceWire<?> serviceWire;
        if (target instanceof AtomicContext) {
            AtomicContext<?> targetContext = (AtomicContext<?>) target;
            serviceWire = targetContext.getServiceWire(targetName.getPortName());
            if (serviceWire == null) {
                BuilderConfigException e = new BuilderConfigException("Target service not found for reference " + referenceWire.getReferenceName());
                e.setIdentifier(targetName.getPortName());
                throw e;
            }
            if (!referenceWire.getBusinessInterface().isAssignableFrom(serviceWire.getBusinessInterface())) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(referenceWire, (ServiceWire<T>) serviceWire, target, isOptimizable(sourceScope, targetContext.getScope()));
        } else if (target instanceof ReferenceContext) {
            serviceWire = ((ReferenceContext) target).getTargetWire();
            assert(serviceWire != null);
            if (!referenceWire.getBusinessInterface().isAssignableFrom(serviceWire.getBusinessInterface())) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(referenceWire, (ServiceWire<T>) serviceWire, target, isOptimizable(sourceScope, target.getScope()));
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid wire target type for reference " + referenceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void connect(ServiceWire<T> sourceWire, Context<?> targetContext) throws BuilderConfigException {
        ServiceWire<T> targetWire;
        if (targetContext instanceof ComponentContext) {
            targetWire = ((ComponentContext) targetContext).getServiceWire(sourceWire.getServiceName());
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
        for (ServiceInvocationChain sourceChain : sourceWire.getInvocationChains().values()) {
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
            ServiceInvocationChain serviceChain = targetWire.getInvocationChains().get(sourceChain.getMethod());
            connect(sourceChain, serviceChain);
        }
    }

    /**
     * Public access set for unit testing
     */
    public <T> void connect(ReferenceWire<T> source, ServiceWire<T> serviceWire, Context<?> target, boolean optimizable) {
        Map<Method, ServiceInvocationChain> targetInvocationConfigs = serviceWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && source.getInvocationChains().isEmpty() && targetInvocationConfigs.isEmpty()) {
            source.setTargetWire(serviceWire);
            return;
        }
        String serviceName = serviceWire.getServiceName();
        for (ReferenceInvocationChain referenceChain : source.getInvocationChains().values()) {
            // match wire chains
            ServiceInvocationChain serviceChain = targetInvocationConfigs.get(referenceChain.getMethod());
            if (serviceChain == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible source and target chain interfaces for reference");
                e.setIdentifier(source.getReferenceName());
                throw e;
            }
            if (target instanceof ReferenceContext) {
                ReferenceContext referenceContext = (ReferenceContext) target;
                TargetInvoker invoker = referenceContext.createTargetInvoker(serviceName, referenceChain.getMethod());
                connect(referenceChain, serviceChain, invoker);
            } else {
                ComponentContext componentContext = (ComponentContext) target;
                TargetInvoker invoker = componentContext.createTargetInvoker(serviceName, referenceChain.getMethod());
                connect(referenceChain, serviceChain, invoker);
            }
        }
    }

    public void connect(ReferenceInvocationChain referenceChain, ServiceInvocationChain serviceChain, TargetInvoker invoker) {
        // if handlers are configured, add them
        if (serviceChain.getRequestHandlers() != null || serviceChain.getResponseHandlers() != null) {
            if (serviceChain.getRequestHandlers() == null) {
                // the target may not have request handlers, so bridge it on the source
                if (serviceChain.getHeadInterceptor() != null) {
                    List<MessageHandler> handlers = new ArrayList<MessageHandler>();
                    handlers.add(new MessageDispatcher(serviceChain.getHeadInterceptor()));
                    MessageChannel channel = new MessageChannelImpl(handlers);
                    referenceChain.setTargetRequestChannel(channel);
                } else {
                    BuilderConfigException e = new BuilderConfigException("Service chain must have an interceptor");
                    e.setIdentifier(serviceChain.getMethod().getName());
                    throw e;
                }
            } else {
                referenceChain.setTargetRequestChannel(new MessageChannelImpl(serviceChain.getRequestHandlers()));
            }
            referenceChain.setTargetResponseChannel(new MessageChannelImpl(serviceChain.getResponseHandlers()));
        } else {
            // no handlers, just connect interceptors
            if (serviceChain.getHeadInterceptor() == null) {
                BuilderConfigException e = new BuilderConfigException("No chain handler or interceptor for operation");
                e.setIdentifier(serviceChain.getMethod().getName());
                throw e;
            }
            if (!(referenceChain.getTailInterceptor() instanceof InvokerInterceptor && serviceChain
                    .getHeadInterceptor() instanceof InvokerInterceptor)) {
                // check that we do not have the case where the only interceptors are invokers since we just need one
                referenceChain.setTargetInterceptor(serviceChain.getHeadInterceptor());
            }
        }
        referenceChain.build(); //FIXME build should be moved out
        referenceChain.setTargetInvoker(invoker);
    }

    public void connect(ServiceInvocationChain sourceChain, ServiceInvocationChain serviceChain) {
        if (sourceChain.getTailInterceptor() != null) {
            // connect the tail interceptor to a target request channel or target interceptor
            if (serviceChain.getRequestHandlers() != null) {
                MessageChannel targetRequestChannel = serviceChain.getRequestChannel();
                MessageChannel targetResponseChannel = serviceChain.getResponseChannel();
                sourceChain.setTargetRequestChannel(targetRequestChannel);
                sourceChain.setTargetResponseChannel(targetResponseChannel);
                sourceChain.getTailInterceptor().setNext(new RequestResponseInterceptor(null, targetRequestChannel,
                        null, targetResponseChannel));
            } else {
                // connect to target interceptor
                if (serviceChain.getHeadInterceptor() == null) {
                    BuilderConfigException e = new BuilderConfigException("Service chain must have an interceptor");
                    e.setIdentifier(serviceChain.getMethod().getName());
                    throw e;
                }
                sourceChain.getTailInterceptor().setNext(new BridgingInterceptor(serviceChain.getHeadInterceptor()));
            }
        } else {
            // no source interceptor
            List<MessageHandler> sourceRequestHandlers = sourceChain.getRequestHandlers();
            List<MessageHandler> targetRequestHandlers = serviceChain.getRequestHandlers();
            List<MessageHandler> targetResponseHandlers = serviceChain.getResponseHandlers();
            List<MessageHandler> sourceResponseHandlers = sourceChain.getResponseHandlers();
            if (sourceRequestHandlers != null && !sourceRequestHandlers.isEmpty()) {
                // connect source and target side request handlers
                if (targetRequestHandlers != null && !targetRequestHandlers.isEmpty()) {
                    sourceRequestHandlers.add(new BridgingHandler(targetRequestHandlers.get(0)));
                } else {
                    sourceRequestHandlers.add(new MessageDispatcher(serviceChain.getHeadInterceptor()));
                }
            } else {
                // no source interceptor or request handlers
                MessageChannel targetRequestChannel = serviceChain.getRequestChannel();
                MessageChannel targetResponseChannel = serviceChain.getResponseChannel();
                if ((targetRequestHandlers == null || targetRequestHandlers.isEmpty())
                        && (targetResponseHandlers == null || targetResponseHandlers.isEmpty())){
                    // no handlers on target, connect directly to head target interceptor (there must be at least one)
                    if (serviceChain.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("Service chain must have an interceptor");
                        e.setIdentifier(serviceChain.getMethod().getName());
                        throw e;
                    }
                    sourceChain.setTargetInterceptor(serviceChain.getHeadInterceptor());
                } else {
                    if (targetRequestChannel == null) {
                        sourceChain.setTargetInterceptor(new BridgingInterceptor(serviceChain.getHeadInterceptor()));

                    } else {
                        sourceChain.setTargetRequestChannel(targetRequestChannel);
                        sourceChain.addInterceptor(new RequestResponseInterceptor(null, targetRequestChannel, null, targetResponseChannel));
                    }
                    sourceChain.setTargetResponseChannel(targetResponseChannel);
                }
            }
            // bridge response handlers
            if (sourceResponseHandlers != null && !sourceResponseHandlers.isEmpty()) {
                if (targetResponseHandlers != null && !targetResponseHandlers.isEmpty()) {
                    sourceResponseHandlers.add(new BridgingHandler(targetResponseHandlers.get(0)));
                }
            }
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
