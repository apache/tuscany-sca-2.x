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

import static java.lang.System.in;
import static java.lang.System.out;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;


/**
 * A little SCA command shell.
 */
public class Shell {
    final NodeFactory nodeFactory = NodeFactory.newInstance();

    public static class NodeInfo {
        final String name;
        final String curi;
        final String cloc;
        final Node node;

        NodeInfo(final String name, final String curi, final String cloc, final Node node) {
            this.name = name;
            this.curi = curi;
            this.cloc = cloc;
            this.node = node;
        }

        public String toString() {
            return name + " " + curi + " " + cloc;
        }
    }
    final Map<String, NodeInfo> nodes = new HashMap<String, NodeInfo>();

    final List<String> history = new ArrayList<String>();
    
    public static void main(final String[] args) throws Exception {
        new Shell().run();
    }

    boolean start(final String name, final String curi, final String cloc) {
        final Node node = nodeFactory.createNode(new Contribution(curi, cloc));
        nodes.put(name, new NodeInfo(name, curi, cloc, node));
        node.start();
        return true;
    }

    boolean stop(final String name) {
        nodes.get(name).node.stop();
        nodes.remove(name);
        return true;
    }

    boolean status() {
        out.println(nodes.values());
        return true;
    }

    boolean history() {
        for (String l: history)
            out.println(l);
        return true;
    }

    static boolean bye() {
        return false;
    }

    List<String> read(final BufferedReader r) throws IOException {
        out.print("=> ");
        final String l = r.readLine();
        history.add(l);
        return Arrays.asList(l != null? l.split(" ") : "bye".split(" "));
    }
       
    Callable<Boolean> eval(final List<String> toks) {
        final String op = toks.get(0);
        if (op.equals("start")) return new Callable<Boolean>() { public Boolean call() {
            return start(toks.get(1), toks.get(2), toks.get(3));
        }};
        if (op.equals("stop")) return new Callable<Boolean>() { public Boolean call() {
            return stop(toks.get(1));
        }};
        if (op.equals("status")) return new Callable<Boolean>() { public Boolean call() {
            return status();
        }};
        if (op.equals("history")) return new Callable<Boolean>() { public Boolean call() {
            return history();
        }};
        if (op.equals("bye")) return new Callable<Boolean>() { public Boolean call() {
            return bye();
        }};
        return new Callable<Boolean>() { public Boolean call() {
            return true;
        }};
    }

    boolean apply(final Callable<Boolean> func) {
        try {
            return func.call();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public Map<String, NodeInfo> run() throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(in));
        while(apply(eval(read(r))));
        return nodes;
    }
}
