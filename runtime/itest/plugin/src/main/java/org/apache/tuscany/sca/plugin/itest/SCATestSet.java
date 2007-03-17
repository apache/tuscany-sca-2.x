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
package org.apache.tuscany.sca.plugin.itest;

import java.util.Collection;
import java.net.URI;

import org.apache.maven.surefire.testset.SurefireTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.StackTraceWriter;
import org.apache.maven.surefire.report.PojoStackTraceWriter;

import org.apache.tuscany.spi.model.Operation;

/**
 * @version $Rev$ $Date$
 */
public class SCATestSet implements SurefireTestSet {
    private final MavenEmbeddedRuntime runtime;
    private final String name;
    private final URI contextId;
    private final URI componentId;
    private final Collection<? extends Operation<?>> operations;

    public SCATestSet(MavenEmbeddedRuntime runtime, 
                      String name,
                      URI contextId,
                      URI uri,
                      Collection<? extends Operation<?>> operations) {
        this.runtime = runtime;
        this.name = name;
        this.contextId = contextId;
        this.componentId = uri;
        this.operations = operations;
    }

    public void execute(ReporterManager reporterManager, ClassLoader classLoader) throws TestSetFailedException {
        for (Operation<?> operation : operations) {
            String operationName = operation.getName();
            reporterManager.testStarting(new ReportEntry(this, operationName, name));
            try {
                runtime.executeTest(contextId, componentId, operation);
                reporterManager.testSucceeded(new ReportEntry(this, operationName, name));
            } catch (Exception e) {
                StackTraceWriter stw = new PojoStackTraceWriter(name, operationName, e);
                reporterManager.testFailed(new ReportEntry(this, operationName, name, stw));
                throw new TestSetFailedException(e);
            }
        }
    }

    public int getTestCount() {
        return operations.size();
    }

    public String getName() {
        return name;
    }

    public Class getTestClass() {
        throw new UnsupportedOperationException();
    }
}
