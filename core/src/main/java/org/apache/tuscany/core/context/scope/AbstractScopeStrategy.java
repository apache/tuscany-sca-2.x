/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context.scope;

import static org.apache.tuscany.core.context.ContextConstants.UNDEFINED_SCOPE;

import org.apache.tuscany.core.context.ScopeStrategy;

/**
 * Implements basic scope strategy functionality
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeStrategy implements ScopeStrategy {

    public AbstractScopeStrategy() {
    }

    /**
     * Determines legal scope references according to standard SCA scope rules
     * 
     * @param pReferrer the scope of the component making the reference
     * @param pReferee the scope of the component being referred to
     */
    public boolean downScopeReference(int pReferrer, int pReferee) {
        if (pReferrer == UNDEFINED_SCOPE || pReferee == UNDEFINED_SCOPE) {
            return false;
        } else if ((pReferrer < 0) || (pReferee < 0)) {
            return false;
        }
        return (pReferrer > pReferee);
    }

}
