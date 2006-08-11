/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.launcher;

/**
 * Exception indicating that application's main method is missing or invalid
 * (for example it may not be static).
 *  
 * @version $Rev: 411440 $ $Date: 2006-06-03 10:40:55 -0400 (Sat, 03 Jun 2006) $
 */
public class InvalidMainException extends LaunchException {
    public InvalidMainException(String message) {
        super(message);
    }
}
