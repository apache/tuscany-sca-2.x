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
package org.apache.tuscany.spi.component;

import java.util.List;

/**
 * @version $Rev$ $Date$
 */
public class GroupInitializationException extends TargetResolutionException {
    private final List<Exception> causes;

    /**
     * Exception indicating a problem initializing a group of components.
     *
     * @param contextId an identified for the context being initialized
     * @param causes the individual exceptions that occurred
     */
    public GroupInitializationException(String contextId, List<Exception> causes) {
        super(contextId);
        this.causes = causes;
    }

    /**
     * Return the exceptions that occurred as the group was initialized.
     *
     * @return a list of exceptions that occurred
     */
    public List<Exception> getCauses() {
        return causes;
    }
}
