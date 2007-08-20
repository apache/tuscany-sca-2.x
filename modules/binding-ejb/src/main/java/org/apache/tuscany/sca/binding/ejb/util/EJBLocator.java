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
package org.apache.tuscany.sca.binding.ejb.util;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;

/**
 * CosNaming utility
 */
public class EJBLocator {

    /*
     * Root Context Initial Reference Key ------------
     * ----------------------------------- Server Root NameServiceServerRoot
     * Cell Persistent Root NameServiceCellPersistentRoot Cell Root
     * NameServiceCellRoot, NameService Node Root NameServiceNodeRoot
     */
    public static final String SERVER_ROOT = "NameServiceServerRoot";
    public static final String CELL_PERSISTENT_ROOT = "NameServiceCellPersistentRoot";
    public static final String CELL_ROOT = "NameServiceCellRoot";
    public static final String NODE_ROOT = "NameServiceNodeRoot";
    public static final String DEFAULT_ROOT = "NameService"; // Same as
    // CELL_ROOT

    public static final String DEFAULT_HOST = "127.0.0.1"; // Default host name
    // or IP address for
    // Websphere
    public static final int DEFAULT_NAMING_PORT = 2809; // Default port
    public static final String NAMING_SERVICE = "NameService"; // The name of
    // the naming
    // service
    private static final Set<String> ROOTS =
        new HashSet<String>(Arrays.asList(new String[] {SERVER_ROOT, CELL_PERSISTENT_ROOT, CELL_ROOT, DEFAULT_ROOT,
                                                        NODE_ROOT}));

    // private static final String CHARS_TO_ESCAPE = "\\/.";
    private static final String RFC2396 =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789;/:?@&=+$,-_.!~*'()";
    private static final String HEX = "0123456789ABCDEF";

    private String hostName = DEFAULT_HOST;
    private int port = DEFAULT_NAMING_PORT;
    private String root = SERVER_ROOT;

    private ORB orb = null;
    private ObjectLocator locator = null;
    boolean managed = true;

