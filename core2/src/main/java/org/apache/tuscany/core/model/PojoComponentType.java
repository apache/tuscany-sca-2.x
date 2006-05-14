package org.apache.tuscany.core.model;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class PojoComponentType extends ComponentType {

    private Scope lifecycleScope = Scope.UNDEFINED;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private final List<Injector> injectors = new ArrayList<Injector>();
    private final Map<String,Member> members = new HashMap<String,Member>();

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

    public void setReferenceMember(String name, Member member) {
        members.put(name,member);
    }

    public Map<String,Member> getReferenceMembers() {
        return members;
    }

}
