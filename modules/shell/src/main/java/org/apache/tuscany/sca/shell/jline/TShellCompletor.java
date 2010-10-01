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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.apache.tuscany.sca.shell.Shell;

/**
 * A Completor thats specific to the Tuscany Shell that knows about
 * each command and has an argument specific Completor for each command argument.
 */
public class TShellCompletor extends ArgumentCompletor {

    Map<String, Completor[]> completors;
    final Completor commandCompletor = new SimpleCompletor(Shell.COMMANDS);
    final ArgumentDelimiter delim = new WhitespaceArgumentDelimiter();
    final Shell shell;
    
    static String lastArg;

    public TShellCompletor(Shell shell) {
        super((Completor)null);
        this.shell = shell;
        completors = new HashMap<String, Completor[]>();
        completors.put("help", new Completor[]{commandCompletor, commandCompletor, new NullCompletor()});    
//        completors.put("install", new Completor[]{commandCompletor, new InstallCompletor(), new NullCompletor()});    
        completors.put("install", new Completor[]{commandCompletor, new FileNameCompletor(), new FileNameCompletor(), new NullCompletor()});    
        completors.put("installed", new Completor[]{commandCompletor, new ICURICompletor(shell), new NullCompletor()});    
        completors.put("load", new Completor[]{commandCompletor, new FileNameCompletor(), new NullCompletor()});    
        completors.put("remove", new Completor[]{commandCompletor, new ICURICompletor(shell), new NullCompletor()});    
        completors.put("run", new Completor[]{commandCompletor, new FileNameCompletor(), new NullCompletor()});    
        completors.put("addDeploymentComposite", new Completor[]{commandCompletor, new ICURICompletor(shell), new FileNameCompletor(), new NullCompletor()});    
        completors.put("printDomainLevelComposite", new Completor[]{commandCompletor, new NullCompletor()});    
        completors.put("save", new Completor[]{commandCompletor, new FileNameCompletor(), new NullCompletor()});    
        completors.put("start", new Completor[]{commandCompletor, new ICURICompletor(shell), new CompositeURICompletor(shell), new NullCompletor()});    
        completors.put("status", new Completor[]{commandCompletor, new ICURICompletor(shell), new CompositeURICompletor(shell), new NullCompletor()});    
        completors.put("stop", new Completor[]{commandCompletor, new ICURICompletor(shell), new CompositeURICompletor(shell), new NullCompletor()});    
    }

    @Override
    /**
     * Copied from JLine ArgumentCompletor class. The only change is to 
     * get the completors by using the getCompletors method and setting the lastArg static.
     */
    public int complete(final String buffer, final int cursor,
                        final List candidates) {
        ArgumentList list = delim.delimit(buffer, cursor);
        int argpos = list.getArgumentPosition();
        int argIndex = list.getCursorArgumentIndex();

        if (argIndex < 0) {
            return -1;
        }

        if (argIndex > 0) {
            /* set the last argument in a static for the CompositeURICompletor */
            lastArg = list.getArguments()[argIndex-1];
            if (lastArg != null) lastArg = lastArg.trim();
        }
        
        final Completor comp;
        
        Completor[] completors = getCompletors(buffer);

        // if we are beyond the end of the completors, just use the last one
        if (argIndex >= completors.length) {
            comp = completors[completors.length - 1];
        } else {
            comp = completors[argIndex];
        }

        // ensure that all the previous completors are successful before
        // allowing this completor to pass (only if strict is true).
        for (int i = 0; getStrict() && (i < argIndex); i++) {
            Completor sub =
                completors[(i >= completors.length) ? (completors.length - 1) : i];
            String[] args = list.getArguments();
            String arg = ((args == null) || (i >= args.length)) ? "" : args[i];

            List subCandidates = new LinkedList();

            if (sub.complete(arg, arg.length(), subCandidates) == -1) {
                return -1;
            }

            if (subCandidates.size() == 0) {
                return -1;
            }
        }

        int ret = comp.complete(list.getCursorArgument(), argpos, candidates);

        if (ret == -1) {
            return -1;
        }

        int pos = ret + (list.getBufferPosition() - argpos);

        /**
         *  Special case: when completing in the middle of a line, and the
         *  area under the cursor is a delimiter, then trim any delimiters
         *  from the candidates, since we do not need to have an extra
         *  delimiter.
         *
         *  E.g., if we have a completion for "foo", and we
         *  enter "f bar" into the buffer, and move to after the "f"
         *  and hit TAB, we want "foo bar" instead of "foo  bar".
         */
        if ((cursor != buffer.length()) && delim.isDelimiter(buffer, cursor)) {
            for (int i = 0; i < candidates.size(); i++) {
                String val = candidates.get(i).toString();

                while ((val.length() > 0)
                    && delim.isDelimiter(val, val.length() - 1)) {
                    val = val.substring(0, val.length() - 1);
                }

                candidates.set(i, val);
            }
        }

        ConsoleReader.debug("Completing " + buffer + "(pos=" + cursor + ") "
            + "with: " + candidates + ": offset=" + pos);

        return pos;
    }

    protected Completor[] getCompletors(String buffer) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<buffer.length(); i++) {
            if (Character.isWhitespace(buffer.charAt(i))) {
                break;
            }
            sb.append(buffer.charAt(i));
        }
        String command = sb.toString();
        Completor[] comps = completors.get(command);
        return comps == null ? new Completor[]{commandCompletor} : comps;
    }
}
