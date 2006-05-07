package org.apache.tuscany.core.model;

import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;

/**
 * @version $$Rev$$ $$Date$$
 */
public class PojoComponentType extends ComponentType {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private List<Injector> injectors = new ArrayList<Injector>();

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

}
