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
package helloworld;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.sca.databinding.jaxb.JAXBTypeHelper;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.interfacedef.java.jaxws.GeneratedClassLoader;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSerializer.XmlSchemaSerializerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.Document;

import anotherpackage.BBean;
import anotherpackage.CBean;
import yetanotherpackage.DBean;

/**
 * Tests that the helloworld server is available
 */
@Ignore("Just some scratch code I don't want to loose just yet")
public class SchemaCorrectionTestCase{

    private Node node;

    @Before
	public void startServer() throws Exception {
        node = TuscanyRuntime.newInstance().createNode("default");
	}
    
    @Test
    public void testSchemaGen() throws JAXBException, IOException, TransformerException, Exception {                  
        ExtensionPointRegistry extensionPointRegistry = ((NodeImpl)node).getExtensionPointRegistry();
        JAXBTypeHelper typeHelper = new JAXBTypeHelper(extensionPointRegistry);
        JAXBContextHelper contextHelper = new JAXBContextHelper(extensionPointRegistry);
        
        //typeHelper.getSchemaDefinitions(factory, resolver, intf);
        
        Package pkg = CBean.class.getPackage();
        String pkgName = pkg.getName();
        Annotation[] pkgAnnotations = pkg.getAnnotations();
        boolean genPkgInfo = true;
        
        for (Annotation annotation : pkgAnnotations){
            if (annotation instanceof XmlSchema){
                XmlSchema schemaAnnotation = (XmlSchema)annotation;
                if (schemaAnnotation.namespace() != null){
                    genPkgInfo = false;
                    break;
                }
            }
        }
        
        JAXBContext context = null;
            
        if (genPkgInfo){
            System.out.println("There is no package info so generate one");
/*  Can gen the annotation but then what?             
            Class<?> genClass = generatePackageInfo(pkgName, "http://sometestsnamespace");
            
            Package pkgGen = aClass.getPackage();
            String pkgGenName = pkg.getName();
            Annotation[] pkgGenAnnotations = pkg.getAnnotations();
            XmlSchema schemaAnnotation = null;
            
            for (Annotation annotation : pkgGenAnnotations){
                if (annotation instanceof XmlSchema){
                    schemaAnnotation = (XmlSchema)annotation;
                }
            }
            
            pkgAnnotations = pkgGenAnnotations;
*/            
            
/*   Can't pass the generarted package into JAXB. It has to 
 *   be properly referenced as a package of a class you're trying 
 *   generate.          
            Class<?>[] classes = {aClass, genClass};
            context = contextHelper.createJAXBContext(classes);
*/         
        } else {
            System.out.println("There is package info");
        }
        
        Class<?>[] classes = {ABean.class, BBean.class, CBean.class, DBean.class};
        context = contextHelper.createJAXBContext(classes);
   
        DOMResolverImpl resolver = new DOMResolverImpl();
        context.generateSchema(resolver);     
        
        String toNamespace = null;
        String fromNamespace = null;
        Document toDocument = null;
        Document fromDocument = null;
        Map<String, Document> otherDocuments = new HashMap<String, Document>();
        
        Map<String, DOMResult> results = resolver.getResults();
        for (Map.Entry<String, DOMResult> entry : results.entrySet()) {
            System.out.println("\nPREMERGE NS: " + entry.getKey());
            if (entry.getKey().equals("")){
                fromNamespace = entry.getKey();
                fromDocument = (Document)entry.getValue().getNode();
            } else if (entry.getKey().equals("http://helloworld/")){
                toNamespace = entry.getKey();
                toDocument = (Document)entry.getValue().getNode();
            } else {
                otherDocuments.put(entry.getKey(), (Document)entry.getValue().getNode());
            }
            System.out.println("PREMERGE XSD: ");
            printDOM((Document)entry.getValue().getNode());
        }
        
        // merge no-namespace XSD into default namespace XSD
        System.out.println("\nPOSTMERGE");
        List<Document> mergedDocuments = mergeSchema(fromNamespace, fromDocument, toNamespace, toDocument, otherDocuments.values());
        
        for (Document mergedDocument : mergedDocuments){
            System.out.println("\n");
            printDOM(mergedDocument);
        }    
    }

