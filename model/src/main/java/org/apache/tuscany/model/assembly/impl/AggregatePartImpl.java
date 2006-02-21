/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.impl;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AggregatePart;

/**
 * Implementation of AggregatePart.
 */
public abstract class AggregatePartImpl extends ExtensibleImpl implements AggregatePart {
    private Aggregate aggregate;
    private String name;

    /**
     * Constructor
     */
    protected AggregatePartImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AggregatePart#getName()
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AggregatePart#setName(java.lang.String)
     */
    public void setName(String value) {
        checkNotFrozen();
        name=value;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AggregatePart#getAggregate()
     */
    public Aggregate getAggregate() {
        checkInitialized();
        return aggregate;
    }

    /**
     * Sets the aggregate containing this aggregate part.
     * @param aggregate
     */
    protected void setAggregate(Aggregate aggregate) {
        checkNotFrozen();
        this.aggregate=aggregate;
    }

}
