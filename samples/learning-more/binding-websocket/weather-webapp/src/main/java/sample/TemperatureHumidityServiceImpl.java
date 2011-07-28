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

import org.apache.tuscany.sca.binding.websocket.runtime.WebsocketBindingCallback;
import org.apache.tuscany.sca.binding.websocket.runtime.WebsocketStatus;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Service;

import sample.model.Location;
import sample.model.Response;

@Service({ TemperatureService.class, HumidityService.class })
public class TemperatureHumidityServiceImpl implements TemperatureService, HumidityService {

    @Callback
    protected WebsocketBindingCallback callback;

    @Override
    public void getHumidity(final Location location) {
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                final Response response = new Response();
                response.setDate(new Date());
                response.setData(Helper.randomInt(90) + "%");
                WebsocketStatus status = callback.sendMessage(response);
                if (status == WebsocketStatus.CLOSED) {
                    System.out.println("Client disconnected from HumidityService.");
                    this.cancel();
                }
            }
        }, 0L, 5000L);
    }

    @Override
    public void getTemperature(final Location location) {
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                final Response response = new Response();
                response.setDate(new Date());
                final String data = "" + Helper.randomInt(40);
                response.setData(data);
                WebsocketStatus status = callback.sendMessage(response);
                if (status == WebsocketStatus.CLOSED) {
                    System.out.println("Client disconnected from TemperatureService.");
                    this.cancel();
                }
            }
        }, 0L, 3000L);
    }

}