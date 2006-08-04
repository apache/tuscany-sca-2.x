package org.apache.tuscany.core.implementation.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.wire.OutboundAutowire;

/**
 * A specialization of <code>OutboundAutowire</code> that returns a direct reference to the target
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemOutboundAutowire<T> implements OutboundAutowire<T>, SystemOutboundWire<T> {
    private String referenceName;
    private Class<T> businessInterface;
    private AutowireComponent<?> context;

    public SystemOutboundAutowire(String referenceName, Class<T> businessInterface, AutowireComponent<?> context) {
        this.referenceName = referenceName;
        this.businessInterface = businessInterface;
        this.context = context;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return null;
    }

    public void setTargetName(QualifiedName targetName) {
    }

    public T getTargetService() throws TargetException {
        return context.resolveInstance(businessInterface);
    }

    public Class<T> getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class<T> businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    @SuppressWarnings("unchecked")
    public void setCallbackInterface(Class<T> interfaze) {
        throw new UnsupportedOperationException();
    }

    public Class<T> getCallbackInterface() {
        throw new UnsupportedOperationException();
    }

    public void addCallbackInterface(Class<?> claz) {
        throw new UnsupportedOperationException();
    }

    public Class[] getImplementedCallbackInterfaces() {
        throw new UnsupportedOperationException();
    }

    public void setTargetWire(InboundWire<T> wire) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, OutboundInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, OutboundInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, InboundInvocationChain> getTargetCallbackInvocationChains() {
        throw new UnsupportedOperationException();
    }

    public void addTargetCallbackInvocationChains(Map<Method, InboundInvocationChain> chains) {
        throw new UnsupportedOperationException();
    }

    public void addTargetCallbackInvocationChain(Method method, InboundInvocationChain chain) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, OutboundInvocationChain> getSourceCallbackInvocationChains() {
        return null;
    }

    public void addSourceCallbackInvocationChains(Map<Method, OutboundInvocationChain> chains) {

    }

    public void addSourceCallbackInvocationChain(Method method, OutboundInvocationChain chain) {

    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        return true;
    }
}
