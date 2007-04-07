package org.apache.tuscany.core.wire.jdk;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReactivationException;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.TargetInvocationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.AbstractInvocationHandler;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.core.wire.NoMethodForOperationException;
import org.apache.tuscany.core.wire.WireUtils;

/**
 * Dispatches to a target through a wire.
 *
 * @version $Rev$ $Date$
 */
public final class JDKInvocationHandler2 extends AbstractInvocationHandler
    implements InvocationHandler, Externalizable, SCAExternalizable {
    private static final long serialVersionUID = -6155278451964527325L;

    // the wire this handler fronts
    private transient Wire wire;
    // the name of the source reference the wire is attached to, used during deserialization
    private String referenceName;
    // the interface the reference proxy implements
    private Class<?> proxyInterface;
    private transient WorkContext workContext;
    //  if the associated wire has a callback
    private transient boolean callback;
    // if the associated wire is conversational
    private boolean conversational;
    private transient Map<Method, InvocationChain> chains;

    /**
     * Constructor used for deserialization only
     */
    public JDKInvocationHandler2() {
    }

    public JDKInvocationHandler2(Class<?> interfaze, boolean conversational, Wire wire, WorkContext workContext)
        throws NoMethodForOperationException {
        super(conversational);
        this.workContext = workContext;
        this.proxyInterface = interfaze;
        this.wire = wire;
        this.conversational = conversational;
        init(interfaze, wire, null);
    }

    public JDKInvocationHandler2(Class<?> interfaze,
                                 Wire wire,
                                 Map<Method, InvocationChain> mapping,
                                 WorkContext workContext)
        throws NoMethodForOperationException {
        this.workContext = workContext;
        this.proxyInterface = interfaze;
        init(interfaze, wire, mapping);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationChain chain = chains.get(method);
        if (chain == null) {
            if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
                return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
            } else if (method.getDeclaringClass().equals(Object.class)
                && "equals".equals(method.getName())) {
                // TODO implement
                throw new UnsupportedOperationException();
            } else if (Object.class.equals(method.getDeclaringClass())
                && "hashCode".equals(method.getName())) {
                return hashCode();
                // TODO beter hash algorithm
            }
            throw new TargetInvocationException("Operation not configured", method.getName());
        }

        if (conversational) {
            Object id = workContext.getIdentifier(Scope.CONVERSATION);
            if (id == null) {
                String convIdFromThread = createConversationID();
                workContext.setIdentifier(Scope.CONVERSATION, convIdFromThread);
            }
        }
        LinkedList<Wire> list = null;
        if (callback) {
            // set up callback address
            list = workContext.getCallbackWires();
            if (list == null) {
                list = new LinkedList<Wire>();
                workContext.setCallbackWires(list);
            }
            list.add(wire);
        }
        // send the invocation down the wire
        Object result = invokeTarget(chain, args, null, list);

        if (callback) {
            list = workContext.getCallbackWires();
            if (list != null) {
                // pop last address
                list.removeLast();
            }
        }
        return result;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    public void setWorkContext(WorkContext context) {
        workContext = context;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(referenceName);
        out.writeObject(proxyInterface);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        referenceName = (String) in.readObject();
        proxyInterface = (Class<?>) in.readObject();
    }

    public void reactivate() throws ReactivationException {
        AtomicComponent owner = workContext.getCurrentAtomicComponent();
        if (owner == null) {
            throw new ReactivationException("Current atomic component not set on work context");
        }
        List<Wire> wires = owner.getWires(referenceName);
        if (wires == null) {
            throw new ReactivationException("Reference wire not found", referenceName, owner.getUri().toString());
        }
        // TODO handle multiplicity
        Wire wire = wires.get(0);
        try {
            init(proxyInterface, wire, null);
        } catch (NoMethodForOperationException e) {
            throw new ReactivationException(e);
        }
    }

    /**
     * Reinitializes the proxy handler
     *
     * @param interfaze the interface the proxy implements
     * @param wire      the wire fronted by the proxy
     * @param mapping   a mapping from proxy interface methods to invocation chain holders
     * @throws org.apache.tuscany.core.wire.NoMethodForOperationException
     *
     */
    private void init(Class<?> interfaze, Wire wire, Map<Method, InvocationChain> mapping)
        throws NoMethodForOperationException {
        this.referenceName = wire.getSourceUri().getFragment();
        this.callback = !wire.getPhysicalInvocationChains().isEmpty();
        if (mapping == null) {
            chains = WireUtils.createInterfaceToWireMapping2(interfaze, wire);
        } else {
            chains = mapping;
        }
    }

    /**
     * Creates a new conversational id
     *
     * @return the conversational id
     */
    private String createConversationID() {
        return UUID.randomUUID().toString();
    }

}
