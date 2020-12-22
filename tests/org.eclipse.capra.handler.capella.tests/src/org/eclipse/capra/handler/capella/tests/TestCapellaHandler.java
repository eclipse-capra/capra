/*******************************************************************************
 * Copyright (c) 2016,2020 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.capella.tests;

import static org.eclipse.capra.testsuite.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsuite.TestHelper.resetSelectionView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.handler.capella.CapellaHandler;
import org.eclipse.capra.testsuite.TestHelper;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.junit.Before;
import org.junit.Test;
import org.polarsys.capella.common.ef.command.AbstractReadWriteCommand;
import org.polarsys.capella.core.data.capellacommon.AbstractState;
import org.polarsys.capella.core.data.capellacommon.StateMachine;
import org.polarsys.capella.core.data.capellacommon.TransitionKind;

public class TestCapellaHandler {

	private static final String STATE_A_NAME = "A";
	private static final String STATE_B_NAME = "B";
	private static final String STATE_C_NAME = "C";

	private static final String STATE_D_NAME = "D";
	private static final String STATE_E_NAME = "E";

	private static final String STATE_MACHINE_A_NAME = "aStateMachine";
	private static final String STATE_MACHINE_B_NAME = "bStateMachine";

	private static final String TEST_PROJECT_NAME = "TestProject";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
	}

	@Test
	public void testLinkUmlClassToCapellaState() {
		TestUtil.executeTestCommand(new AbstractReadWriteCommand() {
			@Override
			public void run() {
				// Create a project
				try {
					TestHelper.createSimpleProject(TEST_PROJECT_NAME);
				} catch (CoreException e) {
					fail("IOException when creating a project");
					e.printStackTrace();
				}
				assertTrue(TestHelper.projectExists(TEST_PROJECT_NAME));
				IProject testProject = TestHelper.getProject(TEST_PROJECT_NAME);

				// Create a State Machine with three states and transitions between them
				StateMachine stateMachineA = TestUtil.createStateMachine(STATE_MACHINE_A_NAME);
				AbstractState stateA = TestUtil.createState(stateMachineA, STATE_A_NAME);
				AbstractState stateB = TestUtil.createState(stateMachineA, STATE_B_NAME);
				AbstractState stateC = TestUtil.createState(stateMachineA, STATE_C_NAME);
				TestUtil.createStateTransition(stateA, stateB, TransitionKind.LOCAL, null);
				TestUtil.createStateTransition(stateB, stateC, TransitionKind.LOCAL, null);
				TestUtil.createStateTransition(stateC, stateA, TransitionKind.LOCAL, null);

				assertTrue("CapellaHandler not found",
						ExtensionPointHelper.getArtifactHandlers().stream().anyMatch(CapellaHandler.class::isInstance));

				assertEquals("CapellaHandler is not highest priority", CapellaHandler.class,
						ExtensionPointHelper.getPriorityHandler().get()
								.getSelectedHandler(ExtensionPointHelper.getArtifactHandlers(), stateMachineA)
								.getClass());

				Model umlModel = TestUtil.setupSimpleUmlModel();
				PackageableElement aClass = umlModel.getPackagedElements().get(0);

				try {
					TestUtil.save(testProject, stateMachineA, umlModel);
				} catch (IOException e) {
					fail("IOException when saving a project");
					e.printStackTrace();
				}

				// Add A and B to the selection view
				assertTrue("SelectionView is not empty", SelectionView.getOpenedView().getSelection().isEmpty());
				SelectionView.getOpenedView().dropToSelection(stateA);
				SelectionView.getOpenedView().dropToSelection(aClass);
				assertFalse("SelectionView is empty", SelectionView.getOpenedView().getSelection().isEmpty());

				// Create a trace via the selection view
				TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();
				TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
				ResourceSet rs = TestUtil.getHoldingResource().getResourceSet();
				EObject traceModel = persistenceAdapter.getTraceModel(rs);
				assertFalse("A trace between the state and the class already exists",
						traceAdapter.isThereATraceBetween(stateA, aClass, traceModel));

				TestHelper.createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());
				EObject updatedTraceModel = persistenceAdapter.getTraceModel(rs);
				assertTrue("There is no trace between the state and the class",
						traceAdapter.isThereATraceBetween(stateA, aClass, updatedTraceModel));
			}
		});
	}

	@Test
	public void testInternalCapellaLinks() {
		TestUtil.executeTestCommand(new AbstractReadWriteCommand() {
			@Override
			public void run() {
				// Create a State Machine with three states and transitions between them
				StateMachine stateMachineA = TestUtil.createStateMachine(STATE_MACHINE_A_NAME);
				AbstractState stateA = TestUtil.createState(stateMachineA, STATE_A_NAME);
				AbstractState stateB = TestUtil.createState(stateMachineA, STATE_B_NAME);
				AbstractState stateC = TestUtil.createState(stateMachineA, STATE_C_NAME);
				TestUtil.createStateTransition(stateA, stateB, TransitionKind.LOCAL, null);
				TestUtil.createStateTransition(stateB, stateC, TransitionKind.LOCAL, null);
				TestUtil.createStateTransition(stateC, stateA, TransitionKind.LOCAL, null);

				// Create another State Machine with two states and transitions between them
				StateMachine stateMachineB = TestUtil.createStateMachine(STATE_MACHINE_B_NAME);
				AbstractState stateD = TestUtil.createState(stateMachineB, STATE_D_NAME);
				AbstractState stateE = TestUtil.createState(stateMachineB, STATE_E_NAME);
				TestUtil.createStateTransition(stateD, stateE, TransitionKind.LOCAL, null);

				TestUtil.createCapellaTrace(stateA, stateD);
				IArtifactHandler<?> capellaHandler = new CapellaHandler();
				assertTrue(capellaHandler.isThereAnInternalTraceBetween(stateA, stateD));
			}
		});
	}
}