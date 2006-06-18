package org.apache.tuscany.spi.model;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.injection.EventInvoker;
import org.apache.tuscany.spi.injection.Injector;

/**
 * A component type specialization for POJO implementations
 *
 * @version $$Rev$$ $$Date$$
 */
public class PojoComponentType<S extends ServiceDefinition, R extends ReferenceDefinition,  P extends Property<?>>
    extends ComponentType<S, R, P> {

    private Scope lifecycleScope = Scope.UNDEFINED;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private final List<Injector> injectors = new ArrayList<Injector>();
    private final Map<String, Member> members = new HashMap<String, Member>();

    public Scope getLifecycleScope() {
        return lifecycleScope;
    }

    public void setLifecycleScope(Scope lifecycleScope) {
        this.lifecycleScope = lifecycleScope;
    }

    public EventInvoker<Object> getInitInvoker() {
        return initInvoker;
    }

    public void setInitInvoker(EventInvoker<Object> initInvoker) {
        this.initInvoker = initInvoker;
    }

    public EventInvoker<Object> getDestroyInvoker() {
        return destroyInvoker;
    }

    public void setDestroyInvoker(EventInvoker<Object> destroyInvoker) {
        this.destroyInvoker = destroyInvoker;
    }

    public List<Injector> getInjectors() {
        return injectors;
    }

    public void addInjector(Injector injector) {
        injectors.add(injector);
    }

    public Member getReferenceMember(String name) {
        return members.get(name);
    }

    public void addReferenceMember(String name, Member member) {
        members.put(name, member);
    }

    public Map<String, Member> getReferenceMembers() {
        return members;
    }

}
