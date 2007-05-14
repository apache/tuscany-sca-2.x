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
package org.apache.tuscany.sca.databinding.impl;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.impl.DirectedGraph.Edge;
import org.apache.tuscany.sca.databinding.impl.DirectedGraph.Vertex;

public class DirectedGraphTestCase extends TestCase {
    private DirectedGraph<String, Object> graph;

    protected void setUp() throws Exception {
        super.setUp();
        graph = new DirectedGraph<String, Object>();
        graph.addEdge("a", "b", null, 3);
        graph.addEdge("b", "c", null, 1);
        graph.addEdge("a", "c", null, 8);
        graph.addEdge("a", "d", null, 3);
        graph.addEdge("b", "d", null, 2);
        graph.addEdge("c", "b", null, 1);
        graph.addEdge("c", "d", null, 2);
        graph.addEdge("d", "b", null, 1);
        graph.addEdge("a", "e", null, 8);
        graph.addEdge("c", "c", null, 2);
    }

    public void testGraph() {
        // System.out.println(graph);

        Vertex vertex = graph.getVertex("a");
        Assert.assertNotNull(vertex);
        Assert.assertEquals(vertex.getValue(), "a");

        Assert.assertNull(graph.getVertex("1"));

        Edge edge = graph.getEdge("a", "b");
        Assert.assertNotNull(edge);
        Assert.assertEquals(edge.getWeight(), 3);

        edge = graph.getEdge("b", "a");
        Assert.assertNull(edge);

        DirectedGraph<String, Object>.Path path = graph.getShortestPath("a", "c");

        List<DirectedGraph<String, Object>.Edge> edges = path.getEdges();
        Assert.assertEquals(edges.size(), 2);
        Assert.assertEquals(edges.get(0), graph.getEdge("a", "b"));
        Assert.assertEquals(edges.get(1), graph.getEdge("b", "c"));

        Assert.assertEquals(path.getWeight(), 4);

        DirectedGraph<String, Object>.Path path2 = graph.getShortestPath("b", "e");
        Assert.assertNull(path2);

        DirectedGraph<String, Object>.Path path3 = graph.getShortestPath("a", "a");
        Assert.assertTrue(path3.getWeight() == 0 && path3.getEdges().isEmpty());

        DirectedGraph<String, Object>.Path path4 = graph.getShortestPath("c", "c");
        Assert.assertTrue(path4.getWeight() == 2 && path4.getEdges().size() == 1);

        // System.out.println(path);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
