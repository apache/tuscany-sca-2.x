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

package org.apache.tuscany.sca.tools.maven.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

/**
 * An implementation of INameEnvironment based on a ClassLoader.
 * 
 * @version $Rev: $ $Date: $
 */
class ClassLoaderNameEnvironment implements INameEnvironment {
    private final static char fileSeparator = System.getProperty("file.separator").charAt(0);
    
    private ClassLoader classLoader;
    private List<String> sourceLocations;
    private Map<String, File> sourceFiles;

    ClassLoaderNameEnvironment(ClassLoader classLoader, List<String> sourceLocations) {
        this.classLoader = classLoader;
        this.sourceLocations = sourceLocations;
        sourceFiles = new HashMap<String, File>();
    }

    public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        StringBuffer className = new StringBuffer();
        for (char[] name: compoundTypeName) {
            if (className.length() != 0) {
                className.append('.');
            }
            className.append(name);
        }
        return nameAnswer(className.toString());
    }

    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        StringBuffer className = new StringBuffer();
        for (char[] name: packageName) {
            if (className.length() != 0) {
                className.append('.');
            }
            className.append(name);
        }
        if (className.length() != 0) {
            className.append('.');
        }
        className.append(typeName);
        return nameAnswer(className.toString());
    }

    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        StringBuffer fullPackageName = new StringBuffer();
        if (parentPackageName != null) {
            for (char[] name: parentPackageName) {
                if (fullPackageName.length() != 0) {
                    fullPackageName.append('.');
                }
                fullPackageName.append(name);
            }
        }
        if (fullPackageName.length() != 0) {
            fullPackageName.append('.');
        }
        fullPackageName.append(packageName);
        return isPackage(fullPackageName.toString());
    }

    public void cleanup() {
    }

    /**
     * Returns the source file for the given class name.
     * 
     * @param className
     * @return
     */
    private File sourceFile(String className) {
        File sourceFile = (File)sourceFiles.get(className);
        if (sourceFile != null) {
            return sourceFile;
        }
        String sourceName = className.replace('.', fileSeparator) + ".java";
        sourceFile = sourceFileInSourceLocations(sourceName);
        sourceFiles.put(className, sourceFile);
        return sourceFile;
    }

    /**
     * Returns the source file for the given source path relative to the source locations.
     * 
     * @param className
     * @return
     */
    private File sourceFileInSourceLocations(String relativePath) {
        for (String sourceLocation : sourceLocations) {
            File sourceFile = new File(sourceLocation, relativePath);
            if (sourceFile.exists()) {
                return sourceFile;
            }
        }
        return null;
    }

    /**
     * Returns true if the given name is a package name.
     * 
     * @param name
     * @return
     */
    private boolean isPackage(String name) {
        if (sourceFile(name) != null) {
            return false;
        }
        String resourceName = '/' + name.replace('.', '/') + ".class";
        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null) {
            return true;
        } else {
            try {
                is.close();
            } catch (IOException e) {}
            return false;
        }
    }

    /**
     * Find the NameAnswer for by the given class name.
     * 
     * @param className
     * @return
     */
    private NameEnvironmentAnswer nameAnswer(String className) {
        try {
            File sourceFile = sourceFile(className);
            if (sourceFile != null) {
                ICompilationUnit compilationUnit = new FileCompilationUnit(sourceFile.getAbsolutePath(), className);
                return new NameEnvironmentAnswer(compilationUnit, null);
            }

            String resourceName = className.replace('.', '/') + ".class";
            InputStream is = classLoader.getResourceAsStream(resourceName);
            if (is == null) {
                return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[2048];
            for (;;) {
                int n = is.read(b);
                if (n <= 0) {
                    break;
                }
                bos.write(b, 0, n);
            }
            byte[] classBytes = bos.toByteArray();
            
            ClassFileReader classFileReader = new ClassFileReader(classBytes, className.toCharArray(), true);
            return new NameEnvironmentAnswer(classFileReader, null);
            
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
