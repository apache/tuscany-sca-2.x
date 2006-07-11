package org.apache.tuscany.core.implementation.java.mock.components;

import java.util.List;


/**
 * Mock system component implementation used in wiring tests
 *
 * @version $Rev: 415107 $ $Date: 2006-06-18 01:37:39 -0700 (Sun, 18 Jun 2006) $
 */
public class SourceImpl implements Source {

    private Target target;
    private List<Target> targets;
    private Target[] targetsArray;
    private List<Target> targetsThroughField;

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<Target> getTargetsThroughField() {
        return targetsThroughField;
    }


    public Target[] getArrayOfTargets() {
        return targetsArray;
    }

    public void setArrayOfTargets(Target[] targets) {
        targetsArray = targets;
    }


}
