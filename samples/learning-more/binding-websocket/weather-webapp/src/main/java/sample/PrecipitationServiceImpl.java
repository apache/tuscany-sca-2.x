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

@Service(PrecipitationService.class)
public class PrecipitationServiceImpl implements PrecipitationService {

    @Callback
    protected WebsocketBindingCallback client;

    @Override
    public void getPrecipitation(final Location location) {
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Response response = new Response();
                response.setDate(new Date());
                response.setData(Helper.randomInt(100) + "%");
                WebsocketStatus status = client.sendMessage(response);
                if (status == WebsocketStatus.CLOSED) {
                    System.out.println("Client disconnected from PrecipitationService.");
                    this.cancel();
                }
            }
        }, 0L, 1000L);
    }
}
