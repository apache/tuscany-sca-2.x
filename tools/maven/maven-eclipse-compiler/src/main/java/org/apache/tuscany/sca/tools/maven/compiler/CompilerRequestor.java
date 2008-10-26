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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerError;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;

class CompilerRequestor implements ICompilerRequestor {
    private String outputDirectory;
    private List<CompilerError> compilerErrors;

    public CompilerRequestor(String outputDirectory, List<CompilerError> compilerErrors) {
        this.outputDirectory = outputDirectory;
        this.compilerErrors = compilerErrors;
    }

    public void acceptResult(CompilationResult result) {
        boolean hasErrors = false;
        if (result.hasProblems()) {
            
            // Convert JDT IProblems into plexus CompilerErrors
            for (IProblem problem: result.getProblems()) {
                if (problem.isWarning()) {
                    compilerErrors.add(new CompilerError(new String(problem.getOriginatingFileName()),
                                                         false,
                                                         problem.getSourceLineNumber(),
                                                         problem.getSourceStart(),
                                                         problem.getSourceLineNumber(),
                                                         problem.getSourceEnd(),
                                                         problem.getMessage()));
                    
                } else if (problem.isError()) {
                    hasErrors = true;
                    compilerErrors.add(new CompilerError(new String(problem.getOriginatingFileName()),
                                                         true,
                                                         problem.getSourceLineNumber(),
                                                         problem.getSourceStart(),
                                                         problem.getSourceLineNumber(),
                                                         problem.getSourceEnd(),
                                                         problem.getMessage()));
                    
                }
            }
        }
        
        // Write the class files 
        if (!hasErrors) {
            ClassFile[] classFiles = result.getClassFiles();
            for (ClassFile classFile: classFiles) {

                // Create file and parent directories
                StringBuffer className = new StringBuffer();
                for (char[] name: classFile.getCompoundName()) {
                    if (className.length() != 0) {
                        className.append('.');
                    }
                    className.append(name);
                }
                File file = new File(outputDirectory, className.toString().replace('.', '/') + ".class");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                
                // Write class file contents
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(classFile.getBytes());
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException(e);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {}
                    }
                }
            }
        }
    }

}