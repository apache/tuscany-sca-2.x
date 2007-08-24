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
package bigbank.webclient.services.profile;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(LoginService.class)
public class SimpleLoginServiceImpl implements LoginService {

    public ProfileService profileService;

    @Reference
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public int login(String userName, String password) {

        if (!"test".equals(userName)) {
            return INVALID_LOGIN;
        }

        if (!"password".equals(password)) {
            return INVALID_PASSWORD;
        }

        profileService.setLoggedIn(true);
        profileService.setFirstName("John");
        profileService.setLastName("Doe");
        profileService.setId(12345);

        return SUCCESS;
    }
}
