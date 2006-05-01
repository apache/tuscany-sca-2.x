/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.mock.component;

import org.apache.tuscany.core.system.annotation.Autowire;

import java.util.List;

/**
 * Mock system component implementation used in autowire tests
 * 
 * @version $Rev$ $Date$
 */
public class AutowireSourceImpl implements Source {

    @Autowire
    protected Target target;
    
    private List<Target> targets;

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

    private Target[] targetsArray;
    
    public Target[] getArrayOfTargets() {
        return targetsArray;
    }

    public void setArrayOfTargets(Target[] targets) {
        targetsArray = targets;
    }

}
