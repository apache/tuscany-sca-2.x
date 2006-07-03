package org.apache.tuscany.container.spring;

import junit.framework.TestCase;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.tuscany.container.spring.config.SCAService;

/**
 * Tests the SCA extensible schema elements for Spring's XML configuration files
 *
 * @version $$Rev: $$ $$Date: $$
 */

public class SpringConfigSchemaTestCase extends TestCase {

    private ConfigurableApplicationContext applicationContext;

    public void setUp() {
        applicationContext =
                new ClassPathXmlApplicationContext("org/apache/tuscany/container/spring/SpringConfigSchemaTest.xml");
    }

    public void testSCAService() {
        SCAService service = (SCAService) applicationContext.getBean("fooService");
        assertEquals("testBean", service.getTarget());
    }

    /*
    public void testSCAReference() {
        SCAReference ref = (SCAReference) applicationContext.getBean("fooReference");
        assertEquals("fooReference", ref.getName());
    }
    */
}
