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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Directed, weighted graph
 * 
 * @param <V> The type of vertex object
 * @param <E> The type of edge object
 */
public class DirectedGraph<V, E> {
    private final Map<V, Vertex> vertices = new HashMap<V, Vertex>();

    /**
     * Key for the shortest path cache
     */
    private final class VertexPair {
        private Vertex source;

        private Vertex target;

        /**
         * @param source
         * @param target
         */
        private VertexPair(Vertex source, Vertex target) {
            super();
            this.source = source;
            this.target = target;
        }

        @Override
        public boolean equals(Object object) {
            if (!VertexPair.class.isInstance(object)) {
                return false;
            }
            VertexPair pair = (VertexPair)object;
            return source == pair.source && target == pair.target;
        }

        @Override
        public int hashCode() {
            int x = source == null ? 0 : source.hashCode();
            int y = target == null ? 0 : target.hashCode();
            return x ^ y;
        }

    }

    private final Map<VertexPair, Path> paths = new HashMap<VertexPair, Path>();

    /**
     * Vertex of a graph
     */
    public final class Vertex {
        private V value;

        // TODO: Do we want to support multiple edges for a vertex pair? If so,
        // we should use a List instead of Map
        private Map<Vertex, Edge> outEdges = new HashMap<Vertex, Edge>();

        private Vertex(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + value + ")";
        }

        public V getValue() {
            return value;
        }

        public Map<Vertex, Edge> getOutEdges() {
            return outEdges;
        }

    }

    /**
     * An Edge connects two vertices in one direction
     */
    public final class Edge {
        private Vertex sourceVertex;

        private Vertex targetVertex;

        private E value;

        private int weight;

        public Edge(Vertex source, Vertex target, E value, int weight) {
            this.sourceVertex = source;
            this.targetVertex = target;
            this.value = value;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return sourceVertex + "->" + targetVertex + "[" + value + "," + weight + "]";
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public Vertex getTargetVertex() {
            return targetVertex;
        }

        public void setTargetVertex(Vertex vertex) {
            this.targetVertex = vertex;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public Vertex getSourceVertex() {
            return sourceVertex;
        }

        public void setSourceVertex(Vertex sourceVertex) {
            this.sourceVertex = sourceVertex;
        }
    }

    private final class Node implements Comparable<Node> {

        private long distance = Integer.MAX_VALUE;

        private Node previous; // NOPMD by rfeng on 9/26/06 9:17 PM

        private Vertex vertex; // NOPMD by rfeng on 9/26/06 9:17 PM

        private Node(Vertex vertex) {
            this.vertex = vertex;
        }

        public int compareTo(Node o) {
            return (distance > o.distance) ? 1 : ((distance == o.distance) ? 0 : -1);
        }
    }

    public void addEdge(V source, V target, E edgeValue, int weight) {
        Vertex s = getVertex(source);
        if (s == null) {
            s = new Vertex(source);
            vertices.put(source, s);
        }
        Vertex t = getVertex(target);
        if (t == null) {
            t = new Vertex(target);
            vertices.put(target, t);
        }
        Edge edge = new Edge(s, t, edgeValue, weight);
        s.outEdges.put(t, edge);
    }

    public Vertex getVertex(V source) {
        Vertex s = vertices.get(source);
        return s;
    }

    public boolean removeEdge(V source, V target) {
        Vertex s = getVertex(source);
        if (s == null) {
            return false;
        }

        Vertex t = getVertex(target);
        if (t == null) {
            return false;
        }

        return s.outEdges.remove(t) != null;

    }

    public Edge getEdge(Vertex source, Vertex target) {
        return source.outEdges.get(target);
    }

    public Edge getEdge(V source, V target) {
        return getEdge(getVertex(source), getVertex(target));
    }

    /**
     * Get the shortes path from the source vertex to the target vertex using
     * Dijkstra's algorithm. If there's no path, null will be returned. If the
     * source is the same as the target, it returns a path with empty edges with
     * weight 0.
     * 
     * @param sourceValue The value identifies the source
     * @param targetValue The value identifies the target
     * @return The shortest path
     */
    public Path getShortestPath(V sourceValue, V targetValue) {
        Vertex source = getVertex(sourceValue);
        if (source == null) {
            return null;
        }
        Vertex target = getVertex(targetValue);
        if (target == null) {
            return null;
        }

        VertexPair pair = new VertexPair(source, target);
        if (paths.containsKey(pair)) {
            return paths.get(pair);
        }

        // HACK: To support same vertex
        if (source == target) {
            Path path = new Path();
            Edge edge = getEdge(source, target);
            if (edge != null) {
                path.addEdge(edge);
            }
            paths.put(pair, path);
            return path;
        }

        Map<Vertex, Node> nodes = new HashMap<Vertex, Node>();
        for (Vertex v : vertices.values()) {
            Node node = new Node(v);
            if (v == source) {
                node.distance = 0;
            }
            nodes.put(v, node);
        }

        Set<Node> otherNodes = new HashSet<Node>(nodes.values());
        Set<Node> nodesOnPath = new HashSet<Node>();
        while (!otherNodes.isEmpty()) {
            Node nextNode = extractMin(otherNodes);
            if (nextNode.vertex == target) {
                Path path = getPath(nextNode);
                paths.put(pair, path); // Cache it
                return path;
            }
            nodesOnPath.add(nextNode);
            for (Edge edge : nextNode.vertex.outEdges.values()) {
                Node adjacentNode = nodes.get(edge.targetVertex);
                if (nextNode.distance + edge.weight < adjacentNode.distance) {
                    adjacentNode.distance = nextNode.distance + edge.weight;
                    adjacentNode.previous = nextNode;
                }
            }
        }
        paths.put(pair, null); // Cache it
        return null;
    }

    /**
     * Searches for the vertex u in the vertex set Q that has the least d[u]
     * value. That vertex is removed from the set Q and returned to the user.
     * 
     * @param nodes
     * @return
     */
    private Node extractMin(Set<Node> nodes) {
        Node node = Collections.min(nodes);
        nodes.remove(node);
        return node;
    }

    /**
     * The path between two vertices
     */
    public final class Path {
        private List<Edge> edges = new LinkedList<Edge>();

        private int weight;

        public int getWeight() {
            return weight;
        }

        public List<Edge> getEdges() {
            return edges;
        }

        public void addEdge(Edge edge) {
            edges.add(0, edge);
            weight += edge.weight;
        }

        @Override
        public String toString() {
            return edges + ", " + weight;
        }
    }

    private Path getPath(Node t) {
        if (t.distance == Integer.MAX_VALUE) {
            return null;
        }
        Path path = new Path();
        Node u = t;
        while (u.previous != null) {
            Edge edge = getEdge(u.previous.vertex, u.vertex);
            path.addEdge(edge);
            u = u.previous;
        }
        return path;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Vertex v : vertices.values()) {
            sb.append(v.outEdges.values()).append("\n");
        }
        return sb.toString();
    }

    public Map<V, Vertex> getVertices() {
        return vertices;
    }

    public void addGraph(DirectedGraph<V, E> otherGraph) {
        for (Vertex v : otherGraph.vertices.values()) {
            for (Edge e : v.outEdges.values()) {
                addEdge(e.sourceVertex.value, e.targetVertex.value, e.value, e.weight);
            }
        }
    }
}
