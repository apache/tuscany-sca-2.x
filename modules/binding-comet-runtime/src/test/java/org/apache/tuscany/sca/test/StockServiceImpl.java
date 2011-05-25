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
package org.apache.tuscany.sca.test;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

import org.apache.tuscany.sca.binding.comet.runtime.callback.CometCallback;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Service;

@Service(StockService.class)
public class StockServiceImpl implements StockService {

	@Callback
	protected CometCallback callback;

	public static final int MAX_VALUE = 1000;
	private final Random random = new Random(new Date().getTime());

	@Override
	public void subscribeForQuotes() {
		final Double value = Math.abs(this.random.nextDouble() * this.random.nextInt(StockServiceImpl.MAX_VALUE));
		callback.sendMessage("ASF" + "#" + Double.valueOf(new DecimalFormat("#.##").format(value)));
	}

}
