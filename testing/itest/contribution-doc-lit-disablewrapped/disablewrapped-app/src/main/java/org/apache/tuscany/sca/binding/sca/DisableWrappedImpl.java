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
package org.apache.tuscany.sca.binding.sca;


import org.apache.tuscany.sca.binding.sca.jaxb.iface.*;
import org.oasisopen.sca.annotation.Service;


@Service(GuessAndGreetDisableWrapped.class)

public class DisableWrappedImpl implements GuessAndGreetDisableWrapped {

    public SendGuessAndNameResponse sendGuessAndName(SendGuessAndName request) 
        throws WayTooLowFaultMsg, IncorrectGuessFaultMsg, GameOverFaultMsg, WayTooHighFaultMsg, OneTwoThreeFaultMsg
    { 

        int guess = request.getGuess();
        String name = request.getName();
        Person inPerson = request.getPerson();

        if (guess == 0) {

            org.apache.tuscany.sca.binding.sca.jaxb.iface.ObjectFactory factory = new org.apache.tuscany.sca.binding.sca.jaxb.iface.ObjectFactory();
            IncorrectGuessFault faultBean = factory.createIncorrectGuessFault();
            //set values for exception
            faultBean.setGuess(guess); 
            String hint = "Try non-zero";
            faultBean.setHint(hint);
            IncorrectGuessFaultMsg exc = new IncorrectGuessFaultMsg("Exc from GuessAndGreetImpl", faultBean);
            throw exc;       
        }

        //magic # is correct, return a person object.
        org.apache.tuscany.sca.binding.sca.jaxb.iface.ObjectFactory personFactory = new org.apache.tuscany.sca.binding.sca.jaxb.iface.ObjectFactory();
        Person person = personFactory.createPerson();
    
        String[] names = name.split(" ");
        person.setFirstName(names[0]);
        if (names.length < 2){
            person.setLastName("Doe");
        } else {
            person.setLastName(names[1]);
        }
        person.setGreeting("Winner!");
        person.setChild(inPerson);

        org.apache.tuscany.sca.binding.sca.jaxb.iface.ObjectFactory wrapperFactory = new org.apache.tuscany.sca.binding.sca.jaxb.iface.ObjectFactory();

        SendGuessAndNameResponse response = wrapperFactory.createSendGuessAndNameResponse(); 
        response.setPerson(person);
        return response;
    }

}

