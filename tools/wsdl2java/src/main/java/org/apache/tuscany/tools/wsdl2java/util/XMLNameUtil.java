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
package org.apache.tuscany.tools.wsdl2java.util;

import java.util.ArrayList;
import java.util.List;

public class XMLNameUtil {

    /**
     * Returns a valid Java name from an XML Name.
     *
     * @param name
     * @param isUpperCase
     * @return a valid Java name from an XML Name
     */
    public static String getJavaNameFromXMLName(String name, boolean isUpperCase) {
        List<String> parsedName = parseName(name, '_');
        StringBuilder result = new StringBuilder(64 * parsedName.size());
        for (String nameComponent: parsedName) {
            if (nameComponent.length() > 0) {
                if (result.length() > 0 || isUpperCase) {
                    result.append(Character.toUpperCase(nameComponent.charAt(0)));
                    result.append(nameComponent.substring(1));
                } else {
                    result.append(nameComponent);
                }
            }
        }
    
        if (result.length() == 0) {
            return "_";
        }
        if (Character.isJavaIdentifierStart(result.charAt(0))) {
            return isUpperCase ? result.toString() : decapitalizeName(result.toString());
        }
        return "_" + result;
    }

    /**
     * Decapitalize a name.
     * @param name
     * @return a decapitalized name
     */
    public static String decapitalizeName(String name) {
        if (name.length() == 0) {
            return name;
        } else {
            String lowerName = name.toLowerCase();
            int i;
            for (i = 0; i < name.length(); i++) {
                if (name.charAt(i) == lowerName.charAt(i)) {
                    break;
                }
            }
            if (i > 1 && i < name.length()) {
                --i;
            }
            return name.substring(0, i).toLowerCase() + name.substring(i);
        }
    }

    /**
     * Parse the given name.
     *
     * @param sourceName
     * @param separator
     * @return some stuff parsed from the name
     */
    private static List<String> parseName(String sourceName, char separator) {
        List<String> result = new ArrayList<String>();
        if (sourceName != null) {
            StringBuilder currentWord = new StringBuilder(64);
            boolean lastIsLower = false;
            int index;
            int length;
            for (index = 0, length = sourceName.length(); index < length; ++index) {
                char curChar = sourceName.charAt(index);
                if (!Character.isJavaIdentifierPart(curChar)) {
                    curChar = separator;
                }
                if (Character.isUpperCase(curChar)
                    || (!lastIsLower && Character.isDigit(curChar))
                    || curChar == separator) {
                    
                    if (lastIsLower && currentWord.length() > 1 
                        || curChar == separator && currentWord.length() > 0) {
                        result.add(currentWord.toString());
                        currentWord = new StringBuilder(64);
                    }
                    lastIsLower = false;
                } else {
                    if (!lastIsLower) {
                        int currentWordLength = currentWord.length();
                        if (currentWordLength > 1) {
                            char lastChar = currentWord.charAt(--currentWordLength);
                            currentWord.setLength(currentWordLength);
                            result.add(currentWord.toString());
                            currentWord = new StringBuilder(64);
                            currentWord.append(lastChar);
                        }
                    }
                    lastIsLower = true;
                }
    
                if (curChar != separator) {
                    currentWord.append(curChar);
                }
            }
    
            result.add(currentWord.toString());
        }
        return result;
    }
    
    /**
     * Return an EPackage name for the given namespace.
     *
     * @param namespace
     * @return an EPackage name for the given namespace
     */
    public static String getPackageNameFromNamespace(String namespace) {
       return org.apache.tuscany.sdo.helper.SDOXSDEcoreBuilder.getDefaultPackageName(namespace);
    }


}
