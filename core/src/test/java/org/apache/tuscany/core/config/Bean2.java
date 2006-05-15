/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.config;

import junit.framework.AssertionFailedError;

import java.util.List;

public class Bean2 {

    private List methodList;

    public List getMethodList() {
        return methodList;
    }

    public void setMethodList(List list) {
        methodList = list;
    }

    private List fieldList;

    public List getfieldList() {
        return fieldList;
    }

    public void setfieldList(List list) {
       throw new AssertionFailedError("setter inadvertantly called");
    }

     
}
