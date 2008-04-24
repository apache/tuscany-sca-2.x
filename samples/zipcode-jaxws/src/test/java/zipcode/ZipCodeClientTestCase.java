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
package zipcode;

import location.LocationClient;
import weather.WeatherForecastClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the callback server is available
 */
public class ZipCodeClientTestCase {

    @Before
	public void startServer() throws Exception {
	}

	@Test
	public void testClient() throws Exception {
        /*
        System.out.println("Running LocationClient...");
		LocationClient.main(null);
        System.out.println("Running WeatherForecastClient...");
        WeatherForecastClient.main(null);
        */
        System.out.println("Running ZipCodeClient...");
        ZipCodeClient.main(null);
	}
    
	@After
	public void stopServer() throws Exception {
	}
}
