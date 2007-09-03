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

package org.apache.tuscany.sca.policy.xml;

import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.impl.BindingTypeImpl;
import org.apache.tuscany.sca.policy.impl.ImplementationTypeImpl;


/* 
 * Processor for handling xml models of ExtensionType meta data definitions
 */
public abstract class IntentAttachPointTypeProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<IntentAttachPointType>, PolicyConstants {

    private IntentAttachPointTypeFactory attachPointTypeFactory;
    private PolicyFactory policyFactory; 
    

    public IntentAttachPointTypeProcessor(PolicyFactory policyFactory, IntentAttachPointTypeFactory attachPointTypeFactory, StAXArtifactProcessor<Object> extensionProcessor) {
        this.policyFactory = policyFactory;
        this.attachPointTypeFactory = attachPointTypeFactory;
    }
    
    public IntentAttachPointType read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        QName type = getQName(reader, TYPE);
        
        if ( type != null ) {
            if ( type.getLocalPart().startsWith(BINDING) ) {
                IntentAttachPointType bindingType = attachPointTypeFactory.createBindingType();
                bindingType.setName(type);
                
                readAlwaysProvidedIntents(bindingType, reader);
                readMayProvideIntents(bindingType, reader);
                return bindingType; 
            } else if ( type.getLocalPart().startsWith(IMPLEMENTATION) ) {
                IntentAttachPointType implType = attachPointTypeFactory.createImplementationType();
                implType.setName(type);
                
                readAlwaysProvidedIntents(implType, reader);
                readMayProvideIntents(implType, reader);
                return implType;
            } else {
                throw new ContributionReadException("Unrecognized IntentAttachPointType - " + type);
            }
        } else { 
            throw new ContributionReadException("Required attribute '" + TYPE + 
                                                "' missing from BindingType Definition");
        }
    }

    private void readAlwaysProvidedIntents(IntentAttachPointType extnType, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, ALWAYS_PROVIDES);
        if (value != null) {
            List<Intent> alwaysProvided = extnType.getAlwaysProvidedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                alwaysProvided.add(intent);
            }
        }
    }
    
    private void readMayProvideIntents(IntentAttachPointType extnType, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, MAY_PROVIDE);
        if (value != null) {
            List<Intent> mayProvide = extnType.getMayProvideIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                mayProvide.add(intent);
            }
        }
    }
  
    public void write(IntentAttachPointType extnType, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        // Write an <sca:bindingType or sca:implementationType>
        if ( extnType instanceof BindingTypeImpl ) {
            writer.writeStartElement(SCA10_NS, BINDING_TYPE);
        } else if ( extnType instanceof ImplementationTypeImpl ) {
            writer.writeStartElement(SCA10_NS, IMPLEMENTATION_TYPE);
        }
        
        writeAlwaysProvidesIntentsAttribute(extnType, writer);
        writeMayProvideIntentsAttribute(extnType, writer);
        
        writer.writeEndElement();
    }
    
    private void writeMayProvideIntentsAttribute(IntentAttachPointType extnType, XMLStreamWriter writer) throws XMLStreamException {
        StringBuffer sb  = new StringBuffer();
        for ( Intent intent : extnType.getMayProvideIntents() ) {
            writer.writeNamespace(intent.getName().getPrefix(), intent.getName().getNamespaceURI());
            sb.append(intent.getName().getPrefix() + COLON + intent.getName().getLocalPart());
            sb.append(WHITE_SPACE);
        }
        
        if ( sb.length() > 0 ) {
            writer.writeAttribute(MAY_PROVIDE, sb.toString());
        }
    }
    
    private void writeAlwaysProvidesIntentsAttribute(IntentAttachPointType extnType, XMLStreamWriter writer) throws XMLStreamException {
        StringBuffer sb  = new StringBuffer();
        for ( Intent intent : extnType.getAlwaysProvidedIntents() ) {
            writer.writeNamespace(intent.getName().getPrefix(), intent.getName().getNamespaceURI());
            sb.append(intent.getName().getPrefix() + COLON + intent.getName().getLocalPart());
            sb.append(WHITE_SPACE);
        }
        
        if ( sb.length() > 0 ) {
            writer.writeAttribute(ALWAYS_PROVIDES, sb.toString());
            
        }
    }
    
    private void resolveExtensionType(IntentAttachPointType extnType, ModelResolver resolver) throws ContributionResolveException {
        //FIXME: need to resolve the binding and implementations across the assembly model
        extnType.setUnresolved(false);
    }
    
    public void resolve(IntentAttachPointType extnType, ModelResolver resolver) throws ContributionResolveException {
        resolveExtensionType(extnType, resolver);

        if ( !extnType.isUnresolved() ) {
             resolver.addModel(extnType);
        }
    }

    //FIXME This method is never used
//    private void resolveAlwaysProvidedIntents(IntentAttachPointType extnType, ModelResolver resolver) throws ContributionResolveException {
//        boolean isUnresolved = false;
//        if (extnType != null && extnType.isUnresolved()) {
//            //resolve alwaysProvided Intents
//            List<Intent> alwaysProvided = new ArrayList<Intent>(); 
//            for ( Intent providedIntent : extnType.getAlwaysProvidedIntents() ) {
//                if ( providedIntent.isUnresolved() ) {
//                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
//                    alwaysProvided.add(providedIntent);
//                    if (providedIntent.isUnresolved()) {
//                        isUnresolved = true;
//                    }
//                }
//            }
//            extnType.getAlwaysProvidedIntents().clear();
//            extnType.getAlwaysProvidedIntents().addAll(alwaysProvided);
//        }
//        extnType.setUnresolved(isUnresolved);
//    }

    //FIXME This method is never used
//    private void resolveMayProvideIntents(IntentAttachPointType extnType, ModelResolver resolver) throws ContributionResolveException {
//        boolean isUnresolved = false;
//        if (extnType != null && extnType.isUnresolved()) {
//            //resolve may provide Intents
//            List<Intent> mayProvide = new ArrayList<Intent>(); 
//            for ( Intent providedIntent : extnType.getMayProvideIntents() ) {
//                if ( providedIntent.isUnresolved() ) {
//                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
//                    mayProvide.add(providedIntent);
//                    if (providedIntent.isUnresolved()) {
//                        isUnresolved = true;
//                    }
//                }
//            }
//            extnType.getAlwaysProvidedIntents().clear();
//            extnType.getAlwaysProvidedIntents().addAll(mayProvide);
//        }
//        extnType.setUnresolved(isUnresolved);
//    }
    
    public Class<IntentAttachPointType> getModelType() {
        return IntentAttachPointType.class;
    }
}
