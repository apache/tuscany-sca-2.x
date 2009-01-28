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

package services.jcr;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;

import services.Album;

public class AlbumImpl implements Album {
    private String gallery;
    private String album;
    private String location;
    private Repository repository=null;
    private Session session=null;
    
    @Property
    public void setGallery(String gallery) {
        this.gallery = gallery;
        this.location = null;
    }
    @Property
    public void setAlbum(String album) {
        this.album = album;
        this.location = null;
    }
    
    protected String getLocation() {
        if (location == null) {
            location = gallery + "/" + album + "/"; 
        }
        return location;
        
    }

    @Init
    public void init() {
        try {
            URL albumURL = this.getClass().getClassLoader().getResource(getLocation());
            if(albumURL != null) {
            	repository = new TransientRepository();
                session = repository.login(
                    new SimpleCredentials("username", "password".toCharArray()));
                try {
                  File album = new File(albumURL.toURI());
                  if (album.isDirectory() && album.exists()) {
                      String[] listPictures = album.list(new ImageFilter(".jpg"));
                    for(String image : listPictures) {
                    	Node root=session.getRootNode();
                    	Node picNode=root.addNode(image);
                        InputStream inFile = getClass().getClassLoader().getResourceAsStream(getLocation()+image);
                        picNode.setProperty("image", inFile );
                        picNode.setProperty("name", image);
                        picNode.setProperty("location", getLocation()+image);
                    	//image = getLocation() + image;
                        //pictures.add(image);
                      }
                  }
                            
                  session.save();
                }catch (Exception e){
                    // FIXME: ignore for now
                    e.printStackTrace();     	
                }
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }   
    }
    
    public String[] getPictures() {
      List<String> pictures = new ArrayList<String>();
    	
      try{	
    	Node root=session.getRootNode();
        NodeIterator nodes = root.getNodes();

        while(nodes.hasNext()){
        	Node node=nodes.nextNode();
        	if(node.getPath().equals("/jcr:system")) continue;
        	
        	pictures.add(node.getProperty("location").getString());
        	//System.out.println(node.getProperty("name").getString());
        	//System.out.println(node.getPath());
        }
      }catch (Exception e) {
          // FIXME: ignore for now
          e.printStackTrace();
      }
      
      String[] pictureArray = new String[pictures.size()];
      pictures.toArray(pictureArray);
      removeNodes();
      return pictureArray;
    }
    
    
    public void removeNodes(){
      try{
    	Node root=session.getRootNode();
        NodeIterator nodes = root.getNodes();
        while(nodes.hasNext()){
        	Node node=nodes.nextNode();
        	if(node.getPath().equals("/jcr:system")) continue;
        	else node.remove();
        }
        session.save();
      }catch (Exception e) {
          // FIXME: ignore for now
          e.printStackTrace();
      }
        
    }
    /**
     * Inner fileFilter class
     */
    private class ImageFilter implements FilenameFilter {
        String afn;
        ImageFilter(String afn) { this.afn = afn; }
        public boolean accept(File dir, String name) {
          // Strip path information:
          String f = new File(name).getName();
          return f.indexOf(afn) != -1;
        }
      } ///:~ 

}