	@After
	public void stopServer() throws Exception {

	}
	
    private static class DOMResolverImpl extends SchemaOutputResolver {
        private Map<String, DOMResult> results = new HashMap<String, DOMResult>();

        @Override
        public Result createOutput(String ns, String file) throws IOException {
            DOMResult result = new DOMResult();
            // TUSCANY-2498: Set the system id to "" so that the xsd:import doesn't produce 
            // an illegal schemaLocation attr 
            result.setSystemId("");
            results.put(ns, result);
            return result;
        }

        public Map<String, DOMResult> getResults() {
            return results;
        }
    }
    
    private void printDOM(Document document)throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(document);
        Result output = new StreamResult(System.out);
        transformer.transform(source, output);
    }
    
    public Class<?> generatePackageInfo(String packageName, String namespace) throws Exception{
        String className  = packageName + ".package-info";
        String internalClassName = className.replace('.', '/');

        PackageInfoGenerator pig = new PackageInfoGenerator();
        
        byte[] byteCode = pig.dump(internalClassName, namespace);
        
        GeneratedClassLoader classLoader = new GeneratedClassLoader(this.getClass().getClassLoader());
        return classLoader.getGeneratedClass(className, byteCode);
    }
    
    
    public class PackageInfoGenerator implements Opcodes {

        public byte[] dump (String internalClassName, String namespace) throws Exception {
            ClassWriter cw = new ClassWriter(0);
            FieldVisitor fv;
            MethodVisitor mv;
            AnnotationVisitor av0;
    
            cw.visit(V1_6, ACC_ABSTRACT + ACC_INTERFACE + ACC_SYNTHETIC, internalClassName, null, "java/lang/Object", null);
    
            cw.visitSource("package-info.java", null);
    

            av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlSchema;", true);
            av0.visit("namespace", namespace);
            av0.visitEnd();

            cw.visitEnd();
    
            return cw.toByteArray();
        }
    }
    
    private List<Document> mergeSchema(String fromNamespace, Document fromDoc, String toNamespace, Document toDoc, Collection<Document> relatedDocs) throws XmlSchemaSerializerException{
        // Read all the input DOMs into a schema collection so we can maniuplate them
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
        schemaCollection.read(fromDoc.getDocumentElement());
        schemaCollection.read(toDoc.getDocumentElement());
        
        for(Document doc : relatedDocs){
            schemaCollection.read(doc.getDocumentElement());
        }
        
        org.apache.ws.commons.schema.XmlSchema fromSchema = null;
        org.apache.ws.commons.schema.XmlSchema toSchema = null;
        List<org.apache.ws.commons.schema.XmlSchema> relatedSchema = new ArrayList<org.apache.ws.commons.schema.XmlSchema>();
        org.apache.ws.commons.schema.XmlSchema schemas[] = schemaCollection.getXmlSchemas();
        for (int i=1; i < schemas.length; i++){
            org.apache.ws.commons.schema.XmlSchema schema = schemas[i];
            if (schema.getTargetNamespace() == null){
                fromSchema = schema;
            } else if (schema.getTargetNamespace().equals(toNamespace)){
                toSchema = schema; 
                relatedSchema.add(schema);
            } else {
                relatedSchema.add(schema);
            }
        }
        
        // copy all the FROM items to the TO schema
        XmlSchemaObjectCollection fromItems = fromSchema.getItems();
        XmlSchemaObjectCollection toItems = toSchema.getItems();
       
        Iterator<XmlSchemaObject> iter = fromItems.getIterator();
        while(iter.hasNext()){
            // don't copy import for TO namespace
            XmlSchemaObject obj = iter.next();
            if (obj instanceof XmlSchemaImport &&
                ((XmlSchemaImport)obj).getNamespace().equals(toNamespace)){
                // do nothing
            } else {
                toItems.add(obj);
                // correct any references to the item just moved
                fixUpMovedTypeReferences(fromNamespace, toNamespace, obj, relatedSchema);
            }
        }
        
        // Create the list of resulting DOMs
        List<Document> resultDocs = new ArrayList<Document>();
        
        for (org.apache.ws.commons.schema.XmlSchema related : relatedSchema){
            resultDocs.add(related.getSchemaDocument());
        }
        
        return resultDocs;
    }
    
    public void fixUpMovedTypeReferences(String fromNamespace, String toNamespace, XmlSchemaObject fixUpObj, List<org.apache.ws.commons.schema.XmlSchema> relatedSchema){
        
        if (!(fixUpObj instanceof XmlSchemaComplexType)){
            return;
        }
        
        for (org.apache.ws.commons.schema.XmlSchema schema : relatedSchema){
            int importRemoveIndex = -1;
            for (int i = 0; i < schema.getItems().getCount(); i++){
                XmlSchemaObject obj = schema.getItems().getItem(i);
                
                // if this is not the TO schema then fix up all references
                // to items moved to the TO schema
                if(!schema.getTargetNamespace().equals(toNamespace)){
                    processXMLSchemaObject(toNamespace, obj, fixUpObj);
                }
                
                // remove FROM imports
                if (obj instanceof XmlSchemaImport &&
                    ((XmlSchemaImport)obj).getNamespace().equals(fromNamespace)){
                    importRemoveIndex = i;
                }
            }

            if (importRemoveIndex >= 0){
                schema.getItems().removeAt(importRemoveIndex);
            }
        }
    }
    
    public void processXMLSchemaObject(String toNamespace, XmlSchemaObject obj,  XmlSchemaObject fixUpObj){
        if (obj instanceof XmlSchemaComplexType){
            processXMLSchemaObject(toNamespace, ((XmlSchemaComplexType)obj).getParticle(), fixUpObj);
        } else if (obj instanceof XmlSchemaElement){
            XmlSchemaElement element = (XmlSchemaElement)obj;
            if(element.getSchemaType() == fixUpObj){
                QName name = element.getSchemaTypeName();
                QName newName = new QName(toNamespace, name.getLocalPart());
                element.setSchemaTypeName(newName);
            }
            ((XmlSchemaElement)obj).getSchemaType();
        } else if (obj instanceof XmlSchemaGroupBase){
            XmlSchemaObjectCollection items = ((XmlSchemaGroupBase)obj).getItems();
            Iterator<XmlSchemaObject> iter = items.getIterator();
            while(iter.hasNext()){
                processXMLSchemaObject(toNamespace, iter.next(), fixUpObj);
            }
        }
    }
    
