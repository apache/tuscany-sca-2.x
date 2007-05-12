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
package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Visitable;
import org.apache.tuscany.sca.assembly.Visitor;

/**
 * Convenience base class for assembly model objects.
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseImpl implements Base, Visitable {
    private List<Object> extensions = new ArrayList<Object>();
    private boolean unresolved;

    /**
     * Constructs a new base model object.
     */
    protected BaseImpl() {
    }
    
    public List<Object> getExtensions() {
        return extensions;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    public boolean accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
