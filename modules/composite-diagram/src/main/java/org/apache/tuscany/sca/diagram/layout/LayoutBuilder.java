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
import java.util.List;

public class LayoutBuilder {

    private Entity[] elts = null;
    private int[][] conns = null;
    private int maxLevels = 8;

    /**
     * Constructor which takes set of entities and their connection matrix
     * @param maxLevels TODO
     */
    public LayoutBuilder(Entity[] entities, int[][] connections, int maxLevels) {
        elts = entities;
        conns = connections.clone();
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

        assignCoordinates();

        return elts;

    }

    private void assignCoordinates() {

        for (Entity ent : elts) {
            ent.setX(ent.getParent().getX() + ent.getStartPosition());
            ent.setY(ent.getParent().getY() + ent.getStartPosition() / 2);
        }
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
        ent.setLevel(level);
        ent.setLane(lane);
        ent.setPositionSet(true);
    }

    /**
     * Perform a topological sort on the graph so that we can place the entities into level/lane grids
     */
    private void sortEntities() {
        int lane = 0;
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
                    return;
                }
            }
            int level = 0;
            for (int i : ids) {
                setPosition(findEntity(i), level++, lane);
                if (maxLevels > 0 && (level % maxLevels == 0)) {
                    level = 0;
                    lane++;
                }
                for (int j = 0; j < conns.length; j++) {
                    // Remove the connections from i
                    conns[i][j] = 0;
                }
            }
            lane++;
        }
    }

}
