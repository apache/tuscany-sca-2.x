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
package org.apache.tuscany.tools.wsdl2java.generate;

import java.io.File;

import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.codegen.writer.InterfaceWriter;

/**
 *
 */
public class RemotableInterfaceWritter extends InterfaceWriter {
    private static final String REMOTABLE_INTERFACE_TEMPLATE="/RemotableInterfaceTemplate.xsl";

    public RemotableInterfaceWritter(String outputFileLocation) {
        super(outputFileLocation);
    }

    public RemotableInterfaceWritter(File outputFileLocation, String language) {
        super(outputFileLocation, language);
    }

    /**
     * Loads the template.
     */
    @Override
    public void loadTemplate() throws CodeGenerationException {
        // the default behavior for the class writers is to use the property map from the languge specific types
        // The properties are arranged in the following order
        // <lang-name>.* .template=<write-class>,<template-name>

        //overrida original behaviour to always load the template we specified
        this.xsltStream = this.getClass().getResourceAsStream(REMOTABLE_INTERFACE_TEMPLATE);

    }
}
