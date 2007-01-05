package org.apache.tuscany.core.implementation.processor;

import org.osoa.sca.annotations.EagerInit;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class EagerInitProcessorTestCase extends TestCase {

    public void testNoLevel() throws ProcessingException {
        EagerInitProcessor processor = new EagerInitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, NoLevel.class, type, null);
        assertEquals(50, type.getInitLevel());
    }

    public void testLevel() throws ProcessingException {
        EagerInitProcessor processor = new EagerInitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, Level.class, type, null);
        assertEquals(1, type.getInitLevel());
    }

    public void testSubclass() throws ProcessingException {
        EagerInitProcessor processor = new EagerInitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitClass(null, SubClass.class, type, null);
        assertEquals(1, type.getInitLevel());
    }

    @EagerInit
    private class NoLevel {
    }

    @EagerInit(1)
    private class Level {
    }

    private class SubClass extends Level {

    }

}
