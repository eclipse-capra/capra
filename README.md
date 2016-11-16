# Capra - A Traceability Management Tool

## Background
Capra is a configurable and extendable traceability management tool. It is created in the context of an ITEA funded project called AMALTHEA4Public whose main aim is to develop a platform that will improve the development of embedded multicore and many core systems.

## How it works

Capra uses the Eclipse Modelling Framework (EMF) as its base technology and stores the traceability model as an EMF model. The traceability metamodel is not fixed and can be defined by the user. It relies on the Eclipse Extension mechanism and provides an extension point for artifacts types to be supported. To add a new type of artifact, one simply needs to add an extension to this extension point and implement the provided interfaces.


# Installing Capra from Sourcecode

## Pre-requisites

Before downloading and using Capra, download Eclipse Modelling Environment and make sure you have the listed dependencies installed.

* [Xcore](https://wiki.eclipse.org/Xcore): Install through Eclipse's "Install new software..." feature
* [Xtend](https://eclipse.org/xtend/): Install through the Eclipse Market Place

## Get the source code

* Open your Eclipse Environment
* Go to File --> Import and select Git --> Projects from Git
* Use the [Capra Git repository](https://git.eclipse.org/r/capra/org.eclipse.capra) and import all available projects to your workspace. Information about the developer resources and the source code repositories is available at [the project website](https://projects.eclipse.org/projects/modeling.capra/developer).
* All dependencies are listed in the target platform file org.eclipse.capra.releng.target/org.eclipse.capra.releng.target.target. Set it as the active target platform by opening it and clicking on "Set as Target Platform" in the upper right corner. Eclipse will now download all dependencies. Note that this will take a while. Check the progress in the lower right corner.
* Build your workspace
* Make sure that all the projects have no errors.
* Click on Run --> Run Configurations and create a new Eclipse Application Configuration
* Select your running workspace
* Click Finish
* Once the new workspace opens, create or import projects that you want to use to create traceability links
* Go to perspectives and switch to the Capra perspective
* Now you can create traceability links as described in [Creating Traceability Links](#create-trace-links).

If compilation errors occur during the first build, check if any of the dependencies above are missing. Cleaning all binaries also often helps resolve issues.

## Run Capra

* Make sure that all the projects have no errors.
* Click on Run --> Run Configurations and create a new Eclipse Application Configuration
* Select your running workspace
* Click "Apply", then "Run"
* Once the new workspace opens, create or import projects that you want to  use to create traceability links
* Go to perspectives and switch to the Capra perspective
* Follow [this video](https://www.dropbox.com/s/9p76ebqvax16uc1/HVAC-Capra%20Incomplete1.mov?dl=0) to create and visualize traceability links  
**NOTE:** For better resolution, download the video first. 



# Contributing to Capra

## Becoming a member of the Eclipse Community

In order to use the Eclipse development infrastructure and to contribute to Capra (and other Eclipse projects), it is necessary to create an [Eclipse account](https://dev.eclipse.org/site_login/createaccount.php). With this account, you will be able to log in to the different services (such as [Bugzilla](https://bugs.eclipse.org/bugs/describecomponents.cgi?product=Capra), the [Eclipse Wiki](https://wiki.eclipse.org/Main_Page), or [Hudson](https://hudson.eclipse.org/capra/)) and push code changes to [Gerrit](https://git.eclipse.org/r/#/admin/projects/capra/org.eclipse.capra).

After you have created the account, you need to sign the [Eclipse Committer Agreement](https://eclipse.org/legal/#CommitterAgreements) (ECA). Depending on whether you work on your own time or for a company, different conditions apply. Please familiarise yourself with the legal issues, ask your employer for help if required, and then sign the ECA [here](https://dev.eclipse.org/site_login/myaccount.php#open_tab_cla).

## Cloning from Git/Gerrit

Instead of using the Git repository listed above, use the Gerrit repository. You can copy the SSH or HTTPS links from the [Capra Gerrit page](https://git.eclipse.org/r/#/admin/projects/capra/org.eclipse.capra). This will allow you to push your changes to Gerrit where the project committers can review them before adding them to the codebase.

* Switch to the Git Perspective, select Clone a Git Repository (icon with a blue arrow), then select Gerrit and follow the wizard.
* Import all projects into your work space
* Create a branch based on the `develop` branch. All development should be based on this branch, following the [GitFlow](http://nvie.com/posts/a-successful-git-branching-model/) approach. Your local changes should go into your own branch. We suggest to use the format `<username>/feature/<feature-name>`. Instead of `feature`, other identifiers such as `fix` can be used.

## Sharing your Idea with the Community

We keep track of the features for Capra in our [Bugzilla](https://bugs.eclipse.org/bugs/describecomponents.cgi?product=Capra). If you plan to contribute your own features, please open a bug in the correct component there. The bug summary should contain the "[feature]" keyword so we know that this is not indeed a bug report. Please provide a short summary and a description of what you are doing. Use the comments of the bug to describe your progress. Other members of the community might engage you for discussions, so please take their opinions into account and react to their suggestions and feedback.

## Building and Testing

Capra uses Maven/Tycho. To check if your changes work, run maven in the root folder:

    mvn clean verify

This will create binaries for Capra and run all tests. Code should only be committed if all tests passed!

To run Capra from within Eclipse, see the instructions above.

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


    Copyright (c) ${date} Chalmers | University of Gothenburg, rt-labs and others.
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    which accompanies this distribution, and is available at
    http://www.eclipse.org/legal/epl-v10.html
    
    Contributors:
       Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation

The Contributors entry can be replaced with the appropriate names. Use "Fix copyrights" from the context menu to add the copyrights to all relevant files in a project or folder.

## Pushing changes to Gerrit

If all pre-requisites listed above are fulfilled, the code can be pushed to Gerrit. Please note that Gerrit combines several commits into one change that will show up using the commit message of the last commit. Make sure that this commit message actually describes the changes you are pushing. Optionally, you can squash all commits into one before pushing to Gerrit.

These are the steps to push a change to Gerrit:

* Make sure that the code compiles and the tests run through by running `mvn clean verify` 
* (Optional) Squash all local commits into one
* Make sure your commit message describes the feature you contribute
* Sign-off the commit: add a Signed-off-by footer to the commit message. If this is missing, Gerrit will not accept your push!
* Push your changes to Gerrit. You can either do this through Eclipse or manually on the command line with a command like this: `git push ssh://<username>@git.eclipse.org:29418/capra/org.eclipse.capra.git HEAD:refs/for/develop` Please note that it is important to use the `HEAD:refs/for/develop` addition which indicates that the push is to Gerrit (`refs/for/`) and which branch the changes should be merged to (`develop`).

The Sign-off needs to be in the footer of the commit message, i.e., separated from the commit message by a new line and in the same block.

Once your change has been pushed to Gerrit, one of the committers in the project will review the change and Hudson will try to build your code. You will be notified about the progress by email, using the email address you used in the Signed-off-by message. If your change is accepted, it will be merged into one of the branches in the main Git repository. If there are comments or Hudson fails to build the code, you need to make the necessary changes, amend your commit, and push again.

* Make the necessary local changes
* Amend last commit via `git commit --amend`
* Add Gerrit Change-Id to your commit message 

Change-Id and sign-off both need to be in the footer.

## Additional information

More information about contributing code via Gerrit and how to use the Eclipse Git repositories can be found here:

* A good overview of a workflow with Gerrit/Bugzilla/Hudson is provided [here](https://www.eclipse.org/community/eclipse_newsletter/2013/september/article4.php)
* https://wiki.eclipse.org/Gerrit
* https://wiki.eclipse.org/Development_Resources/Contributing_via_Git
* https://wiki.eclipse.org/Stardust/Contributing_via_Gerrit
* https://wiki.eclipse.org/CDT/contributing