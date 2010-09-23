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

package org.apache.tuscany.sca.sample.comet;

import java.util.Date;

import org.apache.tuscany.sca.sample.comet.model.Location;
import org.apache.tuscany.sca.sample.comet.model.Response;
import org.oasisopen.sca.annotation.Service;

@Service({TemperatureService.class, HumidityService.class})
public class TemperatureHumidityServiceImpl implements TemperatureService, HumidityService {

    @Override
    public Response getHumidity(final Location location) {
        final Response response = new Response();
        response.setDate(new Date());
        response.setData(Helper.randomInt(90) + "%");
        return response;
    }

    @Override
    public Response getTemperature(final Location location, final int scale) {
        final Response response = new Response();
        response.setDate(new Date());
        final String data = "" + Helper.randomInt(scale == TemperatureService.CELSIUS ? 40 : 150);
        response.setData(data);
        return response;
    }

}
