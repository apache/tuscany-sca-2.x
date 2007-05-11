/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.core.work;


import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;

/**
 * Default immutable implementation of the <code>WorkEvent</code> class.
 */
class DefaultWorkEvent implements WorkEvent {
    
    // Work item for this event
    private WorkItem workItem;

    // Exception if something has gone wrong
    private WorkException exception;

    /**
     * Instantiates the event.
     *
     * @param workItem Work item for this event.
     */
    public DefaultWorkEvent(final DefaultWorkItem workItem) {
        this.workItem = workItem;
        this.exception = workItem.getException();
    }

    /**
     * Returns the work type based on whether the work was accepted, started,
     * rejected or completed.
     *
     * @return Work type.
     */
    public int getType() {
        return workItem.getStatus();
    }

    /**
     * Returns the work item associated with this work type.
     *
     * @return Work item.
     */
    public WorkItem getWorkItem() {
        return workItem;
    }

    /**
     * Returns the exception if the work completed with an exception.
     *
     * @return Work exception.
     */
    public WorkException getException() {
        return exception;
    }
}
