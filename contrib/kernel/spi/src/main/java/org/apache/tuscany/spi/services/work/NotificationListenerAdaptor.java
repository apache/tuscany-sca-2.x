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
package org.apache.tuscany.spi.services.work;

/**
 * Adaptor class for the notification listsner.
 * 
 * @version $Revision$ $Date$
 *
 */
public class NotificationListenerAdaptor<T extends Runnable> implements NotificationListener<T> {

    /**
     * Callback method when the unit of work is accepted.
     * 
     * @param work Work that was accepted.
     */
    public void workAccepted(T work) {
    }
    
    /**
     * Callback method when the unit of work is successfully completed.
     * 
     * @param work Work that was succesfully completed.
     */
    public void workCompleted(T work) {
    }
    
    /**
     * Callback when the unit of work is started.
     * 
     * @param work Unit of work that was started.
     */
    public void workStarted(T work) {
    }
    
    /**
     * Callback when the unit of work is rejected.
     * 
     * @param work Unit of work that was rejected.
     */
    public void workRejected(T work) {
    }
    
    /**
     * Callnack when the unit of work fails to complete.
     * 
     * @param work Unit of work that failed to complete.
     * @param error Error that caused the unit of work to fail.
     */
    public void workFailed(T work, Throwable error) {
        error.printStackTrace();
    }

}
