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
package org.apache.tuscany.sca.databinding.job;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class JobExecutionContext implements java.io.Serializable {
    private JobDataMap jobData;

    public JobDataMap getJobData() {
        return jobData;
    }

    public void storeJSONData(String jsonData) {
        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        jobData = (JobDataMap) xstream.fromXML(jsonData);
    }

    public String getJSONData() {
        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        String jsonData = xstream.toXML(jobData);
        return jsonData;
    }

    public void setJobData(JobDataMap jdm) {
        this.jobData = jdm;
    }
}
