/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;

/**
 * A utility class that converts between XML names and Java names.
 */
public class XMLNameUtil {

    private static final List domains = Arrays.asList(new String[]{"COM", "com", "ORG", "org"});

    /**
     * Constructor
     */
    private XMLNameUtil() {
        super();
    }

    /**
     * Return an EPackage name for the given namespace.
     *
     * @param namespace
     * @return
     */
    public static String getPackageNameFromNamespace(String namespace) {

        URI uri = URI.createURI(namespace);
        List parsedName;
        if (uri.isHierarchical()) {
            String host = uri.host();
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }
            parsedName = parseName(host, '.');
            Collections.reverse(parsedName);
            if (!parsedName.isEmpty()) {
                parsedName.set(0, ((String) parsedName.get(0)).toLowerCase());
            }

            parsedName.addAll(parseName(uri.trimFileExtension().path(), '/'));

        } else {
            String opaquePart = uri.opaquePart();
            int index = opaquePart.indexOf(":");
            if (index != -1 && "urn".equalsIgnoreCase(uri.scheme())) {
                parsedName = parseName(opaquePart.substring(0, index), '-');
                if (parsedName.size() > 0 && domains.contains(parsedName.get(parsedName.size() - 1))) {
                    Collections.reverse(parsedName);
                    parsedName.set(0, ((String) parsedName.get(0)).toLowerCase());
                }
                parsedName.addAll(parseName(opaquePart.substring(index + 1), '/'));

            } else {
                parsedName = parseName(opaquePart, '/');
            }
        }

        StringBuffer qualifiedPackageName = new StringBuffer();
        for (Iterator i = parsedName.iterator(); i.hasNext();) {
            String packageName = (String) i.next();
            if (packageName.length() > 0) {
                if (qualifiedPackageName.length() > 0) {
                    qualifiedPackageName.append('.');
                }
                qualifiedPackageName.append(getJavaNameFromXMLName(packageName, false));
            }
        }
        return qualifiedPackageName.toString();

    }

    /**
     * Returns a namespace prefix for the given package Name
     *
     * @param packageName
     * @return
     */
    public static String getNSPrefixFromPackageName(String packageName) {
        String nsPrefix = packageName;
        int index = nsPrefix.lastIndexOf('.');
        return index == -1 ? nsPrefix : nsPrefix.substring(index + 1);
    }

    /**
     * Parse the given name.
     *
     * @param sourceName
     * @param separator
     * @return
     */
    private static List parseName(String sourceName, char separator) {
        List result = new ArrayList();
        if (sourceName != null) {
            StringBuffer currentWord = new StringBuffer();
            boolean lastIsLower = false;
            for (int index = 0, length = sourceName.length(); index < length; ++index) {
                char curChar = sourceName.charAt(index);
                if (!Character.isJavaIdentifierPart(curChar)) {
                    curChar = separator;
                }
                if (Character.isUpperCase(curChar) || (!lastIsLower && Character.isDigit(curChar)) || curChar == separator) {
                    if (lastIsLower && currentWord.length() > 1 || curChar == separator && currentWord.length() > 0) {
                        result.add(currentWord.toString());
                        currentWord = new StringBuffer();
                    }
                    lastIsLower = false;
                } else {
                    if (!lastIsLower) {
                        int currentWordLength = currentWord.length();
                        if (currentWordLength > 1) {
                            char lastChar = currentWord.charAt(--currentWordLength);
                            currentWord.setLength(currentWordLength);
                            result.add(currentWord.toString());
                            currentWord = new StringBuffer();
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
     * Returns a valid Java name from an XML Name
     *
     * @param name
     * @param isUpperCase
     * @return
     */
    public static String getJavaNameFromXMLName(String name, boolean isUpperCase) {
        List parsedName = parseName(name, '_');
        StringBuffer result = new StringBuffer();
        for (Iterator i = parsedName.iterator(); i.hasNext();) {
            String nameComponent = (String) i.next();
            if (nameComponent.length() > 0) {
                if (result.length() > 0 || isUpperCase) {
                    result.append(Character.toUpperCase(nameComponent.charAt(0)));
                    result.append(nameComponent.substring(1));
                } else {
                    result.append(nameComponent);
                }
            }
        }

        return result.length() == 0 ? "_" : Character.isJavaIdentifierStart(result.charAt(0)) ? isUpperCase ? result.toString() : decapitalizeName(result.toString()) : "_" + result;
    }

    /**
     * Returns a valid fully qualified class name from a QName
     * @param namespace
     * @param name
     * @return
     */
    public static String getFullyQualifiedClassNameFromQName(String namespace, String name) {
        return XMLNameUtil.getPackageNameFromNamespace(namespace)+'.'+XMLNameUtil.getJavaNameFromXMLName(name, true);
    }
    
    /**
     * Decapitalize a name.
     * @param name
     * @return
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
     * Capitalize a name.
     * @param name
     * @return
     */
    public static String capitalizeName(String name) {
        int l=name.length();
        if (l == 0) {
            return name;
        } else if (l==1) {
            return name.toUpperCase();
        } else {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }

}