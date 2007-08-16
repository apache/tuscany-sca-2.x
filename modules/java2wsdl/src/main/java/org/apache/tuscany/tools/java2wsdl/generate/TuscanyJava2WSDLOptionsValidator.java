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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.util.ArrayList;

import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;
import org.apache.ws.java2wsdl.utils.Java2WSDLOptionsValidator;

/**
 * This class is an extension from the Axis2 implementation in order to handle
 * additional optoins specific to Tuscany. This class can be done away with once
 * Axis2 is also enhanced to support these additional options.
 */
public class TuscanyJava2WSDLOptionsValidator extends Java2WSDLOptionsValidator implements TuscanyJava2WSDLConstants {
    public boolean isInvalid(Java2WSDLCommandLineOption option) {
        boolean invalid;
        String optionType = option.getOptionType();

        invalid =
            !((IMPORT_XSD_OPTION).equalsIgnoreCase(optionType) || (IMPORT_XSD_OPTION_LONG).equalsIgnoreCase(optionType)
                || (TuscanyJava2WSDLConstants.EXTRA_CLASSES_DEFAULT_OPTION_LONG).equalsIgnoreCase(optionType)
                || (TuscanyJava2WSDLConstants.EXTRA_CLASSES_DEFAULT_OPTION).equalsIgnoreCase(optionType)
                || (TuscanyJava2WSDLConstants.FACTORY_CLASSNAMES_OPTION_LONG).equalsIgnoreCase(optionType)
                || (TuscanyJava2WSDLConstants.FACTORY_CLASSNAMES_OPTION).equalsIgnoreCase(optionType) || !super
                .isInvalid(option));

        invalid = validateImportXSDOption(invalid, option);

        return invalid;
    }

    private boolean validateImportXSDOption(boolean invalid, Java2WSDLCommandLineOption option) {
        String optionType = option.getOptionType();
        String schemaNSLocationPair = null;

        if (!invalid && (IMPORT_XSD_OPTION).equalsIgnoreCase(optionType)
            || (IMPORT_XSD_OPTION_LONG).equalsIgnoreCase(optionType)) {
            ArrayList optionValues = option.getOptionValues();

            for (int count = 0; count < optionValues.size(); ++count) {
                schemaNSLocationPair = ((String)optionValues.get(count)).trim();
                if ((schemaNSLocationPair.charAt(0) != OPEN_BRACKET) || (schemaNSLocationPair
                    .charAt(schemaNSLocationPair.length() - 1) != CLOSE_BRACKET)
                    || (schemaNSLocationPair.indexOf(COMMA) == -1))

                {
                    System.out.println("Schema Namespace-Location pair option not specified properly!!");
                    invalid = true;
                }
            }
        }

        return invalid;
    }
}
