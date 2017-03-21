package org.eclipse.capra.handler.php;

import org.eclipse.capra.core.adapters.ArtifactMetaModelAdapter;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.emf.ecore.EObject;

public class PhpHandler extends AbstractArtifactHandler<IModelElement> {

	@Override
	public EObject createWrapper(IModelElement artifact, EObject artifactModel) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		EObject wrapper = adapter.createArtifact(artifactModel, this.getClass().getName(),
				artifact.getHandleIdentifier(), artifact.getElementName(), artifact.getPath().toString());
		return wrapper;
	}

	@Override
	public IModelElement resolveWrapper(EObject wrapper) {
		ArtifactMetaModelAdapter adapter = ExtensionPointHelper.getArtifactWrapperMetaModelAdapter().get();
		String handleIdentifier = adapter.getArtifactUri(wrapper);
		return DLTKCore.create(handleIdentifier);
	}

	@Override
	public String getDisplayName(IModelElement artifact) {
		return artifact.getElementName();
	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		// TODO Auto-generated method stub
		return null;
	}

}
