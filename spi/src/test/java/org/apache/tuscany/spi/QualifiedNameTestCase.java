package org.apache.tuscany.spi;

import junit.framework.TestCase;

/**
 * Tests parsing of naming patters
 *
 * @version $Rev$ $Date$
 */
public class QualifiedNameTestCase extends TestCase {

    public void testSimpleName() throws Exception {
        QualifiedName name = new QualifiedName("Foo");
        assertEquals("Foo", name.getPartName());
        assertEquals(null, name.getPortName());
    }

    public void testCompoundName() throws Exception {
        QualifiedName name = new QualifiedName("Foo/Bar");
        assertEquals("Foo", name.getPartName());
        assertEquals("Bar", name.getPortName());
    }

    public void testCompoundMultiName() throws Exception {
        QualifiedName name = new QualifiedName("Foo/Bar/Baz");
        assertEquals("Foo", name.getPartName());
        assertEquals("Bar/Baz", name.getPortName());
    }

    public void testInvalidName() throws Exception {
        try {
            QualifiedName name = new QualifiedName("/Foo/Bar");
            fail("Invalid name exception not thrown");
        } catch (InvalidNameException e) {

        }
    }

}
