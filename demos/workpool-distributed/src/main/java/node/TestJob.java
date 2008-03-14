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
package node;

import org.apache.tuscany.sca.databinding.job.Job;
import org.apache.tuscany.sca.databinding.job.JobDataMap;
import org.apache.tuscany.sca.databinding.job.JobExecutionContext;
import org.apache.tuscany.sca.databinding.job.RemoteJob;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class TestJob extends RemoteJob<Double> implements java.io.Serializable {
    private boolean EOS = false;
    private Double value;

    public TestJob(Double x, long iterations, int[] items) {
        JobDataMap map = new JobDataMap();
        map.addJobData("value", x);
        map.addJobData("iterations", iterations);
        map.addJobData("items", items);
        context.setJobData(map);
    }

    public TestJob(Double i, boolean eos) {
        value = i;
        this.EOS = eos;
    }

    public TestJob(String jsonData) {
        JobExecutionContext ctxt = new JobExecutionContext();
        ctxt.storeJSONData(jsonData);
    }

    public int getType() {
        return Job.REGULAR_JOB;
    }

    public void setEOS() {
        EOS = true;
    }

    public boolean eos() {
        return EOS;
    }

    @Override
    public Double compute(JobExecutionContext context) {
        JobDataMap contextMap = context.getJobData();
        Long iterations = (Long) contextMap.getJobDataObject("iterations");
        Double value = (Double) contextMap.getJobDataObject("value");
        double x = value.doubleValue();
        System.out.println("Computing sinx for " + value + " for "
                + iterations.intValue() + " times");
        long computing_start = System.currentTimeMillis();
        for (long i = 0; i < iterations.longValue(); ++i) {
            x = Math.sin(x);
        }
        long computing_end = System.currentTimeMillis();
        System.out.println("Computing time= "
                + (computing_end - computing_start));
        System.out.println("Send result = " + x);
        return new Double(x);
    }

}
