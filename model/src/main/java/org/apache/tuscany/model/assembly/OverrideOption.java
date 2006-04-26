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
package org.apache.tuscany.model.assembly;

/**
 * Enumeration for override options that are used to control whether configuration information
 * can be overridden by larger grained definitions.
 */
public enum OverrideOption {
    /**
     * Indicates that the supplied configuration cannot be overridden.
     */
    NO,

    /**
     * Indicates that the supplied configuration may be overriden.
     */
    MAY,

    /**
     * Indicates that the supplied configuration must be overriden.
     */
    MUST

}
