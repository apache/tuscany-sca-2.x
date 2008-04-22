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

package org.apache.tuscany.sca.vtest.javaapi.annotations.scope.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.MService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.NService;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Scope;

@Service(MService.class)
@Scope("COMPOSITE")
public class MServiceImpl implements MService {

	@Reference
	public NService n1;
	
	public static String failedReason = "";
	
	public static Timer aTimer = null;

	public static int counter = 0;

	@Init
    public void initMService() throws Exception {
    	aTimer = new Timer();
    	aTimer.schedule(new MTimerTask(), 1000, 1000);
    	System.out.println("MService->initMService");
    }

    @Destroy
    public void destroyMService() {
    	aTimer.cancel();
    	System.out.println("MService->destroyMService");
    }
    
    public String getName() {
        return "MService";
    }
    
    public String getFailedReason() {
        return failedReason;
    }
    
	private class MTimerTask extends TimerTask {

		public boolean cancel() {
			return true;
		}
		
		public void run() {
			counter++;
			String result = n1.callO1(counter);
			if (!result.equals("None"))
				failedReason = failedReason + ";" + result;
		}
		
	}
}
