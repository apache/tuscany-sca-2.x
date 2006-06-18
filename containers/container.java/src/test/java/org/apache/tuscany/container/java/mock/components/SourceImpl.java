package org.apache.tuscany.container.java.mock.components;

import java.util.List;


/**
 * Mock system component implementation used in wiring tests
 *
 * @version $Rev$ $Date$
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
