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
package org.apache.tuscany.store;


/**
 * A generic monintor interface for services to log events
 * 
 * @version $Rev$ $Date$
 */
public interface StoreMonitor {

    /**
     * Signals the service has started
     * 
     * @param msg
     */
    void start(String msg);

    /**
     * Signals the service has been shutdown
     * 
     * @param msg
     */
    void stop(String msg);

    /**
     * Fired when recovery is started
     */

    void beginRecover();

    /**
     * Fired when recovery is completed
     */

    void endRecover();

    /**
     * Fired when a record is processed during recovery
     * 
     * @param recordId the id of the record being recovered
     */

    void recover(Object recordId);

    /**
     * Signals an error event
     * 
     * @param e the error
     */

    void error(Throwable e);

}
