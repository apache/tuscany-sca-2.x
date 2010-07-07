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

package org.apache.tuscany.sca.shell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.SimpleCompletor;

import org.apache.tuscany.sca.runtime.ActivationException;

/**
 * Keep all the JLine specific code out of the Shell class so that it runs ok
 * when jline isn't on the classpath.  
 */
public class JLine {

    public static String readLine(Object r) throws IOException {
        return ((ConsoleReader)r).readLine();
    }
    
    public static Object createJLineReader(final Shell shell) throws IOException {
        ConsoleReader reader = new ConsoleReader();
        fixCtrlC(reader);
        // Add a Ctrl-c listener
        reader.addTriggeredAction((char)3, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    shell.stop(null);
                } catch (ActivationException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });
        reader.setBellEnabled(false);
        // TODO: write a Completor specific to this that can handle the individual command arguments
        List<Completor> completors = new LinkedList<Completor>();
//        completors.add(new SimpleCompletor(Shell.COMMANDS));
//        completors.add(new ICURICompletor(shell.node));
//        completors.add(new FileNameCompletor());
//        reader.addCompletor(new ArgumentCompletor(completors));
        reader.addCompletor(new TShellCompletor(shell.node));
        return reader;
    }

    /**
     * The windowsbindings.properties shipped inside jline maps ctrl-c to INSERT 
     * with the comment "(frankly, I wasn't sure where to bind this)". That does not
     * seem a great choice as it disables ctrl-c interupt so this resets that binding.
     */
    private static void fixCtrlC(ConsoleReader reader) {
        try {
            Field f = ConsoleReader.class.getDeclaredField("keybindings");
            f.setAccessible(true);
            short[] keybindings = (short[])f.get(reader);
            if (keybindings[3] == -48) keybindings[3] = 3;
        } catch (Exception e) {
            e.printStackTrace(); // shouldnt happen
        }
    }
}
