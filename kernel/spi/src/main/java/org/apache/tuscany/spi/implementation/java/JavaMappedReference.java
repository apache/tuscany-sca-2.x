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
package org.apache.tuscany.spi.implementation.java;

import java.lang.reflect.Member;
import java.net.URI;

import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * A ReferenceDefinition definition that is mapped to a specific location in the implementation class. This location
 * will typically be used to inject reference values.
 *
 * @version $Rev$ $Date$
 */
public class JavaMappedReference extends ReferenceDefinition {
    private Member member;

    public JavaMappedReference() {
    }

    public JavaMappedReference(URI name, ServiceContract serviceContract, Member member) {
        super(name, serviceContract);
        this.member = member;
    }

    /**
     * Returns the Member that this reference is mapped to.
     *
     * @return the Member that this reference is mapped to
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the Member that this reference is mapped to
     *
     * @param member the Member that this reference is mapped to
     */
    public void setMember(Member member) {
        this.member = member;
    }
}
