package org.apache.tuscany.core.config.impl;

import org.osoa.sca.annotations.Service;

/**
 * @version $$Rev$$ $$Date$$
 */

@Service(interfaces = {TestLocalComponent.class})
public class TestMultipleInterfacesComponentImpl implements TestComponent, TestLocalComponent {


}
