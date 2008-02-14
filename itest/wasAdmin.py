"""
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
"""
import sys

def getCellName():
    """Return the name of the cell connected to"""
    return AdminControl.getCell()

def getNodeName():
    """Return the name of the node connected to"""
    return AdminControl.getNode()

def startApplicationOnServer(appName,serverName):
    """Start the named application on one server"""
    print "startApplicationOnServer: Entry. appname=%s servername=%s" % ( appName,serverName )
    cellName = getCellName()
    nodeName = getNodeName()
    # Get the application manager 
    appManager = AdminControl.queryNames('cell=%s,node=%s,type=ApplicationManager,process=%s,*' %(cellName,nodeName,serverName))
    print "startApplicationOnServer: appManager=%s" % ( repr(appManager) )
    # start it
    rc = AdminControl.invoke(appManager, 'startApplication', appName)
    print "startApplicationOnServer: Exit. rc=%s" % ( repr(rc) )

def stopApplicationOnServer(appName,serverName):
    """Stop the named application on one server"""
    print "stopApplicationOnServer: Entry. appname=%s servername=%s" % ( appName,serverName )
    cellName = getCellName()
    nodeName = getNodeName()
    # Get the application manager 
    appManager = AdminControl.queryNames('cell=%s,node=%s,type=ApplicationManager,process=%s,*' %(cellName,nodeName,serverName))
    print "stopApplicationOnServer: appManager=%s" % ( repr(appManager) )
    # start it
    rc = AdminControl.invoke(appManager, 'stopApplication', appName)
    print "stopApplicationOnServer: Exit. rc=%s" % ( repr(rc) )

def installApplicationOnServer( fileName, appName, contextRoot, serverName ):
    """Install given application on the named server using given context root"""
    print "installApplicationOnServer: fileName=%s appName=%s contextRoot=%s ServerName=%s" % ( fileName, appName,contextRoot,serverName )
    AdminApp.install(fileName,'[-appname ' + appName + ' -contextroot ' + contextRoot  + ' -server ' + serverName + ' -usedefaultbindings ]')
    AdminConfig.save()
    """modify classloader model for application"""
    deploymentID = AdminConfig.getid('/Deployment:' + appName + '/')
    deploymentObject = AdminConfig.showAttribute(deploymentID, 'deployedObject')
    classldr = AdminConfig.showAttribute(deploymentObject, 'classloader')
    print AdminConfig.showall(classldr)
    AdminConfig.modify(classldr, [['mode', 'PARENT_LAST']])
    """Modify WAR class loader model"""
    AdminConfig.show(deploymentObject, 'warClassLoaderPolicy')
    AdminConfig.modify(deploymentObject, [['warClassLoaderPolicy', 'SINGLE']])
    AdminConfig.save()

def uninstallApplicationOnServer( appName ):
    """Delete the named application from the cell"""
    AdminApp.uninstall( appName )
    AdminConfig.save()



"""-----------------------------------------------------------
   Phyton script to interface with WAS Admin/Management Tools
-----------------------------------------------------------"""

if len(sys.argv) < 1:
    print "wasAdmin.py : need parameters : functionName [args]"
    sys.exit(0)
if(sys.argv[0] == 'installApplicationOnServer'):
    installApplicationOnServer(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
elif(sys.argv[0] == 'startApplicationOnServer'):
    startApplicationOnServer(sys.argv[1], sys.argv[2])
elif(sys.argv[0] == 'uninstallApplicationOnServer'):
    uninstallApplicationOnServer(sys.argv[1])
else:
    print "Exiting without doing anything"

