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

package org.apache.tuscany.sca.itest.interfaces;

import java.io.Serializable;

public class ParameterObject implements Serializable {
    private static final long serialVersionUID = 1L;
    public String field1;

    public ParameterObject() {
        this.field1 = null;
    }

    public ParameterObject(String field1) {
        this.field1 = field1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof ParameterObject) {
            ParameterObject other = (ParameterObject)o;
            if (field1 != null)
                return field1.equals(other.field1);
        }
        return false;
    }
}
