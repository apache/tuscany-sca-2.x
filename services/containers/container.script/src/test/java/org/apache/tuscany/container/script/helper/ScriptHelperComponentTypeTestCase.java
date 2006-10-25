package org.apache.tuscany.container.script.helper;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperComponentType;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

public class ScriptHelperComponentTypeTestCase extends TestCase {
    
    public void testLifecycleScope() {
        ScriptHelperComponentType ct = new ScriptHelperComponentType();
        assertEquals(Scope.MODULE, ct.getLifecycleScope());
        ct.setLifecycleScope(Scope.COMPOSITE);
        assertEquals(Scope.COMPOSITE, ct.getLifecycleScope());
    }

    @SuppressWarnings("unchecked")
    public void testComponentTypeConstructor() {
        ComponentType ct = new ComponentType();
        Property property = new Property();
        ct.add(property);
        ReferenceDefinition reference = new ReferenceDefinition();
        ct.add(reference);
        ServiceDefinition service = new ServiceDefinition();
        ct.add(service);

        ScriptHelperComponentType pct = new ScriptHelperComponentType(ct);
        
        assertEquals(property, pct.getProperties().values().iterator().next());
        assertEquals(reference, pct.getReferences().values().iterator().next());
        assertEquals(service, pct.getServices().values().iterator().next());
    }
}
