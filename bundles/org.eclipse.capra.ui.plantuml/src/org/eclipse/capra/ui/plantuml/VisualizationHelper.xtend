package org.eclipse.capra.ui.plantuml

import java.util.Collection
import java.util.List
import org.eclipse.capra.core.adapters.Connection
import org.eclipse.capra.core.helpers.ExtensionPointHelper

import org.eclipse.emf.ecore.EObject
import org.eclipse.capra.core.helpers.ArtifactHelper

class VisualizationHelper {
	def static String createMatrix(EObject traceModel, EObject artifactModel, Collection<EObject> rows, Collection<EObject> columns, Boolean internalLinks){	
	val traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get()
	val artifactHelper = new ArtifactHelper(artifactModel)
	'''
	@startuml
	salt
	{#
	«IF rows !== null && rows.size() > 0»
	.«FOR e : columns»|«artifactHelper.getArtifactLabel(e)»«ENDFOR»
	«FOR first : rows»«artifactHelper.getArtifactLabel(first)»«FOR second : columns» |«IF internalLinks»«IF traceAdapter.isThereATraceBetween(first, second, traceModel) || traceAdapter.isThereAnInternalTraceBetween(first, second)»X«ELSE ».«ENDIF»«ELSE»«IF traceAdapter.isThereATraceBetween(first, second, traceModel)»X«ELSE ».«ENDIF»«ENDIF»«ENDFOR»
	«ENDFOR»
	«ELSE»
	Select a single element to view a traceability graph and at least two elements to show their traceability matrix.
	«ENDIF»
	}
	
	@enduml
	'''
	}
	
	def static String createNeighboursView(List<Connection> connections, List<EObject> selectedObjects, EObject artifactModel){
	var helper = new Connections(connections, selectedObjects, artifactModel);
	'''
	@startuml
	left to right direction
	object "«helper.originLabel()»«IF helper.originHasLocation()» [[«helper.originLocation().replaceAll(" ", "%20")» (Go to)]]«ENDIF»" as «helper.originId()» #pink
	«FOR id:helper.objectIdsWithoutOrigin()»
	object "«helper.label(id)»«IF helper.hasLocation(id)» [[«helper.location(id).replaceAll(" ", "%20")» (Go to)]]«ENDIF»" as «id»
	«ENDFOR»
	«FOR a:helper.arrows()» 
	«a»
	«ENDFOR» 
	@enduml
	''' 
	}
} 
  

 