# Defining Target Platforms

We are using the TPD editor for target platforms which can be found at <https://github.com/eclipse-cbi/targetplatform-dsl>. It currently only works in Eclipse versions >= 2018.09, but can generate target definitions for any Eclipse platform.

It allows defining target definitions with a DSL and generating `.target` files from them automatically. During the generation, the latest versions of the selected features are selected and added to the definitions.

We are following a scheme that is used by some other Eclipse projects, e.g., [EASE](https://git.eclipse.org/c/ease/org.eclipse.ease.core.git/tree/releng/org.eclipse.ease.releng.target).

# Building and developing
	
Tycho will use the file `org.eclipse.capra.releng.target.target` for builds. This file has to be manually overridden when a new target platform has been created.

When developing, any of the target platforms can be used by first generating a target definition and then setting it as the current target platform. This allows developers to check compatibility in different Eclipse versions.

# Available targets

There are target platform files available for each Eclipse version starting from Eclipse Neon. Each target platform will draw from the corresponding repositories for the Eclipse platform and Eclipse Orbit.
	