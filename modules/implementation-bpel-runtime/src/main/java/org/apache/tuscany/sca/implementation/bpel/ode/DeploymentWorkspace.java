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

package org.apache.tuscany.sca.implementation.bpel.ode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;

/**
 * 
 */
public class DeploymentWorkspace {
    private static final Logger logger = Logger.getLogger(DeploymentWorkspace.class.getName());

    static final String DEPLOY_FILENAME = "deploy.xml";

    private BPELImplementation implementation;
    private File workingDir;
    private File bpelFile;

    /**
     * @param implementation
     */
    public DeploymentWorkspace(BPELImplementation implementation) {
        super();
        this.implementation = implementation;
        this.workingDir = createWorkingDirectory();
    }

    /**
     * @param implementation
     */
    public DeploymentWorkspace(BPELImplementation implementation, File workingDir) {
        super();
        this.implementation = implementation;
        this.workingDir = workingDir;
        if (this.workingDir == null) {
            this.workingDir = createWorkingDirectory();
        }
    }

    public File getCBPFile() throws IOException {
        String name = getBPELFile().getName();
        int index = name.lastIndexOf('.');
        if (index != -1) {
            name = name.substring(0, index);
        }
        return new File(workingDir, name + ".cbp");
    }

    public synchronized File getBPELFile() throws IOException {
        if (bpelFile != null) {
            String location = implementation.getProcessDefinition().getLocation();
            String fileName = implementation.getProcessDefinition().getURI();
            File file = new File(workingDir, fileName);
            if (file.isFile()) {
                bpelFile = file;
                return file;
            }
            URL url = new URL(location);
            this.bpelFile = copy(url, workingDir, fileName);
        }
        return bpelFile;
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    public static URI createURI(String uri) {
        if (uri == null) {
            return null;
        }
        if (uri.indexOf('%') != -1) {
            // Avoid double-escaping
            return URI.create(uri);
        }
        int index = uri.indexOf(':');
        String scheme = null;
        String ssp = uri;
        if (index != -1) {
            scheme = uri.substring(0, index);
            ssp = uri.substring(index + 1);
        }
        try {
            return new URI(scheme, ssp, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets the File containing the BPEL process definition
     * @return - the File object containing the BPEL process
     */
    private static File getContainer(String location) {
        try {
            File theProcess = null;
            URI locationURI = createURI(location);
            String protocol = locationURI.getScheme();
            if ("file".equals(protocol)) {
                theProcess = new File(locationURI);
            } else if ("jar".equals(protocol) || "wsjar".equals(protocol) || "zip".equals(protocol)) {
                String uri = locationURI.toString();
                // jar contribution
                uri = uri.substring(protocol.length() + 1, uri.lastIndexOf("!/"));
                locationURI = createURI(uri);
                if ("file".equals(locationURI.getScheme())) {
                    theProcess = new File(locationURI);
                }
            }
            return theProcess;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception converting BPEL file URL to an URI: " + location, e);
        } // end try
        return null;
    } // end getBPELFile

    /** 
     * Gets the directory containing the BPEL process
     * @return
     */
    static File getDirectory(String location) {
        File file = getContainer(location);
        if (file == null) {
            return null;
        }
        File theDir = file.getParentFile();
        return theDir;
    } // end getDirectory

    public File getDeployFile() {
        return new File(workingDir, DEPLOY_FILENAME);
    }

    private static String getSystemProperty(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty(name);
            }
        });
    }

    private File createWorkingDirectory() {
        String tmpDir = getSystemProperty("java.io.tmpdir");
        File root = new File(tmpDir);
        // Add user name as the prefix. For multiple users on the same Lunix,
        // there will be permission issue if one user creates the .tuscany folder
        // first under /tmp with no write permission for others.
        String userName = getSystemProperty("user.name");
        if (userName != null) {
            root = new File(root, userName);
        }
        root = new File(root, ".tuscany/bpel/" + UUID.randomUUID().toString());
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("BPEL working directory: " + root);
        }
        return root;
    }

    public static File copy(URL url, File directory, String fileName) throws IOException {
        File file = new File(directory, fileName);
        file.getParentFile().mkdirs();
        FileOutputStream os = new FileOutputStream(file);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        InputStream is = connection.getInputStream();
        byte[] buf = new byte[8192];
        while (true) {
            int size = is.read(buf);
            if (size < 0)
                break;
            os.write(buf, 0, size);
        }
        is.close();
        os.close();
        return file;
    }

    private static boolean deleteFiles(File file) {
        boolean result = true;
        if (file.isFile()) {
            if (!file.delete()) {
                result = false;
            }
        } else if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!deleteFiles(f)) {
                    result = false;
                }
            }
            if (!file.delete()) {
                result = false;
            }
        }
        return result;
    }

    public boolean delete() {
        return deleteFiles(workingDir);
    }

}
