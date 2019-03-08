package org.eclipse.capra.ui.plantuml

import java.util.Collection
import java.util.List
import org.eclipse.capra.core.adapters.Connection
import org.eclipse.capra.core.helpers.ExtensionPointHelper

import org.eclipse.emf.ecore.EObject

class VisualizationHelper {
	def static String createMatrix(EObject traceModel, Collection<EObject> firstElements, Collection<EObject> secondElements, Boolean internalLinks){
	val traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get()
	'''
	@startuml
	salt
	{#
	«IF firstElements != null»
	.«FOR e : secondElements»|«Connections.getArtifactLabel(e)»«ENDFOR»
	«FOR first : firstElements»«Connections.getArtifactLabel(first)»«FOR second : secondElements» |«IF internalLinks»«IF traceAdapter.isThereATraceBetween(first, second, traceModel) || traceAdapter.isThereAnInternalTraceBetween(first, second)»X«ELSE ».«ENDIF»«ELSE»«IF traceAdapter.isThereATraceBetween(first, second, traceModel)»X«ELSE ».«ENDIF»«ENDIF»«ENDFOR»
	«ENDFOR»
	«ELSE»
	Choose two containers to show a traceability matrix of their contents.
	«ENDIF»
	}
	
	@enduml
	'''
	} 
	
	def static String createNeighboursView(List<Connection> connections, List<EObject> selectedObjects){
	var helper = new Connections(connections, selectedObjects);
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
  

 