package org.apache.tuscany.model.assembly.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.sdo.OverrideOptions;

import commonj.sdo.Sequence;

public class PojoExternalService implements ExternalService {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoExternalService() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        check();
        this.name = name;
    }

    private List<Binding> bindings = new ArrayList();

    private List<Binding> unModifiableBindings;
    
    public List<Binding> getBindings() {
        if (frozen) {
            if(unModifiableBindings == null){
            unModifiableBindings= Collections.unmodifiableList(bindings);
            }
            return unModifiableBindings;
        } else {
            return bindings;
        }
    }

    private OverrideOptions options;

    // FIXME This has SDO and EMF references
    public OverrideOptions getOverridable() {
        return options;
    }

    public void setOverridable(OverrideOptions options) {
        check();
        this.options = options;
    }

    private ConfiguredService service;

    public ConfiguredService getConfiguredService() {
        return service;
    }

    public void setConfiguredService(ConfiguredService service) {
        check();
        this.service = service;
    }

    private Aggregate aggregate;

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        check();
        this.aggregate = aggregate;
    }

    private Interface contract;

    public Interface getInterfaceContract() {
        return contract;
    }

    public void setInterfaceContract(Interface contract) {
        check();
        this.contract = contract;
    }

    // FIXME SDO reference
    public Sequence getAny() {
        throw new UnsupportedOperationException();
    }

    // FIXME SDO reference
    public Sequence getAnyAttribute() {
        throw new UnsupportedOperationException();
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        for (Binding binding : bindings) {
            if (!binding.accept(visitor)) {
                return false;
            }
        }
        if (service != null && !service.accept(visitor)) {
            return false;
        }
        if (aggregate != null && !aggregate.accept(visitor)) {
            return false;
        }
        if (contract != null && !contract.accept(visitor)) {
            return false;
        }
        return true;
    }

    private void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
