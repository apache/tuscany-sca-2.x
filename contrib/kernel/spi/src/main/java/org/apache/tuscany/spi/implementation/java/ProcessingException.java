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

import org.apache.tuscany.spi.loader.LoaderException;

/**
 * Denotes a problem processing annotations on a POJO implementation
 *
 * @version $Rev$ $Date$
 */
public class ProcessingException extends LoaderException {
    private Member member;

    public ProcessingException() {
    }

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, String identifier) {
        super(message, identifier);
    }

    public ProcessingException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }


    public Member getMemberName() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
