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

package org.apache.tuscany.sca.extensibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the service descriptors. The syntax of the service declaration is similar with the OSGi
 * headers with the following exceptions:
 * <ul>
 * <li>Tuscany uses , and ; as the separator for attibutes
 * <li>Tuscany 
 */
public class ServiceDeclarationParser {

    // private static final String PATH_SEPARATOR = ","; // OSGi style
    private static final String PATH_SEPARATOR = "|";

    // private static final String SEGMENT_SEPARATOR = ";"; // OSGi style
    private static final String SEGMENT_SEPARATOR = ";,";

    private static final String ATTRIBUTE_SEPARATOR = "=";
    private static final String DIRECTIVE_SEPARATOR = ":=";

    private static final char QUOTE_CHAR = '"';
    private static final String QUOTE = "\"";

    // Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2,
    //            path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2
    public static List<Descriptor> parse(String header) {

        if (header != null) {
            if (header.length() == 0) {
                throw new IllegalArgumentException("A header cannot be an empty string.");
            }

            String[] clauseStrings = parseDelimitedString(header, PATH_SEPARATOR);

            List<Descriptor> completeList = new ArrayList<Descriptor>();
            for (int i = 0; (clauseStrings != null) && (i < clauseStrings.length); i++) {
                completeList.add(parseClause(clauseStrings[i]));
            }

            return completeList;
        }

        return null;

    }

    /**
     * Parse the declaration into a map of name/value pairs. The class name is added under "class"
     * and directives are added using @<directiveName> as the key.
     * @param declaration
     * @return A map of attributes
     */
    public static Map<String, String> parseDeclaration(String declaration) {
        List<Descriptor> descriptors = parse(declaration);
        Descriptor descriptor = descriptors.get(0);
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(descriptor.getAttributes());
        map.put("class", descriptor.getValue());
        for (Map.Entry<String, String> e : descriptor.getDirectives().entrySet()) {
            // For directives, add @ as the prefix for the key
            map.put("@" + e.getKey(), e.getValue());
        }
        return map;
    }

    // Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2
    private static Descriptor parseClause(String clauseString) throws IllegalArgumentException {
        // Break string into semi-colon delimited pieces.
        String[] pieces = parseDelimitedString(clauseString, SEGMENT_SEPARATOR);

        // Count the number of different paths; paths
        // will not have an '=' in their string. This assumes
        // that paths come first, before directives and
        // attributes.
        int pathCount = 0;
        for (int pieceIdx = 0; pieceIdx < pieces.length; pieceIdx++) {
            if (pieces[pieceIdx].indexOf('=') >= 0) {
                break;
            }
            pathCount++;
        }

        // Create an array of paths.
        String[] paths = new String[pathCount];
        System.arraycopy(pieces, 0, paths, 0, pathCount);

        // Parse the directives/attributes.
        Map<String, String> dirsMap = new HashMap<String, String>();
        Map<String, String> attrsMap = new HashMap<String, String>();
        int idx = -1;
        String sep = null;
        for (int pieceIdx = pathCount; pieceIdx < pieces.length; pieceIdx++) {
            // Check if it is a directive.
            if ((idx = pieces[pieceIdx].indexOf(DIRECTIVE_SEPARATOR)) >= 0) {
                sep = DIRECTIVE_SEPARATOR;
            }
            // Check if it is an attribute.
            else if ((idx = pieces[pieceIdx].indexOf(ATTRIBUTE_SEPARATOR)) >= 0) {
                sep = ATTRIBUTE_SEPARATOR;
            }
            // It is an error.
            else {
                throw new IllegalArgumentException("Not a directive/attribute: " + clauseString);
            }

            String key = pieces[pieceIdx].substring(0, idx).trim();
            String value = pieces[pieceIdx].substring(idx + sep.length()).trim();

            // Remove quotes, if value is quoted.
            if (value.startsWith(QUOTE) && value.endsWith(QUOTE)) {
                value = value.substring(1, value.length() - 1);
            }

            // Save the directive/attribute in the appropriate array.
            if (sep.equals(DIRECTIVE_SEPARATOR)) {
                // Check for duplicates.
                if (dirsMap.get(key) != null) {
                    throw new IllegalArgumentException("Duplicate directive: " + key);
                }
                dirsMap.put(key, value);
            } else {
                // Check for duplicates.
                if (attrsMap.get(key) != null) {
                    throw new IllegalArgumentException("Duplicate attribute: " + key);
                }
                attrsMap.put(key, value);
            }
        }

        StringBuffer path = new StringBuffer();
        for (int i = 0; i < paths.length; i++) {
            path.append(paths[i]);
            if (i != paths.length - 1) {
                path.append(';');
            }
        }

        Descriptor descriptor = new Descriptor();
        descriptor.text = clauseString;
        descriptor.value = path.toString();
        descriptor.valueComponents = paths;
        descriptor.attributes = attrsMap;
        descriptor.directives = dirsMap;

        return descriptor;
    }

    /**
     * Parses delimited string and returns an array containing the tokens. This
     * parser obeys quotes, so the delimiter character will be ignored if it is
     * inside of a quote. This method assumes that the quote character is not
     * included in the set of delimiter characters.
     * @param value the delimited string to parse.
     * @param delim the characters delimiting the tokens.
     * @return an array of string tokens or null if there were no tokens.
    **/
    private static String[] parseDelimitedString(String value, String delim) {
        if (value == null) {
            value = "";
        }

        List<String> list = new ArrayList<String>();

        int CHAR = 1;
        int DELIMITER = 2;
        int STARTQUOTE = 4;
        int ENDQUOTE = 8;

        StringBuffer sb = new StringBuffer();

        int expecting = (CHAR | DELIMITER | STARTQUOTE);

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            boolean isDelimiter = (delim.indexOf(c) >= 0);
            boolean isQuote = (c == QUOTE_CHAR);

            if (isDelimiter && ((expecting & DELIMITER) > 0)) {
                list.add(sb.toString().trim());
                sb.delete(0, sb.length());
                expecting = (CHAR | DELIMITER | STARTQUOTE);
            } else if (isQuote && ((expecting & STARTQUOTE) > 0)) {
                sb.append(c);
                expecting = CHAR | ENDQUOTE;
            } else if (isQuote && ((expecting & ENDQUOTE) > 0)) {
                sb.append(c);
                expecting = (CHAR | STARTQUOTE | DELIMITER);
            } else if ((expecting & CHAR) > 0) {
                sb.append(c);
            } else {
                throw new IllegalArgumentException("Invalid delimited string: " + value);
            }
        }

        if (sb.length() > 0) {
            list.add(sb.toString().trim());
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static class Descriptor {
        private String text;
        private String value;
        private String[] valueComponents;
        private Map<String, String> attributes;
        private Map<String, String> directives;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String[] getValueComponents() {
            return valueComponents;
        }

        public void setValueComponents(String[] valueComponents) {
            this.valueComponents = valueComponents;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public Map<String, String> getDirectives() {
            return directives;
        }

        public String toString() {
            return text;
        }

    }

}
