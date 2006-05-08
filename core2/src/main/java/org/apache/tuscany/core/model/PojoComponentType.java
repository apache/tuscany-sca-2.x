package org.apache.tuscany.core.model;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.model.ComponentType;

/**
 * @version $$Rev$$ $$Date$$
 */
public class PojoComponentType extends ComponentType {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private final List<Injector> injectors = new ArrayList<Injector>();
    private final Map<String,Member> members = new HashMap<String,Member>();

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

    public Member getMember(String name) {
        return members.get(name);
    }

    public void setMember(Member member) {
        members.put(member.getName(),member);
    }

    public Map<String,Member> getMembers() {
        return members;
    }

}
