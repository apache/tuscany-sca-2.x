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
package org.apache.tuscany.contribution.service;

/**
 * Denotes an invalid configuration artifact
 * 
 * @version $Rev: 525638 $ $Date: 2007-04-04 16:36:03 -0700 (Wed, 04 Apr 2007) $
 */
public class InvalidConfigurationException extends ContributionReadException {
    private static final long serialVersionUID = -4312958640212000366L;

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
