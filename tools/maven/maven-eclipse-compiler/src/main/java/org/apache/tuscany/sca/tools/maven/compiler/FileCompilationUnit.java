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

import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

/**
 * An implementation of ICompilationUnit that wraps a File.
 *
 * @version $Rev: $ $Date: $
 */
class FileCompilationUnit implements ICompilationUnit {
    private final static char fileSeparator = System.getProperty("file.separator").charAt(0);
    private String className;
    private String sourceFile;

    FileCompilationUnit(String sourceFile, String className) {
        this.className = className;
        this.sourceFile = sourceFile;
    }

    public char[] getContents() {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(sourceFile));
            CharArrayWriter writer = new CharArrayWriter(); 
            char[] b = new char[2048];
            for (;;) {
                int n = reader.read(b);
                if (n <= 0) {
                    break;
                }
                writer.write(b, 0, n);
            }
            return writer.toCharArray();
            
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public char[] getFileName() {
        return (className.replace('.', fileSeparator) + ".java").toCharArray();
    }

    public char[] getMainTypeName() {
        int dot = className.lastIndexOf('.');
        if (dot > 0) {
            return className.substring(dot + 1).toCharArray();
        }
        return className.toCharArray();
    }

    public char[][] getPackageName() {
        StringTokenizer tokens = new StringTokenizer(className, ".");
        char[][] packageName = new char[tokens.countTokens() - 1][];
        for (int i = 0; i < packageName.length; i++) {
            String token = tokens.nextToken();
            packageName[i] = token.toCharArray();
        }
        return packageName;
    }
}