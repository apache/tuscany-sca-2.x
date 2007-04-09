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
package org.apache.tuscany.container.javascript.utils.xmlfromxsd;

/**
 * This class provides a generic command line arguments processing utility.  The processArgs
 * method of this class processes the command line arguments that could contain option flags, 
 * options and values and calls a ArgumentHanlder instance for handling the agruments. 
 *
 */
public class CmdLineArgsProcessor {
    private String printUsageMessage = "No hints available on Usage!";

    private CmdLineArgsHandler argsHandler = null;

    public static final String HYPEN = "-";

    public void processArgs(String[] args) {
        try {
            if (args.length == 0) {
                printUsage();
            } else {
                parseAndHandleArgs(args);
            }
        } catch (Exception e) {
            System.out.println("Exception in processing argument - " + e);
            printUsage();
        }

    }

    public void parseAndHandleArgs(String[] args) throws Exception {
        int index = 0;
        while (index < args.length - 1) {
            if (args[index].startsWith(HYPEN) && !args[index + 1].startsWith(HYPEN)) {
                argsHandler.handleArgument(args[index].substring(1), args[index + 1]);
                index = index + 2;
            } else {
                throw new IllegalArgumentException("Wrong Usage of options!");
            }
        }
    }

    protected void printUsage() {

    }

    public CmdLineArgsHandler getArgsHandler() {
        return argsHandler;
    }

    public void setArgsHandler(CmdLineArgsHandler argsHandler) {
        this.argsHandler = argsHandler;
    }

    public String getPrintUsageMessage() {
        return printUsageMessage;
    }

    public void setPrintUsageMessage(String printUsageMessage) {
        this.printUsageMessage = printUsageMessage;
    }
}