/*
    private List<Document> mergeSchema(String fromNamespace, Document fromDoc, String toNamespace, Document toDoc, Collection<Document> relatedDocs) throws XmlSchemaSerializerException{
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
        schemaCollection.read(fromDoc.getDocumentElement());
        schemaCollection.read(toDoc.getDocumentElement());
        
        for(Document doc : relatedDocs){
            schemaCollection.read(doc.getDocumentElement());
        }
        
        org.apache.ws.commons.schema.XmlSchema fromSchema = null;
        org.apache.ws.commons.schema.XmlSchema toSchema = null;
        List<org.apache.ws.commons.schema.XmlSchema> relatedSchema = new ArrayList<org.apache.ws.commons.schema.XmlSchema>();
        org.apache.ws.commons.schema.XmlSchema schemas[] = schemaCollection.getXmlSchemas();
        for (int i=1; i < schemas.length; i++){
            org.apache.ws.commons.schema.XmlSchema schema = schemas[i];
            if (schema.getTargetNamespace() == null){
                fromSchema = schema;
            } else if (schema.getTargetNamespace().equals(toNamespace)){
                toSchema = schema;   
            } else {
                relatedSchema.add(schema);
            }
        }
        
        // add all the FROM items to the TO schema
        XmlSchemaObjectCollection fromItems = fromSchema.getItems();
        XmlSchemaObjectCollection toItems = toSchema.getItems();
       
        Iterator<XmlSchemaObject> iter = fromItems.getIterator();
        while(iter.hasNext()){
            // don't copy import for TO namespace
            XmlSchemaObject obj = iter.next();
            if (obj instanceof XmlSchemaImport &&
                ((XmlSchemaImport)obj).getNamespace().equals(toNamespace)){
                // do nothing
            } else {
                toItems.add(obj);
                fixUpMovedTypeReferences(fromNamespace, toNamespace, obj, relatedSchema);
            }
        }
        
        // remove the FROM namespace from TO schema includes list
        XmlSchemaObjectCollection toIncludes = toSchema.getIncludes();
        XmlSchemaImport schemaImport = null;
        iter = toIncludes.getIterator();
        while(iter.hasNext()){
            XmlSchemaImport tmpImport = (XmlSchemaImport)iter.next();
            if (tmpImport.getNamespace().equals(fromNamespace)){
                schemaImport = tmpImport;
                break;
            }
        }
        
        if (schemaImport != null){
            toIncludes.remove(schemaImport);
            toItems.remove(schemaImport);
        }       
        
        List<Document> resultDocs = new ArrayList<Document>();
        resultDocs.add(toSchema.getSchemaDocument());
        
        for (org.apache.ws.commons.schema.XmlSchema related : relatedSchema){
            resultDocs.add(related.getSchemaDocument());
        }
        
        return resultDocs;
    }
    
    public void fixUpMovedTypeReferences(String fromNamespace, String toNamespace, XmlSchemaObject fixUpObj, List<org.apache.ws.commons.schema.XmlSchema> relatedSchema){
        for (org.apache.ws.commons.schema.XmlSchema schema : relatedSchema){
            XmlSchemaObjectCollection items = schema.getItems();
            Iterator<XmlSchemaObject> iter = items.getIterator();
            XmlSchemaImport importToRemove = null;
            while(iter.hasNext()){
                XmlSchemaObject obj = iter.next();
                processXMLSchemaObject(toNamespace, obj, fixUpObj);
                
                // remote FROM imports
                if (obj instanceof XmlSchemaImport &&
                   ((XmlSchemaImport)obj).getNamespace().equals(fromNamespace)){
                    importToRemove = (XmlSchemaImport)obj;
                }
            }
            
            if (importToRemove != null){
                items.remove(importToRemove);
                schema.getIncludes().remove(importToRemove);
            }
        }
    }
    
    public void processXMLSchemaObject(String toNamespace, XmlSchemaObject obj,  XmlSchemaObject fixUpObj){
        if (obj instanceof XmlSchemaComplexType){
            processXMLSchemaObject(toNamespace, ((XmlSchemaComplexType)obj).getParticle(), fixUpObj);
        } else if (obj instanceof XmlSchemaElement){
            XmlSchemaElement element = (XmlSchemaElement)obj;
            if(element.getSchemaType() == fixUpObj){
                QName name = element.getSchemaTypeName();
                QName newName = new QName(toNamespace, name.getLocalPart());
                element.setSchemaTypeName(newName);
            }
            ((XmlSchemaElement)obj).getSchemaType();
        } else if (obj instanceof XmlSchemaGroupBase){
            XmlSchemaObjectCollection items = ((XmlSchemaGroupBase)obj).getItems();
            Iterator<XmlSchemaObject> iter = items.getIterator();
            while(iter.hasNext()){
                processXMLSchemaObject(toNamespace, iter.next(), fixUpObj);
            }
        }
    }
 */
    
}