    public EJBLocator(boolean managed) {
        this.managed = managed;
        if (!managed) {
            String url = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty(Context.PROVIDER_URL);
                }
            });
            processCorbaURL(url);
        }
    }

    public EJBLocator(String hostName, int port) {
        this.hostName = (hostName == null) ? DEFAULT_HOST : hostName;
        this.port = port > 0 ? port : DEFAULT_NAMING_PORT;
        this.root = SERVER_ROOT;
    }

    public EJBLocator(String hostName, int port, String root) {
        this(hostName, port);
        if (ROOTS.contains(root)) {
            this.root = root;
        } else {
            throw new IllegalArgumentException(root + " is not a legal root");
        }
    }

    public EJBLocator(String corbaName, boolean managed) {
        this.managed = managed;
        if (corbaName.startsWith("corbaname:iiop:")) {
            processCorbaURL(corbaName);
        } else {
            throw new IllegalArgumentException(corbaName + " is not a legal corbaname");
        }
    }

    private void processCorbaURL(String url) {
        if (url != null && (url.startsWith("corbaname:iiop:") || url.startsWith("corbaloc:iiop:"))) {
            /**
             * corbaname:iiop:<hostName>:<port>/<root>#name corbaloc:iiop:<hostname>:<port>/<root>
             * For exmaple,
             * "corbaname:iiop:localhost:2809/NameServiceServerRoot#ejb/MyEJBHome";
             * or "corbaloc:iiop:myhost:2809/NameServiceServerRoot"
             */
            String[] parts = url.split("(:|/|#)");
            if (parts.length > 2 && parts[2].length() > 0) {
                hostName = parts[2]; // The host name
                int index = hostName.lastIndexOf('@'); // version@hostname
                if (index != -1) {
                    hostName = hostName.substring(index + 1);
                }
            }
            if (parts.length > 3 && parts[3].length() > 0) {
                port = Integer.parseInt(parts[3]); // The port number
            }
            if (parts.length > 4 && parts[4].length() > 0) {
                root = parts[4]; // The root of naming
            }
        }
    }

    /**
     * The corbaloc and corbaname formats enable you to provide a URL to access
     * CORBA objects. Use the corbaloc format for resolving to a particular
     * CORBAservice without going through a naming service. Use the corbaname
     * format to resolve a stringified name from a specific naming context.
     */

    /**
     * corbaname Syntax The full corbaname BNF is: &lt;corbaname&gt; =
     * "corbaname:"&lt;corbaloc_obj&gt;["#"&lt;string_name&gt;]
     * &lt;corbaloc_obj&gt; = &lt;obj_addr_list&gt; ["/"&lt;key_string&gt;]
     * &lt;obj_addr_list&gt; = as defined in a corbaloc URL &lt;key_string&gt; =
     * as defined in a corbaloc URL &lt;string_name&gt;= stringified Name
     * empty_string Where:
     * <ul>
     * <li>corbaloc_obj: portion of a corbaname URL that identifies the naming
     * context. The syntax is identical to its use in a corbaloc URL.
     * <li>obj_addr_list: as defined in a corbaloc URL
     * <li>key_string: as defined in a corbaloc URL.
     * <li>string_name: a stringified Name with URL escapes as defined below.
     * </ul>
     * 
     * @param hostName The host name or IP address of the naming server
     * @param port The port number of the naming service
     * @param root The root of the namespace
     * @param name The JNDI name
     */
    private static String getCorbaname(String hostName, int port, String root, String name) {
        if (name == null) {
            return "corbaname:iiop:" + hostName + ":" + port + "/" + root;
        } else {
            return "corbaname:iiop:" + hostName + ":" + port + "/" + root + "#" + toCorbaname(name);
        }
    }

    String getCorbaname(String name) {
        return getCorbaname(hostName, port, root, name);
    }

    /**
     * Connect to the ORB.
     */

    // FIXME. May need to change the IBM classes if this binding is contributed
    // to Tuscany
    public ORB connect() {
        if (orb == null) {
            Properties props = new Properties();
            /*
             * This code is for IBM JVM props.put("org.omg.CORBA.ORBClass",
             * "com.ibm.CORBA.iiop.ORB");
             * props.put("com.ibm.CORBA.ORBInitRef.NameService",
             * getCorbaloc(NAMING_SERVICE));
             * props.put("com.ibm.CORBA.ORBInitRef.NameServiceServerRoot",
             * getCorbaloc("NameServiceServerRoot"));
             */
            orb = ORB.init((String[])null, props);
        }
        return orb;
    }

    /**
     * Replace substrings
     * 
     * @param source The source string.
     * @param match The string to search for within the source string.
     * @param replace The replacement for any matching components.
     * @return
     */
    private static String replace(String source, String match, String replace) {
        int index = source.indexOf(match, 0);
        if (index >= 0) {

            // We have at least one match, so gotta do the
            // work...

            StringBuffer result = new StringBuffer(source.length() + 16);
            int matchLength = match.length();
            int startIndex = 0;

            while (index >= 0) {
                result.append(source.substring(startIndex, index));
                result.append(replace);
                startIndex = index + matchLength;
                index = source.indexOf(match, startIndex);
            }

            // Grab the last piece, if any...
            if (startIndex < source.length()) {
                result.append(source.substring(startIndex));
            }

            return result.toString();

        } else {
            // No matches, just return the source...
            return source;
        }
    }

    /**
     * Resovled the JNDI name from the initial CosNaming context
     * 
     * @param jndiName
     * @return resovled CORBA ojbect
     * @throws NamingException
     */
    private static org.omg.CORBA.Object resovleString(NamingContextExt initCtx, String jndiName) throws NamingException {
        try {
            String name = stringify(jndiName);
            return initCtx.resolve_str(name);
        } catch (Exception e) {
            NamingException ne = new NamingException(e.getMessage());
            ne.setRootCause(e);
            throw ne;
        }
    }

    /**
     * Look up a CORBA object by its JNDI name
     * 
     * @param jndiName
     * @return
     * @throws NamingException
     */
    org.omg.CORBA.Object stringToObject(String jndiName) throws NamingException {
        /*
         * Using an existing ORB and invoking string_to_object with a CORBA
         * object URL with multiple name server addresses to get an initial
         * context CORBA object URLs can contain more than one bootstrap server
         * address. Use this feature when attempting to obtain an initial
         * context from a server cluster. You can specify the bootstrap server
         * addresses for all servers in the cluster in the URL. The operation
         * will succeed if at least one of the servers is running, eliminating a
         * single point of failure. There is no guarantee of any particular
         * order in which the address list will be processed. For example, the
         * second bootstrap server address may be used to obtain the initial
         * context even though the first bootstrap server in the list is
         * available. An example of a corbaloc URL with multiple addresses
         * follows. obj =
         * orb.string_to_object("corbaloc::myhost1:9810,:myhost1:9811,:myhost2:9810/NameService");
         */
        String corbaName = null;
        if (jndiName.startsWith("corbaloc:") || jndiName.startsWith("corbaname:")) {
            // Keep the qualified URL
            corbaName = jndiName;
        } else {
            // Create a corbaname URL
            corbaName = getCorbaname(jndiName);
        }

        connect();
        org.omg.CORBA.Object obj = orb.string_to_object(corbaName);
        return obj;
    }

    private boolean isJndiConfigured() {
        if (managed)
            return true;
        Boolean provided = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                String initCtxFactory = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
                if (initCtxFactory == null) {
                    URL file = Thread.currentThread().getContextClassLoader().getResource("jndi.properties");
                    if (file != null) {
                        return Boolean.TRUE;
                    } else {
                        return Boolean.FALSE;
                    }
                } else {
                    return Boolean.TRUE;
                }
            }
        });
        return provided.booleanValue();
    }

    /**
     * The character escape rules for the stringified name portion of an
     * corbaname are: US-ASCII alphanumeric characters are not escaped.
     * Characters outside this range are escaped, except for the following: ; / : ? @ & = + $ , - _ . ! ~ * ' ( )
     * corbaname Escape Mechanism The percent '%' character is used as an
     * escape. If a character that requires escaping is present in a name
     * component it is encoded as two hexadecimal digits following a "%"
     * character to represent the octet. (The first hexadecimal character
     * represent the highorder nibble of the octet, the second hexadecimal
     * character represents the low-order nibble.) If a '%' is not followed by
     * two hex digits, the stringified name is syntactically invalid.
     * @param s
     * @return RFC2396-encoded stringified name
     */
    static String encode2396(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer encoded = new StringBuffer(s);
        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            if (RFC2396.indexOf(c) == -1) {
                encoded.setCharAt(i, '%');
                char ac[] = Integer.toHexString(c).toCharArray();
                if (ac.length == 2) {
                    encoded.insert(i + 1, ac);
                } else if (ac.length == 1) {
                    encoded.insert(i + 1, '0');
                    encoded.insert(i + 2, ac[0]);
                } else {
                    throw new IllegalArgumentException("Invalid character '" + c + "' in \"" + s + "\"");
                }
                i += 2; // NOPMD
            }
        }
        return encoded.toString();
    }

    /**
     * Decode an RFC2396-encoded string
     * 
     * @param s
     * @return Plain string
     */
    static String decode2396(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer decoded = new StringBuffer(s);
        for (int i = 0; i < decoded.length(); i++) {
            char c = decoded.charAt(i);
            if (c == '%') {
                if (i + 2 >= decoded.length()) {
                    throw new IllegalArgumentException("Incomplete key_string escape sequence");
                }
                int j;
                j = HEX.indexOf(decoded.charAt(i + 1)) * 16 + HEX.indexOf(decoded.charAt(i + 2));
                decoded.setCharAt(i, (char)j);
                decoded.delete(i + 1, i + 3);
            } else if (RFC2396.indexOf(c) == -1) {
                throw new IllegalArgumentException("Invalid key_string character '" + c + "'");
            }
        }
        return decoded.toString();
    }

    /**
     * The backslash '\' character escapes the reserved meaning of '/', '.', and
     * '\' in a stringified name.
     * 
     * @param jndiName
     * @return Escaped stringified name for CosNaming
     */
    private static String stringify(String jndiName) {
        // Esacpe . into \. since it's an INS naming delimeter
        return replace(encode2396(jndiName), ".", "\\.");
    }

    /**
     * Escape the "." into "%5C%2E"
     * 
     * @param jndiName
     * @return corbaname treating "." as a literal
     */
    private static String toCorbaname(String jndiName) {
        // Esacpe . into %5C%2E (\.) since it's an INS naming delimeter
        // For example, sca.sample.StockQuote --->
        // sca%5C%2Esample%5C%2EStockQuote/StockQuote
        return replace(encode2396(jndiName), ".", "%5C%2E");
    }

    private ObjectLocator getObjectLocator() throws NamingException {
        if (locator != null) {
            return locator;
        }
        /*
         * For managed env, jndi is assumed to be configured by default For
         * unmanaged environment, jndi could have configured through
         * jndi.properties file
         */
        if (isJndiConfigured()) {
            locator = new JndiLocator();
        } else { // this is definitely JSE env without jndi configured. Use
            // Corba.
            locator = new CosNamingLocator();
        }
        return locator;
    }

    public Object locate(String jndiName) throws NamingException {

        Object result = getObjectLocator().locate(jndiName);
        return result;
    }

    private static interface ObjectLocator {
        Object locate(String name) throws NamingException;
    }

    private final class JndiLocator implements ObjectLocator {
        private Context context;

        private JndiLocator() throws NamingException {
            /*
            final Properties props = AccessController.doPrivileged(new PrivilegedAction<Properties>() {
                public Properties run() {
                    return System.getProperties();
                }
            });
            Properties properties = new Properties();
            for (Map.Entry e : props.entrySet()) {
                String name = (String)e.getKey();
                if (name.startsWith("java.naming.")) {
                    properties.setProperty(name, (String)e.getValue());
                }
            }
            // System.out.println(properties);
            this.context = new InitialContext(properties);
            */
            this.context = new InitialContext();
        }

        public Object locate(String name) throws NamingException {
            return context.lookup(name);
        }
    }

    private final class CosNamingLocator implements ObjectLocator {
        private NamingContextExt context;

        private CosNamingLocator() {
        }

        public Object locate(String name) throws NamingException {
            if (context != null) {
                return resovleString(context, name);
            } else {
                return stringToObject(name);
            }
        }
    }

    public void setHostEnv(boolean managed) {
        this.managed = managed;
    }
}
