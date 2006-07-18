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

package org.apache.tuscany.databinding.xml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.apache.tuscany.databinding.DataBinding;

public class StAXBinding implements DataBinding {
    public static final String NAME = "StAX";

    public Result createResult(Class resultType) {
        // TODO Auto-generated method stub
        return null;
    }

    public Source createSource(Object source, Class sourceType) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSink() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return NAME;
    }

}
