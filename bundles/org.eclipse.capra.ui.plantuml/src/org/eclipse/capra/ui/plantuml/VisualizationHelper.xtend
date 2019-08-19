package org.eclipse.capra.ui.plantuml

import java.util.Collection
import java.util.List
import org.eclipse.capra.core.adapters.Connection
import org.eclipse.capra.core.helpers.ExtensionPointHelper

import org.eclipse.emf.ecore.EObject
import org.eclipse.capra.core.helpers.ArtifactHelper

class VisualizationHelper {
	def static String createMatrix(EObject traceModel, EObject artifactModel, Collection<EObject> firstElements, Collection<EObject> secondElements, Boolean internalLinks){	
	val traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get()
	val artifactHelper = new ArtifactHelper(artifactModel)
	'''
	@startuml
	salt
	{#
	«IF firstElements != null»
	.«FOR e : secondElements»|«artifactHelper.getArtifactLabel(e)»«ENDFOR»
	«FOR first : firstElements»«artifactHelper.getArtifactLabel(first)»«FOR second : secondElements» |«IF internalLinks»«IF traceAdapter.isThereATraceBetween(first, second, traceModel) || traceAdapter.isThereAnInternalTraceBetween(first, second)»X«ELSE ».«ENDIF»«ELSE»«IF traceAdapter.isThereATraceBetween(first, second, traceModel)»X«ELSE ».«ENDIF»«ENDIF»«ENDFOR»
	«ENDFOR»
	«ELSE»
	Choose two containers to show a traceability matrix of their contents.
	«ENDIF»
	}
	
	@enduml
	'''
	} 
	
	def static String createNeighboursView(List<Connection> connections, List<EObject> selectedObjects, EObject artifactModel){
	var helper = new Connections(connections, selectedObjects, artifactModel);
	'''
	@startuml
	object "«helper.originLabel()»«IF helper.originHasLocation()» [[«helper.originLocation()» (Go to)]]«ENDIF»" as «helper.originId()» #pink
	«FOR id:helper.objectIdsWithoutOrigin()»
	object "«helper.label(id)»«IF helper.hasLocation(id)» [[«helper.location(id)» (Go to)]]«ENDIF»" as «id»
	«ENDFOR»
	«FOR a:helper.arrows()» 
	«a»
	«ENDFOR» 
	@enduml
	''' 
	}
} 
  

 