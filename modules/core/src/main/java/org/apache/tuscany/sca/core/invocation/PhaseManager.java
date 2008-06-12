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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.invocation.Phase;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class PhaseManager {
    private static final Logger log = Logger.getLogger(PhaseManager.class.getName());

    public static final String STAGE_REFERENCE = "reference";
    public static final String STAGE_SERVICE = "service";
    public static final String STAGE_IMPLEMENTATION = "implementation";
    private static final String[] SYSTEM_REFERENCE_PHASES =
        {Phase.REFERENCE, Phase.REFERENCE_INTERFACE, Phase.REFERENCE_POLICY, Phase.REFERENCE_BINDING};

    private static final String[] SYSTEM_SERVICE_PHASES =
        {Phase.SERVICE_BINDING, Phase.SERVICE_POLICY, Phase.SERVICE_INTERFACE, Phase.SERVICE};

    private static final String[] SYSTEM_IMPLEMENTATION_PHASES = {Phase.IMPLEMENTATION_POLICY, Phase.IMPLEMENTATION};

    private String pattern = Phase.class.getName();
    private Map<String, Stage> stages;
    private List<String> phases;

    public class Stage {
        private String name;
        private PhaseSorter<String> sorter = new PhaseSorter<String>();
        private Set<String> firstSet = new HashSet<String>();
        private Set<String> lastSet = new HashSet<String>();
        private List<String> phases = new ArrayList<String>();

        public Stage(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public PhaseSorter<String> getSorter() {
            return sorter;
        }

        public Set<String> getFirstSet() {
            return firstSet;
        }

        public Set<String> getLastSet() {
            return lastSet;
        }

        public List<String> getPhases() {
            return phases;
        }

        public String toString() {
            return name + phases;
        }
    }

    // For unit test purpose
    PhaseManager(String pattern) {
        super();
        this.pattern = pattern;
    }

    public PhaseManager() {
    }

    private List<String> getPhases(String stage) {
        Stage s = getStages().get(stage);
        return s == null ? null : s.getPhases();
    }

    public List<String> getReferencePhases() {
        return getPhases(STAGE_REFERENCE);
    }

    public List<String> getServicePhases() {
        return getPhases(STAGE_SERVICE);
    }

    public List<String> getImplementationPhases() {
        return getPhases(STAGE_IMPLEMENTATION);
    }

    public synchronized List<String> getAllPhases() {
        if (phases == null) {
            phases = new ArrayList<String>();
            phases.addAll(getReferencePhases());
            phases.addAll(getServicePhases());
            phases.addAll(getImplementationPhases());
        }
        return phases;
    }

    public synchronized Map<String, Stage> getStages() {
        if (stages != null) {
            return stages;
        }
        init();

        Set<ServiceDeclaration> services;
        try {
            services = ServiceDiscovery.getInstance().getServiceDeclarations(pattern);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

        for (ServiceDeclaration d : services) {
            if (log.isLoggable(Level.FINE)) {
                log.fine(d.getResource() + ": " + d.getAttributes());
            }
            String name = d.getAttributes().get("name");
            if (name == null) {
                throw new ServiceRuntimeException("Required attribute 'name' is missing.");
            }
            String stageName = d.getAttributes().get("stage");
            if (stageName == null) {
                throw new ServiceRuntimeException("Required attribute 'stage' is missing.");
            }
            Stage stage = stages.get(stageName);
            if (stage == null) {
                throw new ServiceRuntimeException("Invalid stage: " + stage);
            }
            PhaseSorter<String> graph = stage.getSorter();
            Set<String> firstSet = stage.getFirstSet(), lastSet = stage.getLastSet();

            String before = d.getAttributes().get("before");
            String after = d.getAttributes().get("after");
            if (before != null) {
                StringTokenizer tokenizer = new StringTokenizer(before);
                while (tokenizer.hasMoreTokens()) {
                    String p = tokenizer.nextToken();
                    if (!"*".equals(p)) {
                        graph.addEdge(name, p);
                    } else {
                        firstSet.add(name);
                    }
                }
            }
            if (after != null) {
                StringTokenizer tokenizer = new StringTokenizer(after);
                while (tokenizer.hasMoreTokens()) {
                    String p = tokenizer.nextToken();
                    if (!"*".equals(p)) {
                        graph.addEdge(p, name);
                    } else {
                        lastSet.add(name);
                    }
                }
            }
            graph.addVertext(name);
            if(firstSet.size()>1) {
                log.warning("More than one phases are declared to be first: "+firstSet);
            }
            for (String s : firstSet) {
                for (String v : new HashSet<String>(graph.getVertices().keySet())) {
                    if (!firstSet.contains(v)) {
                        graph.addEdge(s, v);
                    }
                }
            }
            if(lastSet.size()>1) {
                log.warning("More than one phases are declared to be the last: "+lastSet);
            }
            for (String s : lastSet) {
                for (String v : new HashSet<String>(graph.getVertices().keySet())) {
                    if (!lastSet.contains(v)) {
                        graph.addEdge(v, s);
                    }
                }
            }

        }

        for (Stage s : stages.values()) {
            List<String> phases = s.getSorter().topologicalSort(false);
            s.getPhases().clear();
            s.getPhases().addAll(phases);
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("Stages: " + stages);
        }
        return stages;
    }

    private void init() {
        stages = new HashMap<String, Stage>();

        Stage referenceStage = new Stage(STAGE_REFERENCE);
        for (int i = 1; i < SYSTEM_REFERENCE_PHASES.length; i++) {
            referenceStage.getSorter().addEdge(SYSTEM_REFERENCE_PHASES[i - 1], SYSTEM_REFERENCE_PHASES[i]);
        }
        referenceStage.getLastSet().add(Phase.REFERENCE_BINDING);
        stages.put(referenceStage.getName(), referenceStage);

        Stage serviceStage = new Stage(STAGE_SERVICE);
        for (int i = 1; i < SYSTEM_SERVICE_PHASES.length; i++) {
            serviceStage.getSorter().addEdge(SYSTEM_SERVICE_PHASES[i - 1], SYSTEM_SERVICE_PHASES[i]);
        }
        stages.put(serviceStage.getName(), serviceStage);

        Stage implementationStage = new Stage(STAGE_IMPLEMENTATION);
        for (int i = 1; i < SYSTEM_IMPLEMENTATION_PHASES.length; i++) {
            implementationStage.getSorter().addEdge(SYSTEM_IMPLEMENTATION_PHASES[i - 1],
                                                    SYSTEM_IMPLEMENTATION_PHASES[i]);
        }
        implementationStage.getLastSet().add(Phase.IMPLEMENTATION);
        stages.put(implementationStage.getName(), implementationStage);
    }
}
