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
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;


/**
 * A little SCA command shell.
 */
public class Shell {
    final NodeFactory nodeFactory;

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

    public Shell(NodeFactory nf) {
        this.nodeFactory = nf;
    }
    
    List<?> start(final String name, final String curi, final String cloc) {
        if (nodes.containsKey(name))
            return emptyList();
        final Node node = nodeFactory.createNode(new Contribution(curi, cloc));
        nodes.put(name, new NodeInfo(name, curi, cloc, node));
        node.start();
        return emptyList();
    }

    List<?> stop(final String name) {
        final NodeInfo ninfo = nodes.get(name);
        if (ninfo == null)
            return emptyList();
        ninfo.node.stop();
        nodes.remove(name);
        return emptyList();
    }

    List<?> stop() {
        for (NodeInfo ninfo: nodes.values())
            ninfo.node.stop();
        nodes.clear();
        return emptyList();
    }

    List<?> restart(final String name, final String curi, final String cloc) {
        stop(name);
        return start(name, curi, cloc);
    }

    List<?> status() {
        return new ArrayList(nodes.values());
    }

    List<?> history() {
        return history;
    }

    List<?> bye() {
        return null;
    }

    List<String> read(final BufferedReader r) throws IOException {
        final String l = r.readLine();
        history.add(l);
        return l != null? Arrays.asList(l.split(" ")) : singletonList("bye");
    }
       
    Callable<List<?>> eval(final List<String> toks) {
        final String op = toks.get(0);
        if(op.equals("start")) return new Callable<List<?>>() { public List<?> call() {
            return start(toks.get(1), toks.get(2), toks.get(3));
        }};
        if(op.equals("stop")) return new Callable<List<?>>() { public List<?> call() {
            if (toks.size() == 1)
                return stop();
            return stop(toks.get(1));
        }};
        if(op.equals("restart")) return new Callable<List<?>>() { public List<?> call() {
            return restart(toks.get(1), toks.get(2), toks.get(3));
        }};
        if(op.equals("status")) return new Callable<List<?>>() { public List<?> call() {
            return status();
        }};
        if(op.equals("history")) return new Callable<List<?>>() { public List<?> call() {
            return history();
        }};
        if(op.equals("bye")) return new Callable<List<?>>() { public List<?> call() {
            return bye();
        }};
        return new Callable<List<?>>() { public List<?> call() {
            return emptyList();
        }};
    }

    List<?> apply(final Callable<List<?>> func) {
        try {
            return func.call();
        } catch (Exception e) {
            return singletonList(e);
        }
    }

    boolean print(final List<?> l, PrintWriter w) {
        if(l == null)
            return false;
        for(Object o: l)
            w.println(o);
        return true;
    }

    public Map<String, NodeInfo> run(final BufferedReader r, final PrintWriter w) throws IOException {
        while(print(apply(eval(read(r))), w));
        r.close();
        return nodes;
    }

    public static void main(final String[] args) throws Exception {
        new Shell(NodeFactory.newInstance()).run(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out, true));
    }
}
