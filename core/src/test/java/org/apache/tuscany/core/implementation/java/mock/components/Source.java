package org.apache.tuscany.core.implementation.java.mock.components;

import java.util.List;


/**
 * Implementations are used in wiring tests
 *
 * @version $Rev$ $Date$
 */
public interface Source {

    Target getTarget();

    List<Target> getTargets();

    List<Target> getTargetsThroughField();

}
