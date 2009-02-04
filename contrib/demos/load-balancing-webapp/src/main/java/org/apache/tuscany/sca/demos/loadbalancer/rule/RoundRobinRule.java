/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tuscany.sca.demos.loadbalancer.rule;

import javax.servlet.http.HttpServletRequest;

import org.apache.webapp.balancer.rules.BaseRule;


public class RoundRobinRule extends BaseRule {
    /**
     * The number of worker nodes that load will  
     * be balanced across
     */
    private int workerCount;
    
    private int currentCount = 1;


    /**
     * Sets the worker count.
     *
     * @param workerCount The worker count
     */
    public void setWorkerCount(int workerCount) {
        if (workerCount == 0) {
            throw new IllegalArgumentException(
                "worker count cannot be 0.");
        } else {
            this.workerCount = workerCount;
        }
    }

    /**
     * Returns the worker count.
     *
     * @return int The worker count
     */
    protected int getWorkerCount() {
        return workerCount;
    }



    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {

        if (currentCount == workerCount){
            currentCount = 1;
            return true;
        } else {
            currentCount++;
            return false;
        }
    }

    /**
     * Returns a String representation of this object.
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        buffer.append(getClass().getName());
        buffer.append(": ");

        buffer.append("Worker count: ");
        buffer.append(getWorkerCount());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}
