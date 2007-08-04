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

import commonj.work.Work;
import commonj.work.WorkException;
import commonj.work.WorkItem;

/**
 * An identity based immutable implementation of the <code>WorkItem</code>
 * interface.
 *
 */
class WorkItemImpl implements WorkItem {
    
    // Id scoped for the VM
    private String id;

    // Status
    private int status = -1;

    // Result
    private Work result;
    
    // Original work
    private Work originalWork;

    // Exception
    private WorkException exception;

    /**
     * Instantiates an id for this item.
     *
     * @param id of this work event.
     */
    protected WorkItemImpl(final String id, final Work orginalWork) {
        this.id = id;
        this.originalWork = orginalWork;
    }

    /**
     * Returns the id.
     *
     * @return Id of this item.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the original work.
     *
     * @return Original work.
     */
    public Work getOriginalWork() {
        return originalWork;
    }

    /**
     * Returns the work result if the work completed.
     *
     * @return Work.
     * @throws WorkException If the work completed with an exception.
     */
    public Work getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result Result.
     */
    protected void setResult(final Work result) {
        this.result = result;
    }

    /**
     * Returns the exception if work completed with an exception.
     *
     * @return Work exception.
     */
    protected WorkException getException() {
        return exception;
    }

    /**
     * Sets the exception.
     *
     * @param exception Exception.
     */
    protected void setException(final WorkException exception) {
        this.exception = exception;
    }

    /**
     * Returns the work type based on whether the work was accepted, started,
     * rejected or completed.
     *
     * @return Work status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status Status.
     */
    protected void setStatus(final int status) {
        this.status = status;
    }

    /**
     * @see Object#hashCode() 
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj Object to be compared.
     * @return true if this object is the same as the obj argument; false
     *         otherwise..
     */
    public boolean equals(final Object obj) {
        return (obj != null) && (obj.getClass() == WorkItemImpl.class) && ((WorkItemImpl) obj).id.equals(id);
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o Object to be compared.
     * @return A negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException needs better documentation.
     */
    public int compareTo(final Object o) {
        if (o.getClass() != WorkItemImpl.class) {
            throw new ClassCastException(o.getClass().getName());
        } else {
            return ((WorkItemImpl) o).getId().compareTo(getId());
        }
    }
}
