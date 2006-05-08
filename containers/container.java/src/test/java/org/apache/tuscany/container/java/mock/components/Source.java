package org.apache.tuscany.container.java.mock.components;

import java.util.List;


/**
 * Implementations are used in wiring tests
 *
 * @version $Rev$ $Date$
 */
public interface Source {

    public Target getTarget();

    public List<Target> getTargets();

    public List<Target> getTargetsThroughField();

}
