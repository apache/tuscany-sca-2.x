package org.apache.tuscany.container.spring;

import junit.framework.TestCase;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.tuscany.container.spring.mock.TestBean;

/**
 * Tests the SCA extensible schema elements for Spring's XML configuration files
 *
 * @version $$Rev$$ $$Date$$
 */

public class SpringConfigSchemaTestCase extends TestCase {

    private ConfigurableApplicationContext applicationContext;

    public void setUp() {
        applicationContext =
                new ClassPathXmlApplicationContext("org/apache/tuscany/container/spring/SpringConfigSchemaTest.xml");
    }

    public void testSCAService() {
        TestBean service = (TestBean) applicationContext.getBean("fooService");
        assertEquals("call me", service.echo("call me"));
    }

    /*
    public void testSCAReference() {
        SCAReference ref = (SCAReference) applicationContext.getBean("fooReference");
        assertEquals("fooReference", ref.getName());
    }
    */
}
