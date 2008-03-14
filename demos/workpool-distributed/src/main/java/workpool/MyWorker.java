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
package workpool;

import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.databinding.job.Job;
import org.apache.tuscany.sca.databinding.job.JobDataMap;
import org.apache.tuscany.sca.databinding.job.JobExecutionContext;
import org.apache.tuscany.sca.databinding.job.RemoteJob;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class MyWorker extends WorkerServiceImpl<Object, Double> {
    private static int resultcount = 0;

    @Override
    public ResultJob computeTask(Job<Object, Double> job) {

        RemoteJob remoteJob = (RemoteJob) job;
        System.out.println("Computing the job");
        JobExecutionContext context = remoteJob.getContext();
        ResultJob resultJob = new ResultJob();
        JobDataMap resultMap = new JobDataMap();
        resultMap.addJobData("result", remoteJob.compute(context));
        resultJob.setJobDataMap(resultMap);
        System.out.println("Count result = " + (++resultcount));
        return resultJob;
    }

}
