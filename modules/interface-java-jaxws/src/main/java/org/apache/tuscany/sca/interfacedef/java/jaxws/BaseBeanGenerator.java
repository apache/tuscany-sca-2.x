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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.Holder;

import org.apache.tuscany.sca.databinding.jaxb.XMLAdapterExtensionPoint;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class BaseBeanGenerator implements Opcodes {
    private static final Map<String, String> COLLECTION_CLASSES = new HashMap<String, String>();
    
    static {
        COLLECTION_CLASSES.put("Ljava/util/Collection;", "java/util/ArrayList");
        COLLECTION_CLASSES.put("Ljava/util/List;", "java/util/ArrayList");
        COLLECTION_CLASSES.put("Ljava/util/Set;", "java/util/HashSet");
        COLLECTION_CLASSES.put("Ljava/util/Queue;", "java/util/LinkedList");
    }
    private final static Class[] KNOWN_JAXB_ANNOTATIONS =
        {XmlAttachmentRef.class, 
         XmlMimeType.class, 
         XmlJavaTypeAdapter.class, 
         XmlList.class};
    
    private static final Map<String, String> JAVA_KEYWORDS = new HashMap<String, String>();

    static {
        JAVA_KEYWORDS.put("abstract", "_abstract");
        JAVA_KEYWORDS.put("assert", "_assert");
        JAVA_KEYWORDS.put("boolean", "_boolean");
        JAVA_KEYWORDS.put("break", "_break");
        JAVA_KEYWORDS.put("byte", "_byte");
        JAVA_KEYWORDS.put("case", "_case");
        JAVA_KEYWORDS.put("catch", "_catch");
        JAVA_KEYWORDS.put("char", "_char");
        JAVA_KEYWORDS.put("class", "_class");
        JAVA_KEYWORDS.put("const", "_const");
        JAVA_KEYWORDS.put("continue", "_continue");
        JAVA_KEYWORDS.put("default", "_default");
        JAVA_KEYWORDS.put("do", "_do");
        JAVA_KEYWORDS.put("double", "_double");
        JAVA_KEYWORDS.put("else", "_else");
        JAVA_KEYWORDS.put("extends", "_extends");
        JAVA_KEYWORDS.put("false", "_false");
        JAVA_KEYWORDS.put("final", "_final");
        JAVA_KEYWORDS.put("finally", "_finally");
        JAVA_KEYWORDS.put("float", "_float");
        JAVA_KEYWORDS.put("for", "_for");
        JAVA_KEYWORDS.put("goto", "_goto");
        JAVA_KEYWORDS.put("if", "_if");
        JAVA_KEYWORDS.put("implements", "_implements");
        JAVA_KEYWORDS.put("import", "_import");
        JAVA_KEYWORDS.put("instanceof", "_instanceof");
        JAVA_KEYWORDS.put("int", "_int");
        JAVA_KEYWORDS.put("interface", "_interface");
        JAVA_KEYWORDS.put("long", "_long");
        JAVA_KEYWORDS.put("native", "_native");
        JAVA_KEYWORDS.put("new", "_new");
        JAVA_KEYWORDS.put("null", "_null");
        JAVA_KEYWORDS.put("package", "_package");
        JAVA_KEYWORDS.put("private", "_private");
        JAVA_KEYWORDS.put("protected", "_protected");
        JAVA_KEYWORDS.put("public", "_public");
        JAVA_KEYWORDS.put("return", "_return");
        JAVA_KEYWORDS.put("short", "_short");
        JAVA_KEYWORDS.put("static", "_static");
        JAVA_KEYWORDS.put("strictfp", "_strictfp");
        JAVA_KEYWORDS.put("super", "_super");
        JAVA_KEYWORDS.put("switch", "_switch");
        JAVA_KEYWORDS.put("synchronized", "_synchronized");
        JAVA_KEYWORDS.put("this", "_this");
        JAVA_KEYWORDS.put("throw", "_throw");
        JAVA_KEYWORDS.put("throws", "_throws");
        JAVA_KEYWORDS.put("transient", "_transient");
        JAVA_KEYWORDS.put("true", "_true");
        JAVA_KEYWORDS.put("try", "_try");
        JAVA_KEYWORDS.put("void", "_void");
        JAVA_KEYWORDS.put("volatile", "_volatile");
        JAVA_KEYWORDS.put("while", "_while");
        JAVA_KEYWORDS.put("enum", "_enum");
    }

    protected static final Map<Object, WeakReference<Class<?>>> generatedClasses =
        Collections.synchronizedMap(new WeakHashMap<Object, WeakReference<Class<?>>>());

    protected XMLAdapterExtensionPoint xmlAdapters;

    public byte[] defineClass(ClassWriter cw,
                              String classDescriptor,
                              String classSignature,
                              String namespace,
                              String name,
                              BeanProperty[] properties) {
        // Declare the class
        declareClass(cw, classDescriptor);

        // Compute the propOrder
        String[] propOrder = null;
        if (properties != null && properties.length > 0) {
            int size = properties.length;
            propOrder = new String[size];
            for (int i = 0; i < size; i++) {
                propOrder[i] = getFieldName(properties[i].getName());
            }
        }
        // Annotate the class
        annotateClass(cw, name, namespace, propOrder);

        // Declare the default constructor
        declareConstructor(cw, classSignature);
        if (properties != null) {
            for (BeanProperty p : properties) {
                boolean isElement = p.isElement() && (!Map.class.isAssignableFrom(p.getType()));
                String xmlAdapterClassSignature = null;
                if (xmlAdapters != null) {
                    Class<?> adapterClass = xmlAdapters.getAdapter(p.getType());
                    if (adapterClass != null) {
                        xmlAdapterClassSignature = CodeGenerationHelper.getSignature(adapterClass);
                    }
                }
                declareProperty(cw, classDescriptor, classSignature, p.getName(), p.getSignature(), p
                    .getGenericSignature(), isElement, p.isNillable(), xmlAdapterClassSignature, p.getJaxbAnnotaions());
            }
        }

        // Close the generation
        cw.visitEnd();
        return cw.toByteArray();
    }

    protected static boolean isHolder(java.lang.reflect.Type type) {
        if (type instanceof ParameterizedType) {
            Class<?> cls = CodeGenerationHelper.getErasure(type);
            return cls == Holder.class;
        }
        return false;
    }

    protected static java.lang.reflect.Type getHolderValueType(java.lang.reflect.Type paramType) {
        if (paramType instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)paramType;
            Class<?> cls = CodeGenerationHelper.getErasure(p);
            if (cls == Holder.class) {
                return p.getActualTypeArguments()[0];
            }
        }
        return paramType;
    }

    protected void declareProperty(ClassWriter cw,
                                   String classDescriptor,
                                   String classSignature,
                                   String propName,
                                   String propClassSignature,
                                   String propTypeSignature,
                                   boolean isElement,
                                   boolean isNillable,
                                   String xmlAdapterClassSignature,
                                   List<Annotation> jaxbAnnotations) {
        if (propClassSignature.equals(propTypeSignature)) {
            propTypeSignature = null;
        }
        declareField(cw,
                     propName,
                     propClassSignature,
                     propTypeSignature,
                     isElement,
                     isNillable,
                     xmlAdapterClassSignature,
                     jaxbAnnotations);
        decalreGetter(cw, classDescriptor, classSignature, propName, propClassSignature, propTypeSignature);
        declareSetter(cw, classDescriptor, classSignature, propName, propClassSignature, propTypeSignature);
    }

    protected String getFieldName(String propName) {
        String name = JAVA_KEYWORDS.get(propName);
        return name != null ? name : propName;
    }

    protected void declareField(ClassWriter cw,
                                String propName,
                                String propClassSignature,
                                String propTypeSignature,
                                boolean isElement,
                                boolean isNillable,
                                String xmlAdapterClassSignature,
                                List<Annotation> jaxbAnnotations) {
        FieldVisitor fv;
        AnnotationVisitor av0;
        fv = cw.visitField(ACC_PROTECTED, getFieldName(propName), propClassSignature, propTypeSignature, null);

        // For Map property, we cannot have the XmlElement annotation
        if (isElement && xmlAdapterClassSignature == null) {
            av0 = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
            av0.visit("name", propName);
            av0.visit("namespace", "");
            // TUSCANY-3283 - force not nillable if it isn't
            if (isNillable) {
                av0.visit("nillable", Boolean.TRUE);
            } else {
                av0.visit("nillable", Boolean.FALSE);
            }
            // FIXME:
            // av0.visit("required", Boolean.FALSE);
            av0.visitEnd();
        }

        if (xmlAdapterClassSignature != null) {
            av0 = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlAnyElement;", true);
            av0.visit("lax", Boolean.TRUE);
            av0.visitEnd();
            av0 = fv.visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
            av0.visit("value", org.objectweb.asm.Type.getType(xmlAdapterClassSignature));
            av0.visitEnd();
        }

        for (Annotation ann : jaxbAnnotations) {
            if (ann instanceof XmlMimeType) {
                AnnotationVisitor mime = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlMimeType;", true);
                mime.visit("value", ((XmlMimeType)ann).value());
                mime.visitEnd();
            } else if (ann instanceof XmlJavaTypeAdapter) {
                AnnotationVisitor ada = fv.visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
                ada.visit("value", org.objectweb.asm.Type.getType(((XmlJavaTypeAdapter)ann).value()));
                ada.visit("type", org.objectweb.asm.Type.getType(((XmlJavaTypeAdapter)ann).type()));
                ada.visitEnd();
            } else if (ann instanceof XmlAttachmentRef) {
                AnnotationVisitor att = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlAttachmentRef;", true);
                att.visitEnd();
            } else if (ann instanceof XmlList) {
                AnnotationVisitor list = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlList;", true);
                list.visitEnd();
            }
        }

        fv.visitEnd();
    }

    protected void declareSetter(ClassWriter cw,
                                 String classDescriptor,
                                 String classSignature,
                                 String propName,
                                 String propClassSignature,
                                 String propTypeSignature) {
        if ("Ljava/util/List;".equals(propClassSignature)) {
            return;
        }
        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC,
                           "set" + capitalize(propName),
                           "(" + propClassSignature + ")V",
                           propTypeSignature == null ? null : "(" + propTypeSignature + ")V",
                           null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        // mv.visitLineNumber(57, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(CodeGenerationHelper.getLoadOPCode(propClassSignature), 1);
        mv.visitFieldInsn(PUTFIELD, classDescriptor, getFieldName(propName), propClassSignature);
        Label l1 = new Label();
        mv.visitLabel(l1);
        // mv.visitLineNumber(58, l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", classSignature, null, l0, l2, 0);
        mv.visitLocalVariable(getFieldName(propName), propClassSignature, propTypeSignature, l0, l2, 1);
        mv.visitMaxs(3, 3);
        mv.visitEnd();

    }

    protected void decalreGetter(ClassWriter cw,
                                 String classDescriptor,
                                 String classSignature,
                                 String propName,
                                 String propClassSignature,
                                 String propTypeSignature) {
        String collectionImplClass = COLLECTION_CLASSES.get(propClassSignature);
        if (collectionImplClass != null) {
            decalreCollectionGetter(cw,
                                    classDescriptor,
                                    classSignature,
                                    propName,
                                    propClassSignature,
                                    propTypeSignature,
                                    collectionImplClass);
            return;
        }

        String getterName = ("Z".equals(propClassSignature) ? "is" : "get") + capitalize(propName);
        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC, getterName, "()" + propClassSignature, propTypeSignature == null ? null
                : "()" + propTypeSignature, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        // mv.visitLineNumber(48, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classDescriptor, getFieldName(propName), propClassSignature);
        mv.visitInsn(CodeGenerationHelper.getReturnOPCode(propClassSignature));
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", classSignature, null, l0, l1, 0);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    protected void decalreCollectionGetter(ClassWriter cw,
                                           String classDescriptor,
                                           String classSignature,
                                           String propName,
                                           String propClassSignature,
                                           String propTypeSignature,
                                           String collectionImplClass) {
        String getterName = "get" + capitalize(propName);
        String fieldName = getFieldName(propName);
        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC, getterName, "()" + propClassSignature, propTypeSignature == null ? null
                : "()" + propTypeSignature, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(63, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classDescriptor, fieldName, propClassSignature);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(64, l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, collectionImplClass);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, collectionImplClass, "<init>", "()V");
        mv.visitFieldInsn(PUTFIELD, classDescriptor, fieldName, propClassSignature);
        mv.visitLabel(l1);
        mv.visitLineNumber(66, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classDescriptor, fieldName, propClassSignature);
        mv.visitInsn(ARETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("this", classSignature, null, l0, l3, 0);
        mv.visitMaxs(3, 1);
        mv.visitEnd();
    }

    protected static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        } else {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }

    protected void declareConstructor(ClassWriter cw, String classSignature) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        // mv.visitLineNumber(37, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", classSignature, null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    protected void declareClass(ClassWriter cw, String classDescriptor) {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classDescriptor, null, "java/lang/Object", null);
    }

    protected void annotateClass(ClassWriter cw, String name, String namespace, String[] propOrder) {
        AnnotationVisitor av0;
        // @XmlRootElement
        av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
        av0.visit("name", name);
        av0.visit("namespace", namespace);
        av0.visitEnd();
        // @XmlAccessorType
        av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlAccessorType;", true);
        av0.visitEnum("value", "Ljavax/xml/bind/annotation/XmlAccessType;", "FIELD");
        av0.visitEnd();
        // @XmlType
        av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
        av0.visit("name", name);
        av0.visit("namespace", namespace);
        if (propOrder != null) {
            AnnotationVisitor pv = av0.visitArray("propOrder");
            for (String p : propOrder) {
                pv.visit(null, p);
            }
            pv.visitEnd();
        }
        av0.visitEnd();
    }

    public Class<?> generate(String classDescriptor,
                             String classSignature,
                             String namespace,
                             String name,
                             BeanProperty[] properties,
                             GeneratedClassLoader classLoader) {
    	
    	// The reflection code here allows for toleration of older versions of ASM. 
    	ClassWriter cw;
    	try {   		
    		Constructor<ClassWriter> c = ClassWriter.class.getConstructor(new Class[] {int.class});
    		Field f = ClassWriter.class.getField("COMPUTE_MAXS");
    		cw = c.newInstance(f.get(null)); 
    	} catch ( Exception ex ) {
    		try {
    			Constructor<ClassWriter> c = ClassWriter.class.getConstructor(new Class[] {boolean.class});
    			cw = c.newInstance(true);
    		} catch ( Exception ex2 ) {
    			throw new IllegalArgumentException(ex2);
    		}
    		
    	} 
		
        byte[] byteCode = defineClass(cw, classDescriptor, classSignature, namespace, name, properties);
        String className = classDescriptor.replace('/', '.');
        Class<?> generated = classLoader.getGeneratedClass(className, byteCode);
        return generated;
    }

    public static class BeanProperty {
        private Class<?> type;
        private String namespace;
        private String name;
        private String signature;
        private String genericSignature;
        private List<Annotation> jaxbAnnotaions = new ArrayList<Annotation>();
        private boolean element;
        private boolean nillable;

        public BeanProperty(String namespace, String name, Class<?> javaClass, Type type, boolean isElement) {
            super();
            this.namespace = namespace;
            this.name = name;
            this.signature = CodeGenerationHelper.getJAXWSSignature(javaClass);
            this.type = javaClass;
            this.genericSignature = CodeGenerationHelper.getJAXWSSignature(type);
            this.element = isElement; 
            // FIXME: How to test nillable?
            // this.nillable = (type instanceof GenericArrayType) || Collection.class.isAssignableFrom(javaClass) || javaClass.isArray();
            // TUSCANY-2389: Set the nillable consistent with what wsgen produces
            this.nillable = javaClass.isArray();
        }

        public String getName() {
            return name;
        }

        public String getSignature() {
            return signature;
        }

        public String getGenericSignature() {
            return genericSignature;
        }

        public Class<?> getType() {
            return type;
        }

        public List<Annotation> getJaxbAnnotaions() {
            return jaxbAnnotaions;
        }

        public String getNamespace() {
            return namespace;
        }

        public boolean isElement() {
            return element;
        }

        public boolean isNillable() {
            return nillable;
        }
    }

    public XMLAdapterExtensionPoint getXmlAdapters() {
        return xmlAdapters;
    }

    public void setXmlAdapters(XMLAdapterExtensionPoint xmlAdapters) {
        this.xmlAdapters = xmlAdapters;
    }

    protected static <T extends Annotation> T findAnnotation(Annotation[] anns, Class<T> annotationClass) {
        for (Annotation a : anns) {
            if (a.annotationType() == annotationClass) {
                return annotationClass.cast(a);
            }
        }
        return null;
    }

    protected static List<Annotation> findJAXBAnnotations(Annotation[] anns) {
        List<Annotation> jaxbAnnotation = new ArrayList<Annotation>();
        for (Class<? extends Annotation> c : KNOWN_JAXB_ANNOTATIONS) {
            Annotation a = findAnnotation(anns, c);
            if (a != null) {
                jaxbAnnotation.add(a);
            }
        }
        return jaxbAnnotation;
    }

    protected List<Annotation> findJAXBAnnotations(Method method) {
        List<Annotation> anns = new ArrayList<Annotation>();
        for (Class<? extends Annotation> c : KNOWN_JAXB_ANNOTATIONS) {
            Annotation ann = method.getAnnotation(c);
            if (ann != null) {
                anns.add(ann);
            }
        }
        return anns;
    }

}
