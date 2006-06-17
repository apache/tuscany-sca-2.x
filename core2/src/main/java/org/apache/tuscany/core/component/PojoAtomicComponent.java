package org.apache.tuscany.core.component;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.InvalidAccessorException;
import org.apache.tuscany.core.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.NoAccessorException;
import org.apache.tuscany.core.injection.WireObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Base implementation of an {@link org.apache.tuscany.spi.component.AtomicComponent} whose type is a Java class
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class PojoAtomicComponent<T> extends AtomicComponentExtension<T> {

    protected boolean eagerInit;
    protected EventInvoker<Object> initInvoker;
    protected EventInvoker<Object> destroyInvoker;
    protected ObjectFactory<?> objectFactory;
    protected List<Class<?>> serviceInterfaces;
    protected List<Injector> injectors;
    protected Map<String, Member> members;

    public PojoAtomicComponent(String name,
                               CompositeComponent<?> parent,
                               ScopeContainer scopeContainer,
                               Class<?> serviceInterface,
                               ObjectFactory<?> objectFactory,
                               boolean eagerInit,
                               EventInvoker<Object> initInvoker,
                               EventInvoker<Object> destroyInvoker,
                               List<Injector> injectors,
                               Map<String, Member> members,
                               WireService wireService) {
        super(name, parent, scopeContainer, wireService);
        assert objectFactory != null : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        serviceInterfaces.add(serviceInterface);
        this.serviceInterfaces = serviceInterfaces;
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.injectors = (injectors == null) ? new ArrayList<Injector>() : injectors;
        this.members = members != null ? members : new HashMap<String, Member>();

    }

    public PojoAtomicComponent(String name,
                               CompositeComponent<?> parent,
                               ScopeContainer scopeContainer,
                               List<Class<?>> serviceInterfaces,
                               ObjectFactory<?> objectFactory,
                               boolean eagerInit,
                               EventInvoker<Object> initInvoker,
                               EventInvoker<Object> destroyInvoker,
                               List<Injector> injectors,
                               Map<String, Member> members,
                               WireService wireService) {
        super(name, parent, scopeContainer, wireService);
        assert objectFactory != null : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.serviceInterfaces = serviceInterfaces;
        this.injectors = (injectors == null) ? new ArrayList<Injector>() : injectors;
        this.members = members != null ? members : new HashMap<String, Member>();
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void init(Object instance) throws TargetException {
        if (initInvoker != null) {
            initInvoker.invokeEvent(instance);
        }
    }

    public void destroy(Object instance) throws TargetException {
        if (destroyInvoker != null) {
            destroyInvoker.invokeEvent(instance);
        }
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContainer.getInstance(this);
    }

    public Object createInstance() throws ObjectCreationException {
        Object instance = objectFactory.getInstance();
        //InstanceWrapper ctx = new InstanceWrapperImpl(this, instance);
        // inject the instance with properties and references
        for (Injector<Object> injector : injectors) {
            injector.inject(instance);
        }
        return instance;
    }

    public void onReferenceWire(OutboundWire wire) {
        String referenceName = wire.getReferenceName();
        Member member = members.get(referenceName);
        if (member == null) {
            throw new NoAccessorException(referenceName);
        }
        injectors.add(createInjector(member, wire));
    }

    public void onReferenceWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        assert wires.size() > 0 : "Wires were empty";
        String referenceName = wires.get(0).getReferenceName();
        Member member = members.get(referenceName);
        if (member == null) {
            throw new NoAccessorException(referenceName);
        }
        injectors.add(createMultiplicityInjector(member, multiplicityClass, wires));
    }

    protected Injector createInjector(Member member, OutboundWire wire) {
        ObjectFactory<?> factory = new WireObjectFactory(wire, wireService);
        if (member instanceof Field) {
            return new FieldInjector((Field) member, factory);
        } else if (member instanceof Method) {
            return new MethodInjector((Method) member, factory);
        } else {
            InvalidAccessorException e = new InvalidAccessorException("Member must be a field or method");
            e.setIdentifier(member.getName());
            throw e;
        }
    }

    protected Injector createMultiplicityInjector(Member member,
                                                  Class<?> interfaceType,
                                                  List<OutboundWire> wireFactories) {
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (OutboundWire wire : wireFactories) {
            factories.add(new WireObjectFactory(wire, wireService));
        }
        if (member instanceof Field) {
            Field field = (Field) member;
            if (field.getType().isArray()) {
                return new FieldInjector(field, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new FieldInjector(field, new ListMultiplicityObjectFactory(factories));
            }
        } else if (member instanceof Method) {
            Method method = (Method) member;
            if (method.getParameterTypes()[0].isArray()) {
                return new MethodInjector(method, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new MethodInjector(method, new ListMultiplicityObjectFactory(factories));
            }
        } else {
            InvalidAccessorException e = new InvalidAccessorException("Member must be a field or method");
            e.setIdentifier(member.getName());
            throw e;
        }
    }


}
