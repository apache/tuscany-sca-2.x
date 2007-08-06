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
package org.apache.tuscany.sca.implementation.java.introspect;

import java.lang.reflect.Member;

/**
 * Denotes a problem processing annotations on a POJO implementation
 * 
 * @version $Rev$ $Date$
 */
public class IntrospectionException extends Exception {
    private static final long serialVersionUID = -361025119035104470L;
    private Member member;

    public IntrospectionException() {
    }

    public IntrospectionException(String message) {
        super(message);
    }

    public IntrospectionException(String message, Member member) {
        super(message);
        this.member = member;
    }

    public IntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntrospectionException(Throwable cause) {
        super(cause);
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
