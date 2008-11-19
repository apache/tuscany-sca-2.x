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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.authorization.AuthorizationPolicy;
import org.apache.tuscany.sca.policy.identity.SecurityIdentityPolicy;

/**
 * Processes an {@link javax.annotation.security.*} annotation
 * Below is a list of annotations
 * 
 *                Type    Method
 * RunAs             x
 * RolesAllowed      x       x
 * PermitAll         x       x
 * DenyAll                   x
 *
 * @version $Rev$ $Date$
 */
public class JSR250PolicyProcessor extends BaseJavaClassVisitor {
    private static final QName RUN_AS = new QName("http://www.osoa.org/xmlns/sca/1.0","runAs");
    private static final QName ALLOW = new QName("http://www.osoa.org/xmlns/sca/1.0","allow");
    private static final QName PERMIT_ALL = new QName("http://www.osoa.org/xmlns/sca/1.0","permitAll");
    private static final QName DENY_ALL = new QName("http://www.osoa.org/xmlns/sca/1.0","denyAll");
    
    private PolicyFactory policyFactory;

    public JSR250PolicyProcessor(AssemblyFactory assemblyFactory, PolicyFactory policyFactory) {
        super(assemblyFactory);
        this.policyFactory = policyFactory;
    }
    

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        
        RunAs runAs = clazz.getAnnotation(javax.annotation.security.RunAs.class);
        if (runAs != null) {
            
            String roleName = runAs.value();
            if(roleName == null) {
                //FIXME handle monitor or error
            }

            SecurityIdentityPolicy policy = new SecurityIdentityPolicy();
            policy.setRunAsRole(roleName);

            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(RUN_AS);
            policySet.getPolicies().add(policy);
            policySet.setUnresolved(false);
            ((org.apache.tuscany.sca.policy.PolicySetAttachPoint)type).getPolicySets().add(policySet);
        }
        
        RolesAllowed rolesAllowed = clazz.getAnnotation(javax.annotation.security.RolesAllowed.class);
        if(rolesAllowed != null) {
            if(rolesAllowed.value().length == 0) {
                //FIXME handle monitor or error
            }
            
            AuthorizationPolicy policy = new AuthorizationPolicy();
            policy.setAccessControl(AuthorizationPolicy.AcessControl.allow);
            
            for(String role : rolesAllowed.value()) {
                policy.getRoleNames().add(role);
            }

            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(ALLOW);
            policySet.getPolicies().add(policy);
            policySet.setUnresolved(false);
            ((org.apache.tuscany.sca.policy.PolicySetAttachPoint)type).getPolicySets().add(policySet);
        }
        
        PermitAll permitAll = clazz.getAnnotation(javax.annotation.security.PermitAll.class);
        if(permitAll != null) {
            AuthorizationPolicy policy = new AuthorizationPolicy();
            policy.setAccessControl(AuthorizationPolicy.AcessControl.permitAll);
            
            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(PERMIT_ALL);
            policySet.getPolicies().add(policy);
            policySet.setUnresolved(false);
            ((org.apache.tuscany.sca.policy.PolicySetAttachPoint)type).getPolicySets().add(policySet);
        }
        
    }
    
    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        RolesAllowed rolesAllowed = method.getAnnotation(javax.annotation.security.RolesAllowed.class);
        if(rolesAllowed != null) {
            if(rolesAllowed.value().length == 0) {
                //FIXME handle monitor or error
            }
            
            AuthorizationPolicy policy = new AuthorizationPolicy();
            policy.setAccessControl(AuthorizationPolicy.AcessControl.allow);
            
            for(String role : rolesAllowed.value()) {
                policy.getRoleNames().add(role);
            }
            
            ConfiguredOperation confOp = assemblyFactory.createConfiguredOperation();
            confOp.setName(method.getName());
            ((OperationsConfigurator)type).getConfiguredOperations().add(confOp);
            
            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(ALLOW);
            policySet.getPolicies().add(policy);
            policySet.setUnresolved(false);
            confOp.getPolicySets().add(policySet);
        }
        
        PermitAll permitAll = method.getAnnotation(javax.annotation.security.PermitAll.class);
        if(permitAll != null) {
            AuthorizationPolicy policy = new AuthorizationPolicy();
            policy.setAccessControl(AuthorizationPolicy.AcessControl.permitAll);
            
            ConfiguredOperation confOp = assemblyFactory.createConfiguredOperation();
            confOp.setName(method.getName());
            ((OperationsConfigurator)type).getConfiguredOperations().add(confOp);
            
            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(PERMIT_ALL);
            policySet.getPolicies().add(policy);
            policySet.setUnresolved(false);
            confOp.getPolicySets().add(policySet);
        }
        
        DenyAll denyAll = method.getAnnotation(javax.annotation.security.DenyAll.class);
        if(denyAll != null) {
            AuthorizationPolicy policy = new AuthorizationPolicy();
            policy.setAccessControl(AuthorizationPolicy.AcessControl.denyAll);
            
            ConfiguredOperation confOp = assemblyFactory.createConfiguredOperation();
            confOp.setName(method.getName());
            ((OperationsConfigurator)type).getConfiguredOperations().add(confOp);
            
            PolicySet policySet = policyFactory.createPolicySet();
            policySet.setName(DENY_ALL);
            policySet.getPolicies().add(policy);
            policySet.setUnresolved(false);
            confOp.getPolicySets().add(policySet);
        }
    }    
}
