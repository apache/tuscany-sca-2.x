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
package org.apache.tuscany.sca.policy.security.jsr250;

import java.lang.reflect.Method;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PolicyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.impl.PolicyJavaInterfaceVisitor;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.oasisopen.sca.annotation.Service;

/**
 * @version $Rev: 662474 $ $Date: 2008-06-02 17:18:28 +0100 (Mon, 02 Jun 2008) $
 */
public class PolicyProcessorTestCase extends TestCase {
    private ExtensionPointRegistry registry;
    private ServiceProcessor serviceProcessor;
    private PolicyProcessor policyProcessor;
    private JSR250PolicyProcessor jsr250Processor;
    private PolicyJavaInterfaceVisitor visitor;
    private JavaImplementation type;   
    
    private interface Interface1 {
        int method1();

        int method2();

        int method3();

        int method4();
    }
    
    @RunAs("Role1")
    @Service(Interface1.class)
    private class Service1 implements Interface1 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }
    }   
    
    @RolesAllowed({"Role2", "Role3"})
    @Service(Interface1.class)
    private class Service2 implements Interface1 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }
    } 
    
    @PermitAll()
    @Service(Interface1.class)
    private class Service3 implements Interface1 {
        public int method1() {
            return 0;
        }

        public int method2() {
            return 0;
        }

        public int method3() {
            return 0;
        }

        public int method4() {
            return 0;
        }
    }    
    

    @Service(Interface1.class)
    private class Service4 implements Interface1 {
        public int method1() {
            return 0;
        }

        @RolesAllowed({"Role4", "Role5"})
        public int method2() {
            return 0;
        }

        @PermitAll
        public int method3() {
            return 0;
        }

        @DenyAll
        public int method4() {
            return 0;
        }
    }       
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registry = new DefaultExtensionPointRegistry();
        registry.start();
        serviceProcessor = new ServiceProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory(registry));
        policyProcessor = new PolicyProcessor(registry);
        jsr250Processor = new JSR250PolicyProcessor(new DefaultAssemblyFactory(), new DefaultPolicyFactory());
        visitor = new PolicyJavaInterfaceVisitor(registry);
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        type = javaImplementationFactory.createJavaImplementation();
    }

    public void testSingleInterfaceWithRunAsAtClassLevel() throws Exception {
        runProcessors(Service1.class, null, type);
        Assert.assertEquals(1, type.getPolicySets().size());
    }  
    
    public void testSingleInterfaceWithRolesAllowedsAtClassLevel() throws Exception {
        runProcessors(Service2.class, null, type);
        Assert.assertEquals(1, type.getPolicySets().size());
    }  
    
    public void testSingleInterfaceWithPermitAllAtClassLevel() throws Exception {
        runProcessors(Service3.class, null, type);
        Assert.assertEquals(1, type.getPolicySets().size());
    }     
    
    public void testSingleInterfaceWithRolesAllowedAtMethodLevel() throws Exception {
        runProcessors(Service4.class, Service4.class.getMethods()[1], type);
        Operation op = getOperationModel(Service4.class.getMethods()[1], type);
        Assert.assertEquals(1, op.getPolicySets().size());
    } 
    
    public void testSingleInterfaceWithPermitAllAtMethodLevel() throws Exception {
        runProcessors(Service4.class, Service4.class.getMethods()[2], type);
        Operation op = getOperationModel(Service4.class.getMethods()[2], type);
        Assert.assertEquals(1, op.getPolicySets().size());
    }     

    public void testSingleInterfaceWithDenyAllAtMethodLevel() throws Exception {
        runProcessors(Service4.class, Service4.class.getMethods()[3], type);
        Operation op = getOperationModel(Service4.class.getMethods()[3], type);
        Assert.assertEquals(1, op.getPolicySets().size());
    }  
    
    public void testSingleInterfaceWithNothingAtMethodLevel() throws Exception {
        runProcessors(Service4.class, Service4.class.getMethods()[0], type);
        Operation op = getOperationModel(Service4.class.getMethods()[0], type);
        Assert.assertEquals(0, op.getPolicySets().size());
    }      
    
    private void runProcessors(Class clazz, Method method, JavaImplementation type)throws Exception {
        serviceProcessor.visitClass(clazz, type);
        policyProcessor.visitClass(clazz, type);
        jsr250Processor.visitClass(clazz, type);
        if (method != null){
            jsr250Processor.visitMethod(method, type);
        }
    }
    
    private Operation getOperationModel(Method method, JavaImplementation type){
        
        for(Operation op : type.getOperations()){
            if (((JavaOperation)op).getJavaMethod().equals(method)){
                return op;
            } 
        }
        
        return null;
    }
}
