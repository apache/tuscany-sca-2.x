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

package org.apache.tuscany.sca.data.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.DAS;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLHelper;

/**
 * Facade to hide DAS implementation details of handling commands
 * 
 * @version $Rev$ $Date$
 */
public class DataAccessEngine {
    private final DAS das;

    public DataAccessEngine(DAS das) {
        this.das = das;
    }

    public DataObject executeGet(ArrayList keyVals, String table, String key) {//TODO need to consider compound keys
        try {
            String sqlQuery = "select * from " + table.toUpperCase();
            List<String> keys = null;
            
            if(key == null) {
                if(keyVals != null && keyVals.size() == 1) {
                    sqlQuery += " where ID = " + keyVals.get(0);
                }            	
            } else {//can be other than ID , can be compount keys
            	keys = getKeys(key);
            	if(keyVals.size() != keys.size()) {
            		throw new RuntimeException("One or more PK values missing");
            	}
            	            	
            	sqlQuery += " where ";
            	
            	for(int i=0; i<keys.size(); i++) {
            		sqlQuery += keys.get(i)+" = ? AND ";
            	}
            	
            	sqlQuery = sqlQuery.substring(0, sqlQuery.lastIndexOf(" AND "));
            }
            
            Command command = this.das.createCommand(sqlQuery);
            
            if(key != null) {
            	for(int i=1; i<=keyVals.size(); i++) {
            		command.setParameter(i, keyVals.get(i-1));
            	}
            }
            
            DataObject returnDO = command.executeQuery();          
            return returnDO;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * @param table table'e Type name - should be same as table name
     * @param key column's Property name - should be same as column name
     * @return
     */
    public Map<Object, DataObject> executeGetAll(String table, String key) {
        try {
            String sqlQuery = "select * from " + table.toUpperCase();
            Command command = this.das.createCommand(sqlQuery);
            DataObject result = command.executeQuery();
            List<String> keys = getKeys(key);
            List<DataObject> resultDataObjects = result.getList(table);
            
            return getMappedDataObjects(resultDataObjects, keys);
        } catch (Exception e) {
            //e.printStackTrace();
        	throw new RuntimeException(e);
        }
    }
    
    public Map<Object, DataObject> executeQuery(String queryString, String table, String key) {
        try {
            Command command = this.das.createCommand(queryString);
            DataObject result = command.executeQuery();
            List<String> keys = getKeys(key);
            List<DataObject> resultDataObjects = result.getList(table);
            
            return getMappedDataObjects(resultDataObjects, keys);
        } catch (Exception e) {
            //e.printStackTrace();
        	throw new RuntimeException(e);
        }
    }
    
    //origDataObject should be with change summary. table, pk is already known to DAS, so no need to have these here
    public void executePut(DataObject origDataObject) {
    	this.das.applyChanges(origDataObject);
    	return;
    }
    
    //return PK/s
    public ArrayList executePost(DataObject origDataObject, String table, String key){
    	//TODO check that PKs are present before insert - this is not correct for auto incr PKs, so let it be upto user whether to send PK or not
    	/*List<String> keys = getKeys(key);
    	for(int i=0; i<keys.size(); i++) {
    		String currentKey = keys.get(i);
    		Object currentKeyValue = origDataObject.get(currentKey);
    		if(currentKeyValue == null) {
    			throw new RuntimeException("PK missing during INSERT");
    		}
    	}*/
    	
    	String sqlString = "insert into "+table+" (";
    	List props = origDataObject.getType().getProperties();
    	if(props.size() != 0) {
	    	for(int i=0; i<props.size(); i++) {
	    		String currPropName = ((Property)props.get(i)).getName();
	    		if(origDataObject.get(currPropName) != null) {
	    			sqlString += currPropName+",";
	    		}
	    	}
	    	sqlString = sqlString.substring(0, sqlString.length()-1);
	    	sqlString += ") values (";
	    	for(int i=0; i<props.size(); i++) {
	    		String currPropName = ((Property)props.get(i)).getName();
	    		if(origDataObject.get(currPropName) != null) {
	    			sqlString += "?,";
	    		}
	    	}

	    	sqlString = sqlString.substring(0, sqlString.length()-1);
	    	sqlString += ")";
	    	
	    	Command insertCommand = this.das.createCommand(sqlString);
	    	int paramIdx = 1;
	    	for(int i=1; i<=props.size(); i++) {
	    		String currPropName = ((Property)props.get(i-1)).getName();
	    		if(origDataObject.get(currPropName) != null) {
	    			insertCommand.setParameter(paramIdx, origDataObject.get(currPropName));
	    			paramIdx++;
	    		}
	    	}
	    	
	    	insertCommand.execute();
	    	
	    	//there can be different possibilities
	    	//1- there is autogen key - insertCommand.getGeneratedKey() will return value and not exception
	    	//2- there is no autogen key - insertCommand.getGeneratedKey() will return exception and value needs to be taken from origDataObject
	    	//for 2 it is straight forward to know the column name same as property name
	    	//for 1 it is possible for only 1 column so no question of compound PK

	    	//now get the PK/s to be returned
	    	ArrayList pks = new ArrayList();

	    	try {
	    		int newId = insertCommand.getGeneratedKey();
	    		pks.add(newId);
	    		return pks;
	    	} catch(Exception e) {
		    	List<String> keys = getKeys(key);
		    	for(int i=0; i<keys.size(); i++) {
		    		String currentKey = keys.get(i);
		    		Object currentKeyValue = origDataObject.get(currentKey);
		    		if(currentKeyValue == null) {
		    			throw new RuntimeException("PK missing during INSERT");
		    		}
		    		pks.add(currentKeyValue);
		    	}
		    	return pks;
	    	}

    	}
    	return null;
    }
    
    //when keyVal is null can be used as deleteAll
    public void executeDelete(ArrayList keyVals, String table, String key) {
        try {
            String sqlQuery = "select * from " + table.toUpperCase();
            List<String> keys = null;
            
            if(key == null) {
                if(keyVals != null && keyVals.size() == 1) {
                    sqlQuery += " where ID = " + keyVals.get(0);
                }            	
            } else {//can be other than ID , can be compount keys
            	keys = getKeys(key);
            	if(keyVals.size() != keys.size()) {
            		throw new RuntimeException("One or more PK values missing");
            	}
            	            	
            	sqlQuery += " where ";
            	
            	for(int i=0; i<keys.size(); i++) {
            		sqlQuery += keys.get(i)+" = ? AND ";
            	}
            	
            	sqlQuery = sqlQuery.substring(0, sqlQuery.lastIndexOf(" AND "));
            }
            
            Command command = this.das.createCommand(sqlQuery);
            
            if(key != null) {
            	for(int i=1; i<=keyVals.size(); i++) {
            		command.setParameter(i, keyVals.get(i-1));
            	}
            }
            
            DataObject result = command.executeQuery();
            List<DataObject> resultDOs = result.getList(table);
            if(resultDOs != null) {
            	for(int i=0; i<resultDOs.size(); i++) {
            		((DataObject)resultDOs.get(i)).delete();
            	}
            }
            this.das.applyChanges(result);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public Map<Object, DataObject> getMappedDataObjects(List<DataObject> resultDataObjects, List<String> keys) {
    	Map<Object, DataObject> resultMap = new HashMap<Object, DataObject>();
    	ArrayList<Object> keyCols = null;
        for(int j=0; j<resultDataObjects.size(); j++) {
        	DataObject currentDO = resultDataObjects.get(j);
        	
            keyCols = new ArrayList<Object>();
        	for(int i=0; i<keys.size(); i++) {
        		String currentKey = keys.get(i);
        		Object currentKeyValue = currentDO.get(currentKey);
        		keyCols.add(currentKeyValue);
        	}
        	
        	resultMap.put(keyCols, currentDO);          	
        }
        return resultMap;
    }
    
    public static List<String> getKeys(String key) {
    	String[] keys = key.split(",");
    	return Arrays.asList(keys);
    }
    
    public DataObject executeCommand(String commandName) {
        try {
            Command command = this.das.getCommand(commandName);
            return command.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DataObject executeCommand(String commandName, String xPath) {
        DataObject root = executeCommand(commandName);
        return root.getDataObject(xPath);
    }
}
