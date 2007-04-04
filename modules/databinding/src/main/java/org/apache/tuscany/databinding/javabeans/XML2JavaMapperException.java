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

package org.apache.tuscany.databinding.javabeans;

import javax.xml.namespace.QName;

/**
 * This exception is used to encapsulate and rethrow exceptions that arise out
 * of converting XML Data to Java Objects.
 */
public class XML2JavaMapperException extends RuntimeException {
    private static final long serialVersionUID = 6596530102591630642L;
    
    private QName xmlElementName;
    private String javaFieldName;
    private Class javaType;

    public XML2JavaMapperException(String message) {
        super(message);
    }

    public XML2JavaMapperException(Throwable cause) {
        super(cause);
    }

    public QName getXmlElementName() {
        return xmlElementName;
    }

    public void setXmlElementName(QName xmlElementName) {
        this.xmlElementName = xmlElementName;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public void setJavaFieldName(String javaFieldName) {
        this.javaFieldName = javaFieldName;
    }

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType) {
        this.javaType = javaType;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " <" + getJavaFieldName() + "> " + " in <" + getJavaType() + ">";
    }
    
    

}
