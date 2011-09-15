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

public class LayoutBuilder {

    private Entity[] elts = null;
    private int[][] conns = null;
    private Entity startEnt = null;
    private int currentMaxLevel = 0;

    /**
     * Constructor which takes set of entities and their connection matrix
     */
    public LayoutBuilder(Entity[] entities, int[][] connections) {
        elts = entities;
        conns = connections;
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

        /**
         * Finding the starting entity
         */
        for (int i = 0; i < elts.length; i++) {
            //System.out.println("ELts "+elts.length);
            Entity ent = elts[i];
            if (isConnected(ent.getId())) {
                setPosition(ent, 0, 0);
                startEnt = ent;
                //System.out.println("startEnt "+ent.getId());
                break;
            }

        }

        if (startEnt != null) {
            assignPositions(startEnt);
        }

        assignPositionsOfOtherConncetedEntities();//such as a different cluster of components
        assignPositionsOfIdleEntities();
        assignCoordinates();

        return elts;

    }

    private void assignPositionsOfIdleEntities() {

        for (Entity ent : elts) {
            if (!ent.isPossitionSet()) {

                setPosition(ent, currentMaxLevel++, 0);
            }
        }
    }

    private void assignPositionsOfOtherConncetedEntities() {

        for (Entity ent : elts) {
            if (!ent.isPossitionSet() && isConnected(ent.getId())) {
                assignPositions(ent);
            }
        }
    }

    private void assignCoordinates() {

        for (Entity ent : elts) {
            ent.setX(ent.getParent().getX() + ent.getStartPosition());
            ent.setY(ent.getParent().getY() + ent.getStartPosition() / 2);
        }
    }

    private void assignPositions(Entity ent) {
        int id = ent.getId();
        int[] entConns = conns[id];

        for (int i = 0; i < entConns.length; i++) {
            if (entConns[i] == 1) {
                Entity nextEnt = findEntity(i);

                //				if(nextEnt.isPossitionSet()){
                //					currentMaxLevel = nextEnt.getLevel()+1; // for diagram clearness purpose
                //				}
                if (nextEnt != null && !nextEnt.isPossitionSet()) {
                    setPosition(nextEnt, currentMaxLevel, ent.getLane() + 1);
                    assignPositions(nextEnt);
                }
            }

        }
        currentMaxLevel = ent.getLevel() + 1;
    }

    private Entity findEntity(int i) {

        for (Entity ent : elts) {
            if (ent.getId() == i) {
                return ent;
            }
        }
        return null;
    }

    /**
     * If there's at least 1 connection, this will return true
     */
    private boolean isConnected(int id) {
        int[] entConns = conns[id];

        //System.out.println("entConns "+entConns.length);
        for (int i = 0; i < entConns.length; i++) {

            if (entConns[i] == 1) {
                return true;
            }

        }

        return false;
    }

    private void setPosition(Entity ent, int level, int lane) {
        ent.setLevel(level);
        ent.setLane(lane);
        ent.setPossitionSet(true);
    }

    public Entity getStartEnt() {
        return startEnt;
    }

    public void setStartEnt(Entity startEnt) {
        this.startEnt = startEnt;
    }

}
