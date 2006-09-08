package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.spi.component.Service;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.easymock.classextension.EasyMock;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * @version $Rev$ $Date$
 */
public class SpringCompositeComponentTestCase extends TestCase {

    public void testAppContextStart() {
        AbstractApplicationContext appContext = EasyMock.createMock(AbstractApplicationContext.class);
        appContext.refresh();
        appContext.setParent(EasyMock.isA(ApplicationContext.class));
        appContext.start();
        replay(appContext);
        SpringCompositeComponent<?> component = new SpringCompositeComponent("spring", appContext, null, null, null);
        component.start();
        verify(appContext);
    }

    public void testChildStart() {
        AbstractApplicationContext appContext = EasyMock.createNiceMock(AbstractApplicationContext.class);
        replay(appContext);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("foo").anyTimes();
        service.start();
        replay(service);
        SpringCompositeComponent<?> component = new SpringCompositeComponent("spring", appContext, null, null, null);
        component.register(service);
        component.start();
        verify(service);
    }


}
