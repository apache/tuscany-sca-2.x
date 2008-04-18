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

import java.io.InputStream;
import java.util.logging.LogManager;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.monitor.impl.MonitorFactoryImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Loads a monitor and adds some problems to it
 */
public class MonitorTestCase {
    
    static private MonitorFactory monitorFactory;
    
    @BeforeClass
    public static void init() throws Exception {
        monitorFactory = new MonitorFactoryImpl();        
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        monitorFactory = null;
    }
    
    @Test
    public void testCreateProblem() throws Exception {  
        String dummyModelObject = "DUMMY MODEL OBJECT";
        
        Monitor monitor = monitorFactory.createMonitor();
        
        Problem problem = null;
        
        problem = new ProblemImpl(this.getClass().getName(), 
                                  "tuscany-monitor-test-messages", 
                                  Severity.WARNING, 
                                  dummyModelObject, 
                                  "MESSAGE1" );
        monitor.problem(problem);
        
        String param = "Some Parameter";
        
        problem = new ProblemImpl(this.getClass().getName(), 
                                  "tuscany-monitor-test-messages", 
                                  Severity.WARNING, 
                                  dummyModelObject, 
                                  "MESSAGE2",
                                  param);
        monitor.problem(problem);
        
        problem = new ProblemImpl(this.getClass().getName(), 
                                  "tuscany-monitor-test-messages", 
                                  Severity.WARNING, 
                                  dummyModelObject, 
                                  "MESSAGE3",
                                  8, 
                                  9, 
                                  4);
        monitor.problem(problem);
        
        Exception ex = new IllegalStateException("TEST_MESSAGE");
        
        problem = new ProblemImpl(this.getClass().getName(), 
                                  "tuscany-monitor-test-messages", 
                                  Severity.ERROR, 
                                  dummyModelObject, 
                                  "MESSAGE4",
                                  ex);
        monitor.problem(problem);          
        
    }
}
