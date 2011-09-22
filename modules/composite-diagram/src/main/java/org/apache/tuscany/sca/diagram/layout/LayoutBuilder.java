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

package org.apache.tuscany.sca.diagram.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tuscany.sca.diagram.artifacts.Constant;

public class LayoutBuilder {

    private Entity[] elts;
    private int[][] conns;
    private int maxLevels = 8;

    private int totalLevel;
    private int totalLane;

    private int totalHeight;
    private int totalWidth;

    private int[][] graph;

    /**
     * Constructor which takes set of entities and their connection matrix
     *
     * @param entities
     * @param connections
     * @param maxLevels
     */
    public LayoutBuilder(Entity[] entities, int[][] connections, int maxLevels) {
        elts = entities;
        graph = connections; // Keep the original connections

        // Clone the connections
        int len = connections.length;
        conns = new int[len][];
        for (int i = 0; i < len; i++) {
            conns[i] = new int[len];
            for (int j = 0; j < len; j++) {
                conns[i][j] = connections[i][j];
            }
        }
        this.maxLevels = maxLevels;
    }

    /**
     * Layout Building Algorithm
     * ~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Here we position (i.e. assigning a level and a lane) all Entities
     * in a unique cell of a grid.
     * 
     * 		 	lane0	  lane1	  lane2	  lane3 ....
     * 		 	 _______________________________
     * level0	|		|		|		|		|
     * 			|_______|_______|_______|_______|
     * level1	|		|		|		|		|
     * 			|_______|_______|_______|_______|
     * level2	|		|		|		|		|     
     * 
     * 1) Determining the Entity at level0, lane0 (starting entity)
     * 		-First Entity in the list of Entities which has one or more adjacent Entities
     * 		-If there is only one Entity it will eventually chosen
     * 
     * 2) Get connected Entities of starting Entity. 
     * 		* If there are connected entities;
     * 			*For each connected Entity; 
     * 				*We assign a corresponding level and a lane
     * 				*Then recurse the procedure for connections of the assigned Entity
     * 	
     * 
     */
    public Entity[] placeEntities() {

        sortEntities();

        // Build the grid for entities
        Entity[][] grid = new Entity[totalLane + 1][totalLevel + 1];
        int[] height = new int[totalLevel + 1];
        int[] width = new int[totalLane + 1];

        for (Entity e : elts) {
            grid[e.getLane()][e.getLevel()] = e;
            if (height[e.getLevel()] < e.getHeight() + Constant.COMPONENT_DEFAULT_HEIGHT) {
                height[e.getLevel()] = e.getHeight() + Constant.COMPONENT_DEFAULT_HEIGHT;
            }
            if (width[e.getLane()] < e.getWidth() + Constant.COMPONENT_DEFAULT_WIDTH) {
                width[e.getLane()] = e.getWidth() + Constant.COMPONENT_DEFAULT_WIDTH;
            }
        }

        for (int i = 1; i < totalLane + 1; i++) {
            width[i] += width[i - 1];
        }

        for (int j = 1; j < totalLevel + 1; j++) {
            height[j] += height[j - 1];
        }

        totalWidth = width[totalLane];
        totalHeight = height[totalLevel];

        for (int i = 0; i < totalLane + 1; i++) {
            for (int j = 0; j < totalLevel + 1; j++) {
                Entity ent = grid[i][j];
                if (ent != null) {
                    int w = ent.getLane() == 0 ? 0 : width[i - 1];
                    ent.setX(ent.getParent().getX() + ent.getStartPosition() + w);
                    int h = ent.getLevel() == 0 ? 0 : height[j - 1];
                    ent.setY(ent.getParent().getY() + ent.getStartPosition() / 2 + h);
                }
            }
        }

        return elts;

    }

    private Entity findEntity(int i) {

        for (Entity ent : elts) {
            if (ent.getId() == i) {
                return ent;
            }
        }
        return null;
    }

    private void setPosition(Entity ent, int level, int lane) {
        if (totalLane < lane) {
            totalLane = lane;
        }
        if (totalLevel < level) {
            totalLevel = level;
        }
        ent.setLevel(level);
        ent.setLane(lane);
        ent.setPositionSet(true);
    }

    /**
     * http://en.wikipedia.org/wiki/Coffman%E2%80%93Graham_algorithm#The_algorithm
     * @param sorted
     * @param e1
     * @param e2
     * @return
     */
    private int compareEntities(List<Integer> sorted, int e1, int e2) {
        List<Integer> neighbors1 = findIncomingNeighbors(e1);
        List<Integer> neighbors2 = findIncomingNeighbors(e2);

        if (neighbors1.isEmpty() && neighbors2.isEmpty()) {
            return 0;
        } else if (neighbors1.isEmpty()) {
            return -1;
        } else if (neighbors2.isEmpty()) {
            return 1;
        }

        int max = graph.length + 1;
        int n1 = max;
        int n2 = max;
        for (int i = sorted.size() - 1; i >= 0; i--) {
            if (neighbors1.contains(sorted.get(i))) {
                n1 = i;
            }
            if (neighbors2.contains(sorted.get(i))) {
                n2 = i;
            }
            if (n1 == n2) {
                // Need to try the 2nd most recently added incoming neighbor
                // Reset the indexes and continue
                n1 = max;
                n2 = max;
                continue;
            }
        }
        return n1 - n2;
    }

    private List<Integer> findIncomingNeighbors(int e1) {
        // Get all the inbound connections for a given entity 
        List<Integer> ins = new ArrayList<Integer>();
        for (int i = 0; i < graph.length; i++) {
            if (graph[i][e1] == 1) {
                ins.add(i);
            }
        }
        return ins;
    }

    /**
     * Perform a topological sort on the graph so that we can place the entities into level/lane grids
     * http://en.wikipedia.org/wiki/Coffman%E2%80%93Graham_algorithm#The_algorithm
     */
    List<Integer> sortEntities() {
        int lane = 0;
        final List<Integer> sorted = new ArrayList<Integer>();
        while (true) {
            List<Integer> ids = new ArrayList<Integer>();
            for (int i = 0; i < conns.length; i++) {
                Entity ent = findEntity(i);
                if (ent.isPositionSet()) {
                    continue;
                }
                boolean beingConnected = false;
                for (int j = 0; j < conns.length; j++) {
                    if (conns[j][i] == 1) {
                        beingConnected = true;
                        break;
                    }
                }
                if (!beingConnected) {
                    ids.add(i);
                }
            }

            if (ids.isEmpty()) {
                boolean end = true;
                // There might be circular dependencies
                for (Entity e : elts) {
                    if (!e.isPositionSet()) {
                        // Pick the first one 
                        ids.add(e.getId());
                        end = false;
                        break;
                    }
                }
                if (end) {
                    return sorted;
                }
            }
            int level = 0;
            Collections.sort(ids, new Comparator<Integer>() {

                @Override
                public int compare(Integer e1, Integer e2) {
                    return compareEntities(sorted, e1, e2);
                }
            });
            for (int i : ids) {
                sorted.add(i);

                if (maxLevels > 0 && level > 0 && (level % maxLevels == 0)) {
                    // Overflow to the next lane
                    level = 0;
                    lane++;
                }

                setPosition(findEntity(i), level++, lane);
                for (int j = 0; j < conns.length; j++) {
                    // Remove the connections from i
                    conns[i][j] = 0;
                }
            }
            lane++;
        }
    }

    public int getTotalLevel() {
        return totalLevel;
    }

    public int getTotalLane() {
        return totalLane;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public int getTotalWidth() {
        return totalWidth;
    }

}
