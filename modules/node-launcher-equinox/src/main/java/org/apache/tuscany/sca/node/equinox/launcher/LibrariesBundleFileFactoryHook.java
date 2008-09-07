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

package org.apache.tuscany.sca.node.equinox.launcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;

/**
 * A bundle file factory hook that.
 *
 * @version $Rev: $ $Date: $
*/
public class LibrariesBundleFileFactoryHook implements org.eclipse.osgi.baseadaptor.hooks.BundleFileFactoryHook {
    
    private Manifest manifest;
    
    private static class LibrariesBundleFile extends BundleFile {
        
        private static class ManifestBundleEntry extends BundleEntry {
            
            private byte[] bytes;
            
            public ManifestBundleEntry(Manifest manifest) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    manifest.write(bos);
                    bytes = bos.toByteArray();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public URL getFileURL() {
                return null;
            }
            
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(bytes);
            }
            
            @Override
            public URL getLocalURL() {
                return null;
            }
            
            @Override
            public String getName() {
                return "META-INF/MANIFEST.MF";
            }
            
            @Override
            public long getSize() {
                return bytes.length;
            }
            
            @Override
            public long getTime() {
                return -1;
            }
            
            @Override
            public byte[] getBytes() throws IOException {
                return bytes;
            }
        }
        
        private Manifest manifest;
        
        public LibrariesBundleFile(Object baseFile, Manifest manifest) {
            super((File)baseFile);
            this.manifest = manifest;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public boolean containsDir(String dir) {
            return false;
        }

        @Override
        public BundleEntry getEntry(String path) {
            if ("META-INF/MANIFEST.MF".equals(path)) {
                return new ManifestBundleEntry(manifest);
            }
            return null;
        }

        @Override
        public Enumeration getEntryPaths(String path) {
            return null;
        }

        @Override
        public File getFile(String path, boolean nativeCode) {
            return null;
        }

        @Override
        public void open() throws IOException {
        }
    }
    
    public LibrariesBundleFileFactoryHook(Manifest manifest) {
        this.manifest = manifest;
    }

    public BundleFile createBundleFile(Object content, BaseData data, boolean base) throws IOException {
        if ("org.apache.tuscany.sca.node.launcher.equinox.libraries".equals(data.getLocation())) {
            return new LibrariesBundleFile(content, manifest);
        } else {
            return null;
        }
    }

}
