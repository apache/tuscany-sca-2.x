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

package scatours;

import static scatours.launcher.LauncherUtil.locate;

import javax.naming.Context;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

import scatours.notification.Notification;

public class NotificationEJBLauncher {

    public static void main(String[] args) throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
        System.setProperty(Context.PROVIDER_URL, "ejbd://localhost:4201");

        SCAContribution notificationContribution = locate("notification");
        SCAContribution notificationEJBContribution = locate("notification-ejb");

        SCANode node =
            SCANodeFactory.newInstance().createSCANode("notification-ejb.composite",
                                                       notificationContribution,
                                                       notificationEJBContribution);
        node.start();

        System.out.println("Quick notification test");
        Notification notification = ((SCAClient)node).getService(Notification.class, "Notification");
        String accountID = "1234";
        String subject = "Holiday payment taken";
        String message = "Payment of £102.37 accepted...";
        notification.notify(accountID, subject, message);

        System.out.println("Node started - Press enter to shutdown.");
        System.in.read();
        node.stop();
    }
}
