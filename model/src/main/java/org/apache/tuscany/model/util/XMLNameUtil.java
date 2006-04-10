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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A utility class that converts between XML names and Java names.
 */
public final class XMLNameUtil {

    private static final List<String> DOMAINS = Arrays.asList(new String[]{"COM", "com", "ORG", "org"});

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
     * @return an EPackage name for the given namespace
     */
    public static String getPackageNameFromNamespace(String namespace) {

        URI uri;
        try {
            uri = new URI(namespace);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        List<String> parsedName;
        if (uri.isAbsolute()) {
            String host = uri.getHost();
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }
            parsedName = parseName(host, '.');
            Collections.reverse(parsedName);
            if (!parsedName.isEmpty()) {
                parsedName.set(0, parsedName.get(0).toLowerCase());
            }

            parsedName.addAll(parseName(trimFileExtension(uri.getPath()), '/'));

        } else {
            String opaquePart = uri.getAuthority();
            int index = opaquePart.indexOf(":");
            if (index != -1 && "urn".equalsIgnoreCase(uri.getScheme())) {
                parsedName = parseName(opaquePart.substring(0, index), '-');
                if (parsedName.size() > 0 && DOMAINS.contains(parsedName.get(parsedName.size() - 1))) {
                    Collections.reverse(parsedName);
                    parsedName.set(0, parsedName.get(0).toLowerCase());
                }
                parsedName.addAll(parseName(opaquePart.substring(index + 1), '/'));

            } else {
                parsedName = parseName(opaquePart, '/');
            }
        }

        StringBuilder qualifiedPackageName = new StringBuilder(128);
        for (String packageName : parsedName) {
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
     * Trim the file extension from a path.
     * @param path
     * @return
     */
    private static String trimFileExtension(String path) {
        int s=path.lastIndexOf('/');
        int d=path.lastIndexOf('.');
        if (d>s) {
            return path.substring(0,d);
        } else {
            return path;
        }
    }

    /**
     * Returns a namespace prefix for the given package Name.
     *
     * @param packageName
     * @return a namespace prefix for the given package Name
     */
    public static String getNSPrefixFromPackageName(String packageName) {
        int index = packageName.lastIndexOf('.');
        return index == -1 ? packageName : packageName.substring(index + 1);
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
     * Returns a valid fully qualified class name from a QName.
     * @param namespace
     * @param name
     * @return a valid fully qualified class name from a QName
     */
    public static String getFullyQualifiedClassNameFromQName(String namespace, String name) {
        return XMLNameUtil.getPackageNameFromNamespace(namespace) + '.'
            + XMLNameUtil.getJavaNameFromXMLName(name, true);
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
     * Capitalize a name.
     * @param name
     * @return a capitalized name
     */
    public static String capitalizeName(String name) {
        int l = name.length();
        if (l == 0) {
            return name;
        } else if (l == 1) {
            return name.toUpperCase();
        } else {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }

}
