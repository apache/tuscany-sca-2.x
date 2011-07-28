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
package sample;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.oasisopen.sca.annotation.Constructor;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

@EagerInit
@Scope("COMPOSITE")
public class Producer {

	@Reference
	protected EventProcessorProducerService eventProcessor;

	private String eventName;
	private long frequency;

	private Timer timer = new Timer();
	private Object lock = new Object();

	@Constructor
	public Producer(@Property(name = "eventName") String eventName, @Property(name = "frequency") long frequency) {
		System.out.println("Producer: In Constructor with eventName=" + eventName + " and frequency=" + frequency);
		this.eventName = eventName;
		this.frequency = frequency;
	}

	@Init
	public void start() {
		System.out.println("Producer: In Init...");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				synchronized (lock) {
					eventProcessor.onEvent(Producer.this.eventName, "Event @ " + new Date());
				}
			}
		}, 0L, this.frequency);
	}

	@Destroy
	public void stop() {
		synchronized (lock) {
			timer.cancel();
		}
		timer = null;
	}

}
