package org.apache.tuscany.core.config.impl;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
@Service(TestNonServiceInterface.class)
public class TestNonServiceSpecifiedImpl implements TestNonServiceInterface, TestNonServiceInterface2{
}
