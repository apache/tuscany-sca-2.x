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

import org.apache.maven.surefire.testset.SurefireTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.report.ReporterManager;

/**
 * @version $Rev$ $Date$
 */
public class SCATestSet implements SurefireTestSet {
    private final String name;
    private final int testCount;

    public SCATestSet(String name, int testCount) {
        this.name = name;
        this.testCount = testCount;
    }

    public void execute(ReporterManager reporterManager, ClassLoader classLoader) throws TestSetFailedException {
    }

    public int getTestCount() {
        return testCount;
    }

    public String getName() {
        return name;
    }

    public Class getTestClass() {
        throw new UnsupportedOperationException();
    }
}
