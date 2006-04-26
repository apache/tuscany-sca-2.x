/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.config;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.apache.tuscany.model.assembly.ComponentInfo;

/**
 * @version $Rev$ $Date$
 */
public class SCAVisitor implements ClassVisitor {
    private final ComponentInfo componentType;

    public SCAVisitor(ComponentInfo componentType) {
        this.componentType = componentType;
    }

    public ComponentInfo getComponentType() {
        return componentType;
    }

    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces) {
    }

    public void visitSource(String string, String string1) {
    }

    public void visitOuterClass(String string, String string1, String string2) {
    }

    public AnnotationVisitor visitAnnotation(String string, boolean b) {
        return null;
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitInnerClass(String string, String string1, String string2, int i) {
    }

    public FieldVisitor visitField(int i, String string, String string1, String string2, Object object) {
        return null;
    }

    public MethodVisitor visitMethod(int i, String string, String string1, String string2, String[] strings) {
        return null;
    }

    public void visitEnd() {
    }
}
