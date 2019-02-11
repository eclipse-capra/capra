package org.eclipse.capra.testsuite;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.handlers.PriorityHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultPriorityHandler {

	IArtifactHandler<?> emfHandler;
	IArtifactHandler<?> buildElementHandler;
	IArtifactHandler<?> testElementHandler;

	private PriorityHandler priorityHandler;

	@Before
	public void setup() {
		priorityHandler = ExtensionPointHelper.getPriorityHandler().get();
		emfHandler = ExtensionPointHelper.getArtifactHandler("org.eclipse.capra.handler.emf.EMFHandler").get();
		testElementHandler = ExtensionPointHelper
				.getArtifactHandler("org.eclipse.capra.handler.hudson.TestElementHandler").get();
		buildElementHandler = ExtensionPointHelper
				.getArtifactHandler("org.eclipse.capra.handler.hudson.BuildElementHandler").get();
	}

	@Test
	public void testPrioritiesOrder() {
		Collection<IArtifactHandler<?>> handlers = new ArrayList<>();
		handlers.add(emfHandler);
		handlers.add(buildElementHandler);

		Assert.assertEquals(buildElementHandler, priorityHandler.getSelectedHandler(handlers, null));

		// Turn order around
		handlers.clear();
		handlers.add(buildElementHandler);
		handlers.add(emfHandler);
		Assert.assertEquals(buildElementHandler, priorityHandler.getSelectedHandler(handlers, null));
	}

	@Test
	public void testPrioritiesConflict() {
		Collection<IArtifactHandler<?>> handlers = new ArrayList<>();
		handlers.add(testElementHandler);
		handlers.add(buildElementHandler);
		// PriorityHandler should choose first element in list in case of a
		// conflict
		Assert.assertEquals(testElementHandler, priorityHandler.getSelectedHandler(handlers, null));
	}

}
