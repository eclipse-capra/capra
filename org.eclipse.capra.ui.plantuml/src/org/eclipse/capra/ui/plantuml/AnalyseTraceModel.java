package org.eclipse.capra.ui.plantuml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.capra.GenericTraceMetaModel.GenericTraceModel;
import org.eclipse.capra.GenericTraceMetaModel.RelatedTo;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class AnalyseTraceModel {

	private static TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
	private static TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
	private static ResourceSet resource = new ResourceSetImpl();
	private static EObject traceModel = persistenceAdapter.getTraceModel(resource);
	private static GenericTraceModel tModel = (GenericTraceModel) traceModel;

	public static double getThresholdPerTraceaLinkSet() {

		Set<EObject> allItems = new HashSet<>();
		for (RelatedTo trace : tModel.getTraces()) {
			allItems.addAll(trace.getItem());
		}

		List<Integer> numberofConnections = new ArrayList<>();

		for (EObject item : allItems) {
			numberofConnections.add(traceAdapter.getConnectedElements(item, traceModel).size());
		}

		double sum = 0;
		double avg = 0;
		for (double i : numberofConnections) {
			sum = sum + i;
		}
		avg = sum / numberofConnections.size();

		double intermediateSum = 0;
		for (double i : numberofConnections) {
			double n = i - avg;
			intermediateSum = intermediateSum + Math.pow(n, 2);
		}
		intermediateSum = intermediateSum / numberofConnections.size();
		double stdv = Math.sqrt(intermediateSum);
		return stdv + avg;

	}

	public static int getNumberOfNodesInNetwork(EObject selection) {

		List<Connection> connections = traceAdapter.getTransitivelyConnectedElements(selection,
				persistenceAdapter.getTraceModel(selection.eResource().getResourceSet()));

		Set<EObject> elements = new HashSet<>();
		for (Connection c : connections) {
			elements.add(c.getOrigin());
			elements.addAll(c.getTargets());
		}

		return elements.size();

	}

	// Number of connections in a sub network. Total reachable connections from
	// one node

}
