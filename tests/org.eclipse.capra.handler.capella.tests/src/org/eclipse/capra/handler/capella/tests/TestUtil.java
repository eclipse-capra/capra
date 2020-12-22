/*******************************************************************************
 * Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *   Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.handler.capella.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.polarsys.capella.common.data.modellingcore.AbstractTrace;
import org.polarsys.capella.common.data.modellingcore.ModellingcorePackage;
import org.polarsys.capella.common.data.modellingcore.TraceableElement;
import org.polarsys.capella.common.ef.ExecutionManager;
import org.polarsys.capella.common.ef.ExecutionManagerRegistry;
import org.polarsys.capella.common.ef.command.ICommand;
import org.polarsys.capella.common.mdsofa.common.helper.EcoreHelper;
import org.polarsys.capella.core.data.capellacommon.AbstractState;
import org.polarsys.capella.core.data.capellacommon.CapellacommonPackage;
import org.polarsys.capella.core.data.capellacommon.Region;
import org.polarsys.capella.core.data.capellacommon.State;
import org.polarsys.capella.core.data.capellacommon.StateMachine;
import org.polarsys.capella.core.data.capellacommon.StateTransition;
import org.polarsys.capella.core.data.capellacommon.TransitionKind;
import org.polarsys.capella.core.data.capellacore.Constraint;
import org.polarsys.capella.core.model.handler.helpers.HoldingResourceHelper;

public class TestUtil {

	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";
	private static final String CLASS_C_NAME = "C";
	private static final String MODEL_A_NAME = "modelA";

	private static ExecutionManager _executionManager;

	/**
	 * Creates an empty state machine with a "default" region.
	 *
	 * @param name the name of the state machine
	 * @return
	 */
	public static StateMachine createStateMachine(String name) {
		Region region = (Region) createType(CapellacommonPackage.Literals.REGION);
		region.setName("default");
		StateMachine machine = (StateMachine) createType(CapellacommonPackage.Literals.STATE_MACHINE);
		machine.setName(name);
		machine.getOwnedRegions().add(region);
		return machine;
	}

	/**
	 * Creates a new state within the first region of the provided state machine.
	 *
	 * @param machine the state machine
	 * @param name    the name of the created state
	 */
	public static AbstractState createState(StateMachine machine, String name) {
		if (machine.getOwnedRegions().size() < 1) {
			throw new IllegalArgumentException("State machine must contain at least one region");
		}
		State s = (State) createType(CapellacommonPackage.Literals.STATE);
		s.setName(name);
		machine.getOwnedRegions().get(0).getOwnedStates().add(s);
		return s;
	}

	public static void createStateTransition(AbstractState source, AbstractState target, TransitionKind kind,
			Constraint guard) {
		StateTransition tr = (StateTransition) createType(CapellacommonPackage.Literals.STATE_TRANSITION);
		tr.setSource(source);
		tr.setTarget(target);
		tr.setKind(kind);
		tr.setGuard(guard);
	}

	/**
	 * Creates an empty UML model.
	 *
	 * @param name the name of the model
	 * @return
	 */
	private static Model createUMLModel(String name) {
		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);
		return model;
	}

	/**
	 * Creates an EClass entity in the provided model.
	 *
	 * @param model UML model
	 * @param name  the name of the created EClass entity
	 */
	private static void createClassInUMLModel(Model model, String name) {
		org.eclipse.uml2.uml.Class c = UMLFactory.eINSTANCE.createClass();
		c.setName(name);
		model.getPackagedElements().add(c);
	}

	/**
	 * Create a UML model and three classes
	 * 
	 * @return a simple UML model
	 */
	public static Model setupSimpleUmlModel() {
		Model umlModel = TestUtil.createUMLModel(MODEL_A_NAME);
		TestUtil.createClassInUMLModel(umlModel, CLASS_A_NAME);
		TestUtil.createClassInUMLModel(umlModel, CLASS_B_NAME);
		TestUtil.createClassInUMLModel(umlModel, CLASS_C_NAME);
		return umlModel;
	}

	/**
	 * Persists (saves) the provided state machine and UML model in the specified
	 * project.
	 *
	 * @param project a handle to the project in which the model is to be persisted
	 * @param sm      the state machine to be persisted
	 * @param model   the UML model to be persisted
	 * @throws IOException
	 */
	public static void save(IProject project, StateMachine sm, Model umlModel) throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		URI umlPath = URI.createFileURI(project.getLocation().toString() + "/" + umlModel.getName() + ".uml");
		Resource umlResource = rs.createResource(umlPath);
		umlResource.getContents().add(umlModel);
		umlResource.save(null);
		URI rmPath = URI.createFileURI(project.getLocation().toString() + "/" + sm.getName() + ".capella");
		Resource smResource = rs.createResource(rmPath);
		smResource.getContents().add(sm);
		smResource.save(null);
	}

	/**
	 * Creates a simple Capella trace link between the two traceable elements
	 * provided.
	 * 
	 * @param source the source element for the trace link
	 * @param target the target element for the trace link
	 * @return the created trace link
	 */
	public static AbstractTrace createCapellaTrace(TraceableElement source, TraceableElement target) {
		AbstractTrace trace = (AbstractTrace) createType(ModellingcorePackage.Literals.ABSTRACT_TRACE);
		// Create source.

		trace.setSourceElement(source);
		assertEquals("Ensure outgoing trace is valid", trace, source.getOutgoingTraces().get(0));
		// Cycle through target elements.

		trace.setTargetElement(target);
		assertEquals("Ensure incoming trace is valid", trace, target.getIncomingTraces().get(0));

		return trace;
	}

	/**
	 * Create an object of specified type.
	 * 
	 * @param type_p
	 * @return
	 */
	public static EObject createType(EClass type_p) {
		assertNotNull(type_p);
		// Get static java representation, so as to avoid dynamic emf instantiations.
		EClass type = EcoreHelper.getStaticClass(type_p);
		EObject result = type.getEPackage().getEFactoryInstance().create(type);
		assertNotNull(result);
		getHoldingResource().getContents().add(result);
		return result;
	}

	/**
	 * @return
	 */
	public static Resource getHoldingResource() {
		return HoldingResourceHelper.getHoldingResource(getExecutionManager().getEditingDomain());
	}

	protected static ExecutionManager getExecutionManager() {
		if (null == _executionManager) {
			_executionManager = ExecutionManagerRegistry.getInstance().addNewManager();
		}
		return _executionManager;
	}

	/**
	 * Execute test command.
	 * 
	 * @param command_p
	 */
	public static void executeTestCommand(ICommand command_p) {
		assertNotNull(command_p);
		getExecutionManager().execute(command_p);
	}

}
