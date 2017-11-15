package org.eclipse.capra.core.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * Implements standard functionality for the methods defined in the
 * {@link TraceMetaModelAdapter}.
 */
public abstract class AbstractMetaModelAdapter implements TraceMetaModelAdapter {

	private List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel,
			List<Object> accumulator, List<String> selectedRelationshipTypes, int currentDepth, int maximumDepth,
			List<Connection> existingTraces) {
		List<Connection> directElements = getInternalElements(element, traceModel, selectedRelationshipTypes, true,
				maximumDepth, existingTraces);
		List<Connection> allElements = new ArrayList<>();
		int currDepth = currentDepth + 1;
		for (Connection connection : directElements) {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				for (EObject e : connection.getTargets()) {
					if (maximumDepth == 0 || currDepth < (maximumDepth + 2)) {
						allElements.addAll(getInternalElementsTransitive(e, traceModel, accumulator,
								selectedRelationshipTypes, currDepth, maximumDepth, existingTraces));
					}
				}
			}
		}

		return allElements;
	}

	@Override
	public List<Connection> getInternalElementsTransitive(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, int maximumDepth, List<Connection> existingTraces) {
		List<Object> accumulator = new ArrayList<>();
		return getInternalElementsTransitive(element, traceModel, accumulator, selectedRelationshipTypes, 0,
				maximumDepth, existingTraces);
	}

	@Override
	public List<Connection> getInternalElements(EObject element, EObject traceModel,
			List<String> selectedRelationshipTypes, boolean traceLinksTransitive, int transitivityDepth,
			List<Connection> existingTraces) {
		List<Connection> allElements = new ArrayList<>();
		ArrayList<Integer> duplicationCheck = new ArrayList<>();
		List<Connection> directElements;
		if (traceLinksTransitive) {
			directElements = getTransitivelyConnectedElements(element, traceModel, selectedRelationshipTypes,
					transitivityDepth);
		} else {
			directElements = getConnectedElements(element, traceModel, selectedRelationshipTypes);
		}
		List<Integer> hashCodes = new ArrayList<>();

		for (Connection conn : existingTraces) {
			int connectionHash = conn.getOrigin().hashCode() + conn.getTlink().hashCode();
			for (EObject targ : conn.getTargets()) {
				connectionHash += targ.hashCode();
			}
			hashCodes.add(connectionHash);
		}

		ResourceSet resourceSet = new ResourceSetImpl();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);

		for (Connection conn : directElements) {
			int connectionHash = conn.getOrigin().hashCode() + conn.getTlink().hashCode();
			for (EObject targ : conn.getTargets()) {
				connectionHash += targ.hashCode();
			}
			if (!hashCodes.contains(connectionHash)) {
				allElements.add(conn);
			}
			for (EObject o : conn.getTargets()) {
				@SuppressWarnings("unchecked")
				IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(o).orElse(null);
				handler.addInternalLinks(o, allElements, duplicationCheck, selectedRelationshipTypes);

			}
		}

		if (element.getClass().getPackage().toString().contains("org.eclipse.eatop")) {
			@SuppressWarnings("unchecked")
			IArtifactHandler<Object> handler = (IArtifactHandler<Object>) artifactHelper.getHandler(element)
					.orElse(null);
			handler.addInternalLinks(element, allElements, duplicationCheck, selectedRelationshipTypes);
		}
		return allElements;
	}

	@Override
	public boolean isThereATraceBetween(EObject first, EObject second, EObject traceModel) {
		return false;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second, EObject traceModel) {
		ResourceSet resourceSet = new ResourceSetImpl();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		IArtifactHandler<?> handlerFirstElement = artifactHelper.getHandler(first).orElse(null);
		IArtifactHandler<?> handlerSecondElement = artifactHelper.getHandler(second).orElse(null);

		return handlerFirstElement.isThereAnInternalTraceBetween(first, second, traceModel)
				|| handlerSecondElement.isThereAnInternalTraceBetween(first, second, traceModel);
	}
}
