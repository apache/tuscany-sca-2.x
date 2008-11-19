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
package org.apache.tuscany.sca.core.invocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Directed, weighted graph
 * 
 * @param <V> The type of vertex object
 * @param <E> The type of edge object
 *
 * @version $Rev$ $Date$
 */
public class PhaseSorter<V> implements Cloneable {
    private final Map<V, Vertex> vertices = new HashMap<V, Vertex>();

    /**
     * Vertex of a graph
     */
    public final class Vertex {
        private V value;

        // TODO: Do we want to support multiple edges for a vertex pair? If so,
        // we should use a List instead of Map
        private Map<Vertex, Edge> outEdges = new HashMap<Vertex, Edge>();
        private Map<Vertex, Edge> inEdges = new HashMap<Vertex, Edge>();

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

        public Map<Vertex, Edge> getInEdges() {
            return inEdges;
        }

    }

    /**
     * An Edge connects two vertices in one direction
     */
    public final class Edge {
        private Vertex sourceVertex;

        private Vertex targetVertex;

        public Edge(Vertex source, Vertex target) {
            this.sourceVertex = source;
            this.targetVertex = target;
        }

        @Override
        public String toString() {
            return sourceVertex + "->" + targetVertex;
        }

        public Vertex getTargetVertex() {
            return targetVertex;
        }

        public void setTargetVertex(Vertex vertex) {
            this.targetVertex = vertex;
        }

        public Vertex getSourceVertex() {
            return sourceVertex;
        }

        public void setSourceVertex(Vertex sourceVertex) {
            this.sourceVertex = sourceVertex;
        }
    }

    public void addEdge(V source, V target) {
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
        Edge edge = new Edge(s, t);
        s.outEdges.put(t, edge);
        t.inEdges.put(s, edge);
    }

    public void addVertext(V source) {
        Vertex s = getVertex(source);
        if (s == null) {
            s = new Vertex(source);
            vertices.put(source, s);
        }
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

        return s.outEdges.remove(t) != null && t.inEdges.remove(s) != null;

    }

    public void removeEdge(Edge edge) {
        edge.sourceVertex.outEdges.remove(edge.targetVertex);
        edge.targetVertex.inEdges.remove(edge.sourceVertex);
    }

    public void removeVertex(Vertex vertex) {
        vertices.remove(vertex.getValue());
        for (Edge e : new ArrayList<Edge>(vertex.outEdges.values())) {
            removeEdge(e);
        }
        for (Edge e : new ArrayList<Edge>(vertex.inEdges.values())) {
            removeEdge(e);
        }
    }

    public Edge getEdge(Vertex source, Vertex target) {
        return source.outEdges.get(target);
    }

    public Edge getEdge(V source, V target) {
        Vertex sv = getVertex(source);
        if (sv == null) {
            return null;
        }
        Vertex tv = getVertex(target);
        if (tv == null) {
            return null;
        }
        return getEdge(getVertex(source), getVertex(target));
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

    public void addGraph(PhaseSorter<V> otherGraph) {
        for (Vertex v : otherGraph.vertices.values()) {
            for (Edge e : v.outEdges.values()) {
                addEdge(e.sourceVertex.value, e.targetVertex.value);
            }
        }
    }

    private Vertex getFirst() {
        for (Vertex v : vertices.values()) {
            if (v.inEdges.isEmpty()) {
                return v;
            }
        }
        if (!vertices.isEmpty()) {
            throw new IllegalArgumentException("Circular ordering has been detected: " + toString());
        } else {
            return null;
        }
    }

    public List<V> topologicalSort(boolean readOnly) {
        PhaseSorter<V> graph = (!readOnly) ? this : (PhaseSorter<V>)clone();
        List<V> list = new ArrayList<V>();
        while (true) {
            Vertex v = graph.getFirst();
            if (v == null) {
                break;
            }
            list.add(v.getValue());
            graph.removeVertex(v);
        }

        return list;
    }

    @Override
    public Object clone() {
        PhaseSorter<V> copy = new PhaseSorter<V>();
        copy.addGraph(this);
        return copy;
    }
}
