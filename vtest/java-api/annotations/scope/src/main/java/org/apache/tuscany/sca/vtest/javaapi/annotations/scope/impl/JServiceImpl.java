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

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.KService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.JService;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Scope;

@Service(JService.class)
@Scope("COMPOSITE")
public class JServiceImpl implements JService {

	@Reference
	public KService k1;
	
	public static String failedReason = "";
	
	public static Timer aTimer = null;

	@Init
    public void initJService() throws Exception {
    	aTimer = new Timer();
    	aTimer.schedule(new JTimerTask(), 1000);
    	System.out.println("JService->initJService");
    }

    @Destroy
    public void destroyJService() {
    	aTimer.cancel();
    	System.out.println("JService->destroyJService");
    }
    
    public String getName() {
        return "JService";
    }
    
    public String getFailedReason() {
        return failedReason;
    }
    
	private class JTimerTask extends TimerTask {

		public boolean cancel() {
			return true;
		}
		
		public void run() {
			String result = k1.callL1();
			if (!result.equals("None"))
				failedReason = failedReason + ";" + result;
		}
		
	}
}
