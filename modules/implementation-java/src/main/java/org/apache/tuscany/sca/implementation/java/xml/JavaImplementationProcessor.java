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

package org.apache.tuscany.sca.implementation.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static org.apache.tuscany.sca.implementation.java.xml.JavaImplementationConstants.CLASS;
import static org.apache.tuscany.sca.implementation.java.xml.JavaImplementationConstants.IMPLEMENTATION_JAVA;
import static org.apache.tuscany.sca.implementation.java.xml.JavaImplementationConstants.IMPLEMENTATION_JAVA_QNAME;
import static org.apache.tuscany.sca.implementation.java.xml.JavaImplementationConstants.SCA11_NS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.PolicySubjectProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 *
 * @version $Rev$ $Date$
 */
public class JavaImplementationProcessor implements StAXArtifactProcessor<JavaImplementation> {

    private JavaImplementationFactory javaFactory;
    private AssemblyFactory assemblyFactory;
    private PolicyFactory policyFactory;
    private PolicySubjectProcessor policyProcessor;
    private Monitor monitor;

    public JavaImplementationProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.javaFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        this.policyProcessor = new PolicySubjectProcessor(policyFactory);
        this.monitor = monitor;
    }

    /**
     * Report a error.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "impl-javaxml-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "impl-javaxml-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }

    public JavaImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        // Read an <implementation.java>
        JavaImplementation javaImplementation = javaFactory.createJavaImplementation();

        ExtensionType implType = policyFactory.createImplementationType();
        implType.setType(getArtifactType());
        implType.setUnresolved(true);
        javaImplementation.setExtensionType(implType);

        javaImplementation.setUnresolved(true);
        javaImplementation.setName(reader.getAttributeValue(null, CLASS));

        // Read policies
        policyProcessor.readPolicies(javaImplementation, reader);

        // read operation elements if exists or skip unto end element
        int event;
        while (reader.hasNext()) {
            event = reader.next();
            if (event == END_ELEMENT && IMPLEMENTATION_JAVA_QNAME.equals(reader.getName())) {
                break;
            }
        }
        return javaImplementation;
    }

    public void write(JavaImplementation javaImplementation, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {

        // Write an <implementation.java>
        policyProcessor.writePolicyPrefixes(javaImplementation, writer);
        writer.writeStartElement(SCA11_NS, IMPLEMENTATION_JAVA);
        policyProcessor.writePolicyAttributes(javaImplementation, writer);

        if (javaImplementation.getName() != null) {
            writer.writeAttribute(CLASS, javaImplementation.getName());
        }

        writer.writeEndElement();
    }

    public void resolve(JavaImplementation javaImplementation, ModelResolver resolver)
        throws ContributionResolveException {

    	try {
	        ClassReference classReference = new ClassReference(javaImplementation.getName());
	        classReference = resolver.resolveModel(ClassReference.class, classReference);
	        Class<?> javaClass = classReference.getJavaClass();
	        if (javaClass == null) {
	            error("ClassNotFoundException", resolver, javaImplementation.getName());
	            //throw new ContributionResolveException(new ClassNotFoundException(javaImplementation.getName()));
	            return;
	        }

	        javaImplementation.setJavaClass(javaClass);

	        try {
	            javaFactory.createJavaImplementation(javaImplementation, javaImplementation.getJavaClass());
	        } catch (IntrospectionException e) {
	            ContributionResolveException ce = new ContributionResolveException(e);
	            error("ContributionResolveException", javaFactory, ce);
	            //throw ce;
	            return;
	        }

	        javaImplementation.setUnresolved(false);
	        mergeComponentType(resolver, javaImplementation);

	        // FIXME the introspector should always create at least one service
	        if (javaImplementation.getServices().isEmpty()) {
	            javaImplementation.getServices().add(assemblyFactory.createService());
	        }
    	} catch (Throwable e ) {
    		throw new ContributionResolveException( "Resolving Java implementation: " + javaImplementation.getName(), e);
    	} // end try
    } // end method

    private JavaElementImpl getMemeber(JavaImplementation impl, String name, Class<?> type) {
        String setter = JavaIntrospectionHelper.toSetter(name);
        try {
            Method method = impl.getJavaClass().getDeclaredMethod(setter, type);
            int mod = method.getModifiers();
            if ((Modifier.isPublic(mod) || Modifier.isProtected(mod)) && (!Modifier.isStatic(mod))) {
                return new JavaElementImpl(method, 0);
            }
        } catch (NoSuchMethodException e) {
            Field field;
            try {
                field = impl.getJavaClass().getDeclaredField(name);
                int mod = field.getModifiers();
                if ((Modifier.isPublic(mod) || Modifier.isProtected(mod)) && (!Modifier.isStatic(mod))) {
                    return new JavaElementImpl(field);
                }
            } catch (NoSuchFieldException e1) {
                // Ignore
            }
        }
        return null;
    }

    /**
     * Merge the componentType from introspection and external file
     * @param resolver
     * @param impl
     */
    private void mergeComponentType(ModelResolver resolver, JavaImplementation impl) {
        // FIXME: Need to clarify how to merge
        ComponentType componentType = getComponentType(resolver, impl);
        if (componentType != null && !componentType.isUnresolved()) {
            Map<String, Reference> refMap = new HashMap<String, Reference>();
            for (Reference ref : impl.getReferences()) {
                refMap.put(ref.getName(), ref);
            }
            for (Reference reference : componentType.getReferences()) {
                refMap.put(reference.getName(), reference);
            }
            impl.getReferences().clear();
            impl.getReferences().addAll(refMap.values());

            // Try to match references by type
            Map<String, JavaElementImpl> refMembers = impl.getReferenceMembers();
            for (Reference ref : impl.getReferences()) {
                if (ref.getInterfaceContract() != null) {
                    Interface i = ref.getInterfaceContract().getInterface();
                    if (i instanceof JavaInterface) {
                        Class<?> type = ((JavaInterface)i).getJavaClass();
                        if (!refMembers.containsKey(ref.getName())) {
                            JavaElementImpl e = getMemeber(impl, ref.getName(), type);
                            if (e != null) {
                                refMembers.put(ref.getName(), e);
                            }
                        }
                    }
                }
            }

            Map<String, Service> serviceMap = new HashMap<String, Service>();
            for (Service svc : impl.getServices()) {
                serviceMap.put(svc.getName(), svc);
            }
            for (Service service : componentType.getServices()) {
                serviceMap.put(service.getName(), service);
            }
            impl.getServices().clear();
            impl.getServices().addAll(serviceMap.values());

            Map<String, Property> propMap = new HashMap<String, Property>();
            for (Property prop : impl.getProperties()) {
                propMap.put(prop.getName(), prop);
            }
            for (Property property : componentType.getProperties()) {
                propMap.put(property.getName(), property);
            }
            impl.getProperties().clear();
            impl.getProperties().addAll(propMap.values());

            if (componentType.getConstrainingType() != null) {
                impl.setConstrainingType(componentType.getConstrainingType());
            }
        }
    }

    private ComponentType getComponentType(ModelResolver resolver, JavaImplementation impl) {
        String className = impl.getJavaClass().getName();
        String componentTypeURI = className.replace('.', '/') + ".componentType";
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        componentType.setURI(componentTypeURI);
        componentType = resolver.resolveModel(ComponentType.class, componentType);
        if (!componentType.isUnresolved()) {
            return componentType;
        }
        return null;
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_JAVA_QNAME;
    }

    public Class<JavaImplementation> getModelType() {
        return JavaImplementation.class;
    }

}
