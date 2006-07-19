package org.apache.tuscany.core.implementation.java.mock.components;

import java.util.List;


/**
 * Implementations are used in wiring tests
 *
 * @version $Rev: 415107 $ $Date: 2006-06-18 01:37:39 -0700 (Sun, 18 Jun 2006) $
 */
public interface Source {

    Target getTarget();

    List<Target> getTargets();

    List<Target> getTargetsThroughField();

}
