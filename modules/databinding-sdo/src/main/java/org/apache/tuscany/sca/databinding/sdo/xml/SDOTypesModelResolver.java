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

package org.apache.tuscany.sca.databinding.sdo.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.sdo.SDOTypes;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.monitor.impl.ProblemImpl;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;

/**
 * SDO types model resolver that aggregates the SDO type registration for an SCA contribution
 */
public class SDOTypesModelResolver implements ModelResolver {
    private Contribution contribution;
    private HelperContext helperContext;
    private List<SDOTypes> sdoTypes = new ArrayList<SDOTypes>();
    private ContributionFactory contributionFactory;
    private XSDFactory xsdFactory;
    private ProcessorContext context;

    public SDOTypesModelResolver(Contribution contribution, FactoryExtensionPoint modelFactories) {
        super();
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        this.xsdFactory = modelFactories.getFactory(XSDFactory.class);
        this.contribution = contribution;
    }

    public void addModel(Object resolved, ProcessorContext context) {
        if (helperContext == null) {
            helperContext = SDOUtil.createHelperContext();
        }
        this.context = context;
        SDOTypes types = (SDOTypes)resolved;
        try {
            loadSDOTypes(types, contribution.getModelResolver());
        } catch (ContributionResolveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sdoTypes.add(types);
    }

    public Object removeModel(Object resolved, ProcessorContext context) {
        SDOTypes types = (SDOTypes)resolved;

        return sdoTypes.remove(types);
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved, ProcessorContext context) {
        SDOTypes types = (SDOTypes)unresolved;
        String ns = types.getNamespace();
        this.context = context;
        for (SDOTypes t : sdoTypes) {
            if (t.getNamespace().equals(types.getNamespace())) {
                try {
                    loadSDOTypes(types, contribution.getModelResolver());
                } catch (ContributionResolveException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return (T)t;
            }
        }
        return (T)types;
    }

    private static void register(Class<?> factoryClass, HelperContext helperContext) throws Exception {
        Field field = factoryClass.getField("INSTANCE");
        Object factory = field.get(null);
        Method method = factory.getClass().getMethod("register", new Class[] {HelperContext.class});
        method.invoke(factory, new Object[] {helperContext});
    }

    private void defineFromFactory(SDOTypes importSDO, ModelResolver resolver) throws ContributionResolveException {
        String factoryName = importSDO.getFactory();
        if (factoryName != null) {
            ClassReference reference = new ClassReference(factoryName);
            ClassReference resolved = resolver.resolveModel(ClassReference.class, reference, context);
            if (resolved != null && !resolved.isUnresolved()) {
                try {
                    Class<?> factoryClass = resolved.getJavaClass();
                    // Get the namespace
                    Field field = factoryClass.getField("NAMESPACE_URI");
                    importSDO.setNamespace((String)field.get(null));
                    register(factoryClass, helperContext);
                    importSDO.setUnresolved(false);
                } catch (Exception e) {
                    ContributionResolveException ce = new ContributionResolveException(e);
                    error(context.getMonitor(), "ContributionResolveException", resolver, ce);
                    //throw ce;
                }
            } else {
                error(context.getMonitor(), "FailToResolveClass", resolver, factoryName);
                //ContributionResolveException loaderException =
                //new ContributionResolveException("Fail to resolve class: " + factoryName);
                //throw loaderException;
            }
        }
    }

    private void defineFromXSD(SDOTypes importSDO, ModelResolver resolver) throws ContributionResolveException {
        String location = importSDO.getSchemaLocation();
        if (location != null) {
            try {
                Artifact artifact = contributionFactory.createArtifact();
                artifact.setURI(location);
                artifact = resolver.resolveModel(Artifact.class, artifact, context);
                if (artifact.getLocation() != null) {
                    String wsdlURL = artifact.getLocation();
                    URLConnection connection = new URL(wsdlURL).openConnection();
                    connection.setUseCaches(false);
                    InputStream xsdInputStream = connection.getInputStream();
                    try {
                        XSDHelper xsdHelper = helperContext.getXSDHelper();
                        List<Type> sdoTypes = xsdHelper.define(xsdInputStream, wsdlURL);
                        for (Type t : sdoTypes) {
                            importSDO.setNamespace(t.getURI());
                            break;
                        }
                        importSDO.getTypes().addAll(sdoTypes);
                    } finally {
                        xsdInputStream.close();
                    }
                    importSDO.setUnresolved(false);
                } else {
                    error(context.getMonitor(), "FailToResolveLocation", resolver, location);
                    //ContributionResolveException loaderException = new ContributionResolveException("Fail to resolve location: " + location);
                    //throw loaderException;
                }
            } catch (IOException e) {
                ContributionResolveException ce = new ContributionResolveException(e);
                error(context.getMonitor(), "ContributionResolveException", resolver, ce);
                //throw ce;
            }
        } else {
            String ns = importSDO.getNamespace();
            if (ns != null) {
                XSDefinition xsd = xsdFactory.createXSDefinition();
                xsd.setUnresolved(true);
                xsd.setNamespace(ns);
                xsd = resolver.resolveModel(XSDefinition.class, xsd, context);
                if (!xsd.isUnresolved()) {
                    XSDHelper xsdHelper = helperContext.getXSDHelper();
                    xsdHelper.define(xsd.getLocation().toString());
                }
            }
        }
    }

    private void loadSDOTypes(SDOTypes types, ModelResolver resolver) throws ContributionResolveException {
        synchronized (types) {
            if (types.isUnresolved()) {
                defineFromFactory(types, resolver);
                defineFromXSD(types, resolver);
                types.setUnresolved(false);
            }
        }
    }

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem =
                new ProblemImpl(this.getClass().getName(), "databinding-sdo-validation-messages", Severity.ERROR,
                                message, model, message, ex);
            monitor.problem(problem);
        }
    }

    /**
     * Report a error.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                new ProblemImpl(this.getClass().getName(), "databinding-sdo-validation-messages", Severity.ERROR,
                                message, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

}
