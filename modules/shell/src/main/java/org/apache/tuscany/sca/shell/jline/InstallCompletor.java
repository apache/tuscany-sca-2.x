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

package org.apache.tuscany.sca.shell.jline;

import java.io.File;
import java.util.List;

import jline.FileNameCompletor;

import org.apache.tuscany.sca.shell.Shell;

/**
 * A Completor for the install command.
 * The command format is: install [<uri>] <contributionURL> [-start] [-metadata <url>] [-duris <uri,uri,...>]
 * 
 * TODO: doesn't seem to complete the -xxx parameters properly yet
 * 
 */
public class InstallCompletor extends FileNameCompletor {

    ICURICompletor icuriCompletor;
    
    public InstallCompletor(Shell shell) {
        icuriCompletor = new ICURICompletor(shell);
    }

    public int complete(final String buf, final int cursor,
                        final List candidates) {

//        System.err.println("buf:" + buf);
//        System.err.println("candidates:" + candidates);

        if ("-duris".equals(TShellCompletor.lastArg)) {
            return icuriCompletor.complete(buf, cursor, candidates);
        }
        if ("-metadata".equals(TShellCompletor.lastArg)) {
            return super.complete(buf, cursor, candidates);
        }
        
        return super.complete(buf, cursor, candidates);
    }
    
    @Override
    public int matchFiles(String buffer, String translated, File[] entries,
                          List candidates) {
        if (entries == null) {
            return -1;
        }

        int matches = 0;

        // first pass: just count the matches
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getAbsolutePath().startsWith(translated)) {
                matches++;
            }
        }
        if ("-metadata".startsWith(buffer)) {
            matches++;
        }
        if ("-duris".startsWith(buffer)) {
            matches++;
        }
        if ("-start".startsWith(buffer)) {
            matches++;
        }

        // green - executable
        // blue - directory
        // red - compressed
        // cyan - symlink
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getAbsolutePath().startsWith(translated)) {
                String name =
                    entries[i].getName()
                    + (((matches == 1) && entries[i].isDirectory())
                       ? File.separator : " ");

                /*
                if (entries [i].isDirectory ())
                {
                        name = new ANSIBuffer ().blue (name).toString ();
                }
                */
                candidates.add(name);
            }
        }

        if ("-metadata".startsWith(buffer) && !TShellCompletor.allArgs.contains("-metadata")) {
            candidates.add("-metadata" + (matches == 1 ? " " : ""));
        }
        if ("-duris".startsWith(buffer) && !TShellCompletor.allArgs.contains("-duris")) {
            candidates.add("-duris" + (matches == 1 ? " " : ""));
        }
        if ("-start".startsWith(buffer) && !TShellCompletor.allArgs.contains("-start")) {
            candidates.add("-start" + (matches == 1 ? " " : ""));
        }

        final int index = buffer.lastIndexOf(File.separator);

        int x= index + File.separator.length();
//        System.out.println("x="+x);
        return x;
//        return index + File.separator.length();
    }
}
