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
package org.apache.tuscany.sca.binding.ejb.java2idl;

import java.util.StringTokenizer;

/**
 * IDL Exception
 */
public class ExceptionType extends ValueType {

    private static WorkCache cache = new WorkCache(ExceptionType.class);

    private String repositoryId;

    public static ExceptionType getExceptionType(Class cls) {
        return (ExceptionType)cache.getType(cls);
    }

    protected ExceptionType(Class cls) {
        super(cls);
    }

    protected void parse() {
        super.parse();
        if (!Exception.class.isAssignableFrom(javaClass) || RuntimeException.class.isAssignableFrom(javaClass))
            throw new IDLViolationException("Exception type " + javaClass.getName() + " must be a checked exception.",
                                            "1.2.6");
        // calculate exceptionRepositoryId
        StringBuffer b = new StringBuffer("IDL:");
        String base = javaClass.getName();
        if (base.endsWith("Exception"))
            base = base.substring(0, base.length() - 9);
        StringTokenizer tokenizer = new StringTokenizer(base, ".");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                // Defect 281395
                b.append(IDLUtil.javaToIDLName(token));
                b.append('/');
            } else {
                b.append(IDLUtil.javaToIDLName(token + "Ex"));
            }
        }
        b.append(":1.0");
        repositoryId = b.toString();
    }

    /**
     * Return the repository ID for the mapping of this analysis to an
     * exception.
     */
    public String getExceptionRepositoryId() {
        return repositoryId;
    }

}
