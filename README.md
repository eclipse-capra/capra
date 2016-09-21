# Capra - A Traceability Management Tool

### Background
Capra is a configurable and extendable traceability management tool. It is created in the context of an ITEA funded project called AMALTHEA4Public whose main aim is to develop a platform that will improve the development of embedded multicore and many core systems.

### How it works

Capra uses the Eclipse Modelling Framework (EMF) as its base technology and stores the traceability model as an EMF model. The traceability metamodel is not fixed and can be defined by the user. It relies on the Eclipse Extension mechanism and provides an extension point for artifacts types to be supported. To add a new type of artifact, one simply needs to add an extension to this extension point and implement the provided interfaces.


# User Guide - End Users

### Pre-requisites

Before downloading and using Capra, download Eclipse Modelling Environment and make sure you have the listed dependencies installed.

* [PlantUML](http://plantuml.com/eclipse.html): Use the nightlies [update site](http://basar.idi.ntnu.no/svn/tdt4100/anonymous/trunk/updatesite) in Eclipse's "Install new software..." feature. Version 1.1.11 or higher should be installed through this link. Older versions advertised on the website will not work! It might also be necessary to install [Graphviz](http://www.graphviz.org) binaries on your system to view the visualisation of trace links.
* [Xcore](https://wiki.eclipse.org/Xcore): Install through Eclipse's "Install new software..." feature
* [Mylyn](https://www.eclipse.org/mylyn/): Install the "Mylyn Builds Connector: Hudson/Jenkins" through Eclipse's "Install new software..." feature
* [C/C++ Development Tools](https://www.eclipse.org/cdt/): : Use Eclipse's "Install new software..." feature
* [Java Development Tools](https://www.eclipse.org/jdt): Use Eclipse's "Install new software..." feature
* [Xtend](https://eclipse.org/xtend/): Install through the Eclipse Market Place
* [Papyrus](https://eclipse.org/papyrus/): Install through the Eclipse Market Place  

### Get the source code

* Open your Eclipse Environment
* Go to File >> Import and select Git >> Projects from Git
* Use the [GitHub repository](https://github.com/Salome-Maro/TraceabilityManagement) and import all available projects to your workspace
* Build your workspace
* Make sure that all the projects have no errors.
* Click on Run >> Run Configurations and create a new Eclipse Application Configuration
* Select your running workspace
* Click Finish
* Once the new workspace opens, create or import projects that you want to use to create traceability links
* Go to perspectives and switch to the Capra perspective
* Now you can create traceability links as described in [Creating Traceability Links](#create-trace-links).

If compilation errors occur during the first build, check if any of the dependencies above are missing. Cleaning all binaries also often helps resolve issues.

### Run Capra

* Make sure that all the projects have no errors.
* Click on Run --> Run Configurations and create a new Eclipse Application Configuration
* Select your running workspace
* Click "Apply", then "Run"
* Once the new workspace opens, create or import projects that you want to  use to create traceability links
* Go to perspectives and switch to the Capra perspective
* Follow [this video](https://www.dropbox.com/s/9p76ebqvax16uc1/HVAC-Capra%20Incomplete1.mov?dl=0) to create and visualize traceability links  
**NOTE:** For better resolution, download the video first. 



# How to Extend the Tool - For Developers

## Before committing

Please make sure that *all* points in the following checklist are fullfilled before committing your work to a Capra repository:

* The code is formatted according to the built-in Eclipse code formatting rules.
* All classes and all public methods are documented.
* Each source code file has a copyright header (see below).
* The imports are organised.

Organisation of imports and code formatting can be automated by selecting them as save actions for the Java editor in the Eclipse preferences.

## Adding new source files

It is important to maintain the correct copyright messages, indicating the contributors of each file and that it is covered by the EPL. You can use automation to insert a correct copyright header.

Install the [Eclipse Releng Tools](https://wiki.eclipse.org/Development_Resources/How_to_Use_Eclipse_Copyright_Tool). They contain the copyright tool. Use the following copyright header:

```
Copyright (c) ${date} Chalmers | University of Gothenburg, rt-labs and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
 
  Contributors:
     Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
```

The Contributors entry can be replaced with the appropriate names. Use "Fix copyrights" from the context menu to add the copyrights to all relevant files in a project or folder.
