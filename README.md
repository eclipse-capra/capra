# Eclipse Capra - A Traceability Management Tool

## Background
Eclipse Capra is a configurable and extendable traceability management tool. Eclipse Capra is a traceability management tool. It allows the creation of trace links between arbitrary artefacts, provides features to edit them and keep them consistent, and visualise the relationships between them. This includes traceability matrices and graph visualisations that are helpful for reporting and change impact analysis.

## How it works

In essence, Eclipse Capra allows the creation of trace links between arbitrary artefacts, as long as an adapter for these artefacts is available. This way, a trace link can be created between EMF model elements, source code files supported by the Eclipse Platform (e.g., Java, C, Python), or tasks from an issue tracking system supported by Eclipse Mylyn. External artefacts for which the Eclipse Platform does not offer built-in support can also be linked if a fitting adapter is provided. Built-in capabilities allow linking to Office documents and documents hosted by Google Docs. Through its EMF adapter, Capra currently supports elements from UML, SysML, AADL, EAST-ADL, or AUTOSAR models created in, e.g., Eclipse Papyrus, Eclipse EATOP, or ARTOP. The same adapter allows tracing from and to requirements modelled in ProR. Furthermore, adapters for test case executions managed by a continuous integration server like Hudson or Jenkins can be traced to.

Once these trace links are established, Capra offers features to manage them. If a model element that is linked to is moved, e.g., Capra will notify the user and allow changing the link accordingly. The same support is given for model elements that are deleted or renamed. Quick fixes are available to fix most isses in a semi-automatic fashion.

Capra also offers a visualisation of the trace links that allows developers to traverse the relationships established through the links and understand how the different artefacts are connected. This is helpful when assessing the impact a change has (e.g., which design artefacts need to be adapted when a requirement has changed?) or when trying to understand how the design artefacts in a complex development project are connected. In addition, Capra can display traceability matrices, as requested by standards like ISO 26262.

The tool is highly extensible. The meta-model used for the traceability links can easily be adapted to a specific end-user's needs. Capra's modular architecture allows exchanging the persistence, the visualisation, and the management modules easily. New adapters for additional artefacts can easily be added without re-compilation. This allows end-users to customise almost every aspect of the tool if needed. At the same time, we provide sensible defaults that will allow the majority of users to use Capra out of the box without extensive configuration.

## Building the Eclipse Capra source distribution

Please note that the `master` branch of Eclipse Capra always contains the latest stable release. All development takes place in the `develop` branch, following a [gitflow](http://nvie.com/posts/a-successful-git-branching-model/) model.

To build Eclipse Capra, follow these steps:

* Open your Eclipse Environment
* Go to File --> Import and select Git --> Projects from Git
* Clone the [Eclipse Capra Git repository](https://git.eclipse.org/c/capra/org.eclipse.capra) and import all available projects to your workspace. Information about the developer resources and the source code repositories is available at [the project website](https://projects.eclipse.org/projects/modeling.capra/developer).
* Make sure to switch to the *develop* branch if you want to use the current version of Eclipse Capra
* To make sure Eclipse can resolve all dependencies, set the correct target platform file for your version of Eclipse. All target platform files can be found in `releng/org.eclipse.capra.releng.target`. Open the correct file and set it as the active target platform by opening it and clicking on "Set as Target Platform" in the upper right corner. Eclipse will now download all dependencies. Note that this will take a while. Check the progress in the lower right corner.
* Build your workspace
* Make sure that all the projects have no errors.

If compilation errors occur during the first build, check if any of the dependencies above are missing. Cleaning all binaries also often helps resolve issues.

To start Eclipse Capra, follow these steps:

* Make sure that all the projects have no errors.
* Click on Run --> Run Configurations and create a new Eclipse Application Configuration
* Select your running workspace
* Click "Apply", then "Run"
* Once the new workspace opens, create or import projects that you want to  use to create traceability links
* Go to perspectives and switch to the Capra perspective
* Follow [this video](https://youtu.be/XRtLs5OT_yM) to create and visualize traceability links.

## More information about Eclipse Capra

The Eclipse Capra team maintains a number of wiki page that describe how to install and run Eclipse Capra from source code, how to extend Eclipse Capra with a custom traceability model, how to contribute to Eclipse Capra, and how to prepare a release. You can find these pages here:

 * https://wiki.eclipse.org/Capra
 * https://wiki.eclipse.org/Capra/CustomTraceabilityMetaModel
 * https://wiki.eclipse.org/Capra/Contributing
 * https://wiki.eclipse.org/Capra/PreparingForRelease

## License

Eclipse Capra is licensed under [EPL 2.0](https://www.eclipse.org/legal/epl-v20.html).