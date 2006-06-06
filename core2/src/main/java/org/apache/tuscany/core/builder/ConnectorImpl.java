package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.wire.BridgingInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.MessageDispatcher;
import org.apache.tuscany.core.wire.OutboundAutowire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {

    public <T> void connect(SCAObject<T> source) {
        CompositeComponent parent = source.getParent();
        Scope scope = source.getScope();
        if (source instanceof AtomicComponent) {
            AtomicComponent<T> sourceComponent = (AtomicComponent<T>) source;
            for (List<OutboundWire> referenceWires : sourceComponent.getOutboundWires().values()) {
                for (OutboundWire<T> outboundWire : referenceWires) {
                    if (outboundWire instanceof OutboundAutowire) {
                        continue;
                    }
                    try {
                        connect(outboundWire, parent, scope);
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
    public <T> void connect(OutboundWire<T> sourceWire, CompositeComponent<?> parent, Scope sourceScope) throws BuilderConfigException {
        assert(sourceScope != null): "Source scope was null";
        assert(sourceWire.getTargetName() != null): "WireDefinition target name was null";
        QualifiedName targetName = sourceWire.getTargetName();
        SCAObject<?> target = parent.getChild(targetName.getPartName());
        if (target == null) {
            BuilderConfigException e = new BuilderConfigException("Target not found for reference" + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }

        if (target instanceof AtomicComponent) {
            AtomicComponent<?> targetComponent = (AtomicComponent<?>) target;
            InboundWire<T> targetWire = targetComponent.getInboundWire(targetName.getPortName());
            if (targetWire == null) {
                BuilderConfigException e = new BuilderConfigException("Target service not found for reference " + sourceWire.getReferenceName());
                e.setIdentifier(targetName.getPortName());
                throw e;
            }
            if (!sourceWire.getBusinessInterface().isAssignableFrom(targetWire.getBusinessInterface())) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(sourceWire, targetWire, target, isOptimizable(sourceScope, target.getScope()));
        } else if (target instanceof Reference) {
            InboundWire<T> targetWire = ((Reference) target).getInboundWire();
            assert(targetWire != null);
            if (!sourceWire.getBusinessInterface().isAssignableFrom(targetWire.getBusinessInterface())) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            connect(sourceWire, targetWire, target, isOptimizable(sourceScope, target.getScope()));
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid wire target type for reference " + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
        }
    }

    public <T> void connect(InboundWire<T> sourceWire, OutboundWire<T> targetWire, boolean optimizable) throws BuilderConfigException {
        Map<Method, OutboundInvocationChain> targetChains = targetWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && sourceWire.getInvocationChains().isEmpty() && targetChains.isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            return;
        }
        //String serviceName = targetWire.getTargetName();
        for (InboundInvocationChain inboundChain : sourceWire.getInvocationChains().values()) {
            // match wire chains
            OutboundInvocationChain outboundChain = targetChains.get(inboundChain.getMethod());
            if (outboundChain == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible source and target wire interfaces");
                e.setIdentifier(sourceWire.getServiceName());
                throw e;
            }
            connect(inboundChain, outboundChain);
        }
    }

    public <T> void connect(OutboundWire<T> sourceWire, InboundWire<T> targetWire, SCAObject<?> context, boolean optimizable) {
        Map<Method, InboundInvocationChain> targetChains = targetWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && sourceWire.getInvocationChains().isEmpty() && targetChains.isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            return;
        }
        for (OutboundInvocationChain outboundChain : sourceWire.getInvocationChains().values()) {
            // match wire chains
            InboundInvocationChain inboundChain = targetChains.get(outboundChain.getMethod());
            if (inboundChain == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible sourceWire and target chain interfaces for reference");
                e.setIdentifier(sourceWire.getReferenceName());
                throw e;
            }
            if (context instanceof Component) {
                connect(outboundChain, inboundChain, ((Component) context).createTargetInvoker(targetWire.getServiceName(), inboundChain.getMethod()));
            } else if (context instanceof Reference) {
                connect(outboundChain, inboundChain, ((Reference) context).createTargetInvoker(targetWire.getServiceName(), inboundChain.getMethod()));
            }
        }
    }

    public void connect(OutboundInvocationChain sourceChain, InboundInvocationChain targetChain, TargetInvoker invoker) {
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
                    BuilderConfigException e = new BuilderConfigException("Service chain must have an interceptor");
                    e.setIdentifier(targetChain.getMethod().getName());
                    throw e;
                }
            } else {
                sourceChain.setTargetRequestChannel(new MessageChannelImpl(targetChain.getRequestHandlers()));
            }
            sourceChain.setTargetResponseChannel(new MessageChannelImpl(targetChain.getResponseHandlers()));
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
        sourceChain.build(); //FIXME build should be moved out
        sourceChain.setTargetInvoker(invoker);
    }


    public void connect(InboundInvocationChain sourceChain, OutboundInvocationChain targetChain) {
        sourceChain.addInterceptor(new BridgingInterceptor(targetChain.getHeadInterceptor()));
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
