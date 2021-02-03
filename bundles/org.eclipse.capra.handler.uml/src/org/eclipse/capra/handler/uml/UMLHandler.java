/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/
package org.eclipse.capra.handler.uml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.handlers.AbstractArtifactHandler;
import org.eclipse.capra.core.helpers.EMFHelper;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.ConnectorEnd;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.Transition;

/**
 * Handler to allow tracing to and from arbitrary UML model elements.
 * 
 * @author Dominik Einkemmer
 */
public class UMLHandler extends AbstractArtifactHandler<EModelElement> {

	@Override
	public EObject createWrapper(EModelElement artifact, EObject artifactModel) {
		return artifact;
	}

	@Override
	public EModelElement resolveWrapper(EObject wrapper) {
		return (EModelElement) wrapper;
	}

	@Override
	public String getDisplayName(EModelElement artifact) {
		return EMFHelper.getIdentifier(artifact);
	}

	@Override
	public List<Connection> getInternalLinks(EObject investigatedElement, List<String> selectedRelationshipTypes) {
		List<Integer> duplicationCheck = new ArrayList<>();
		List<Connection> allElements = new ArrayList<>();
		if (Relationship.class.isAssignableFrom(investigatedElement.getClass())) {
			if (selectedRelationshipTypes.isEmpty()
					|| selectedRelationshipTypes.contains(investigatedElement.eClass().getName())) {
				Relationship rel = Relationship.class.cast(investigatedElement);
				List<EObject> relatedElements = new ArrayList<>();
				int connectionHash = investigatedElement.hashCode() + rel.hashCode();
				for (Element element : rel.getRelatedElements()) {
					relatedElements.add(element);
					connectionHash += element.hashCode();
				}
				if (!duplicationCheck.contains(connectionHash)) {
					Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements, rel);
					allElements.add(conn);
					duplicationCheck.add(connectionHash);
				}
			}
		} else if (ActivityEdge.class.isAssignableFrom(investigatedElement.getClass())) {
			if (selectedRelationshipTypes.isEmpty()
					|| selectedRelationshipTypes.contains(investigatedElement.eClass().getName())) {
				ActivityEdge activityEdge = ActivityEdge.class.cast(investigatedElement);
				List<EObject> relatedElements = new ArrayList<>();
				relatedElements.add(activityEdge.getTarget());
				relatedElements.add(activityEdge.getSource());
				int connectionHash = investigatedElement.hashCode() + activityEdge.hashCode();
				if (!duplicationCheck.contains(connectionHash)) {
					Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements, activityEdge);
					allElements.add(conn);
					duplicationCheck.add(connectionHash);
				}
			}
		} else if (Transition.class.isAssignableFrom(investigatedElement.getClass())) {
			if (selectedRelationshipTypes.isEmpty()
					|| selectedRelationshipTypes.contains(investigatedElement.eClass().getName())) {
				Transition transition = Transition.class.cast(investigatedElement);
				List<EObject> relatedElements = new ArrayList<>();
				relatedElements.add(transition.getSource());
				relatedElements.add(transition.getTarget());
				int connectionHash = investigatedElement.hashCode() + transition.hashCode()
						+ transition.getTarget().hashCode() + transition.getSource().hashCode();
				if (!duplicationCheck.contains(connectionHash)) {
					Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements, transition);
					allElements.add(conn);
					duplicationCheck.add(connectionHash);
				}
			}
		} else if (Message.class.isAssignableFrom(investigatedElement.getClass())) {
			if (selectedRelationshipTypes.isEmpty()
					|| selectedRelationshipTypes.contains(investigatedElement.eClass().getName())) {
				Message msg = Message.class.cast(investigatedElement);
				MessageOccurrenceSpecification receiver = (MessageOccurrenceSpecification) msg.getReceiveEvent();
				MessageOccurrenceSpecification sender = (MessageOccurrenceSpecification) msg.getSendEvent();
				if (receiver != null) {
					List<EObject> relatedElements = new ArrayList<>();
					relatedElements.add(sender.getCovered());
					relatedElements.add(receiver.getCovered());
					int connectionHash = investigatedElement.hashCode() + msg.hashCode()
							+ msg.getMessageSort().hashCode() + sender.getCovered().hashCode()
							+ receiver.getCovered().hashCode();
					if (!duplicationCheck.contains(connectionHash)) {
						Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements, msg);
						allElements.add(conn);
						duplicationCheck.add(connectionHash);
					}
				}
			}
		} else if (Port.class.isAssignableFrom(investigatedElement.getClass())) {
			if (selectedRelationshipTypes.isEmpty()
					|| selectedRelationshipTypes.contains(investigatedElement.eClass().getName())) {
				Port port = Port.class.cast(investigatedElement);
				EList<Interface> provideds = port.getProvideds();
				EList<Interface> requireds = port.getRequireds();
				List<EObject> relatedElements = new ArrayList<>();
				relatedElements.addAll(requireds);
				relatedElements.addAll(provideds);
				int connectionHash = investigatedElement.hashCode() + port.hashCode();
				for (EObject el : relatedElements) {
					connectionHash += el.hashCode();
				}
				if (!duplicationCheck.contains(connectionHash)) {
					Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements, port);
					allElements.add(conn);
					duplicationCheck.add(connectionHash);
				}
			}
		} else if (Connector.class.isAssignableFrom(investigatedElement.getClass())) {
			if (selectedRelationshipTypes.isEmpty()
					|| selectedRelationshipTypes.contains(investigatedElement.eClass().getName())) {
				Connector connector = Connector.class.cast(investigatedElement);
				EList<ConnectorEnd> connectedEnds = connector.getEnds();
				List<EObject> relatedElements = new ArrayList<>();
				connectedEnds.forEach(connectedEnd -> {
					if (connectedEnd.getPartWithPort() != null) {
						relatedElements.add(connectedEnd.getPartWithPort());
					} else {
						relatedElements.add(connectedEnd);
					}
				});
				int connectionHash = investigatedElement.hashCode() + connector.hashCode();
				for (EObject el : relatedElements) {
					connectionHash += el.hashCode();
				}
				if (!duplicationCheck.contains(connectionHash)) {
					Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements, connector);
					allElements.add(conn);
					duplicationCheck.add(connectionHash);
				}
			}
		} else {
			EObject root = EcoreUtil.getRootContainer(investigatedElement);
			TreeIterator<EObject> modelContents = root.eAllContents();
			while (modelContents.hasNext()) {
				EObject content = modelContents.next();
				if (selectedRelationshipTypes.isEmpty()
						|| selectedRelationshipTypes.contains(content.eClass().getName())) {
					if (Relationship.class.isAssignableFrom(content.getClass())) {
						Relationship relation = Relationship.class.cast(content);
						boolean isRelatedToElement = false;
						List<EObject> relatedElements = new ArrayList<>();
						for (Element relatedElement : relation.getRelatedElements()) {
							if (relatedElement.hashCode() == investigatedElement.hashCode()) {
								isRelatedToElement = true;
							} else {
								relatedElements.add(relatedElement);
							}
						}
						if (isRelatedToElement) {
							int connectionHash = investigatedElement.hashCode() + relation.hashCode();
							for (EObject element : relatedElements) {
								connectionHash += element.hashCode();
							}
							if (!duplicationCheck.contains(connectionHash)) {
								Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements,
										relation);
								allElements.add(conn);
								duplicationCheck.add(connectionHash);
							}
						}
					} else if (ActivityEdge.class.isAssignableFrom(content.getClass())) {
						if (selectedRelationshipTypes.isEmpty()
								|| selectedRelationshipTypes.contains(content.eClass().getName())) {
							ActivityEdge activityEdge = ActivityEdge.class.cast(content);
							List<EObject> relatedElements = new ArrayList<>();
							if (activityEdge.getTarget().hashCode() == investigatedElement.hashCode()) {
								relatedElements.add(activityEdge.getSource());
							} else if (activityEdge.getSource().hashCode() == investigatedElement.hashCode()) {
								relatedElements.add(activityEdge.getTarget());
							}
							int connectionHash = investigatedElement.hashCode() + activityEdge.hashCode();
							if (!duplicationCheck.contains(connectionHash)) {
								Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements,
										activityEdge);
								allElements.add(conn);
								duplicationCheck.add(connectionHash);
							}
						}
					} else if (Transition.class.isAssignableFrom(content.getClass())) {
						Transition transition = Transition.class.cast(content);
						List<EObject> relatedElements = new ArrayList<>();
						if (transition.getSource().hashCode() == investigatedElement.hashCode()) {
							relatedElements.add(transition.getTarget());
							int connectionHash = investigatedElement.hashCode() + transition.hashCode()
									+ transition.getTarget().hashCode();
							if (!duplicationCheck.contains(connectionHash)) {
								Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements,
										transition);
								allElements.add(conn);
								duplicationCheck.add(connectionHash);
							}
						} else if (transition.getTarget().hashCode() == investigatedElement.hashCode()) {
							relatedElements.add(transition.getSource());
							int connectionHash = investigatedElement.hashCode() + transition.hashCode()
									+ transition.getSource().hashCode();
							if (!duplicationCheck.contains(connectionHash)) {
								Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements,
										transition);
								allElements.add(conn);
								duplicationCheck.add(connectionHash);
							}
						}
					} else if (Message.class.isAssignableFrom(content.getClass())) {
						Message msg = Message.class.cast(content);
						MessageOccurrenceSpecification receiver = (MessageOccurrenceSpecification) msg
								.getReceiveEvent();
						MessageOccurrenceSpecification sender = (MessageOccurrenceSpecification) msg.getSendEvent();
						List<EObject> relatedElements = new ArrayList<>();
						if (receiver != null && receiver.getCovered() != null) {
							if (receiver.getCovered().hashCode() == investigatedElement.hashCode()) {
								relatedElements.add(sender.getCovered());
								int connectionHash = investigatedElement.hashCode() + msg.hashCode()
										+ msg.getMessageSort().hashCode() + sender.getCovered().hashCode();
								if (!duplicationCheck.contains(connectionHash)) {
									Connection conn = new Connection(Arrays.asList(investigatedElement),
											relatedElements, msg);
									allElements.add(conn);
									duplicationCheck.add(connectionHash);
								}
							} else if (sender.getCovered().hashCode() == investigatedElement.hashCode()) {
								relatedElements.add(receiver.getCovered());
								int connectionHash = investigatedElement.hashCode() + msg.hashCode()
										+ msg.getMessageSort().hashCode() + receiver.getCovered().hashCode();
								if (!duplicationCheck.contains(connectionHash)) {
									Connection conn = new Connection(Arrays.asList(investigatedElement),
											relatedElements, msg);
									allElements.add(conn);
									duplicationCheck.add(connectionHash);
								}
							}
						}
					} else if (Port.class.isAssignableFrom(content.getClass())) {
						Port port = Port.class.cast(content);
						EList<Interface> provideds = port.getProvideds();
						boolean investigatedIsProvided = false;
						for (Interface provided : provideds) {
							if (provided.hashCode() == investigatedElement.hashCode()) {
								investigatedIsProvided = true;
							}
						}
						EList<Interface> requireds = port.getRequireds();
						boolean investigatedIsRequired = false;
						for (Interface required : requireds) {
							if (required.hashCode() == investigatedElement.hashCode()) {
								investigatedIsRequired = true;
							}

						}
						List<EObject> relatedElements = new ArrayList<>();

						if (investigatedIsProvided) {
							relatedElements.addAll(requireds);
						} else if (investigatedIsRequired) {
							relatedElements.addAll(provideds);
						}
						if (investigatedIsProvided || investigatedIsRequired) {
							int connectionHash = investigatedElement.hashCode() + port.hashCode();
							for (EObject el : relatedElements) {
								connectionHash += el.hashCode();
							}
							if (!duplicationCheck.contains(connectionHash)) {
								Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements,
										port);
								allElements.add(conn);
								duplicationCheck.add(connectionHash);
							}
						}
					} else if (Connector.class.isAssignableFrom(content.getClass())) {
						Connector connector = Connector.class.cast(content);
						EList<ConnectorEnd> connectedEnds = connector.getEnds();
						List<EObject> relatedElements = new ArrayList<>();
						boolean isConnected = false;
						for (ConnectorEnd connectedEnd : connectedEnds) {
							if (connectedEnd.getPartWithPort() != null) {
								relatedElements.add(connectedEnd.getPartWithPort());
								if (connectedEnd.getPartWithPort().hashCode() == investigatedElement.hashCode()) {
									isConnected = true;
									relatedElements.remove(connectedEnd.getPartWithPort());
								}
							} else {
								relatedElements.add(connectedEnd);
								if (connectedEnd.hashCode() == investigatedElement.hashCode()) {
									isConnected = true;
									relatedElements.remove(connectedEnd);
								}
							}
						}
						if (isConnected) {
							int connectionHash = investigatedElement.hashCode() + connector.hashCode();
							for (EObject el : relatedElements) {
								connectionHash += el.hashCode();
							}
							if (!duplicationCheck.contains(connectionHash)) {
								Connection conn = new Connection(Arrays.asList(investigatedElement), relatedElements,
										connector);
								allElements.add(conn);
								duplicationCheck.add(connectionHash);
							}
						}
					}
				}
			}
		}
		return allElements;
	}

	@Override
	public boolean isThereAnInternalTraceBetween(EObject first, EObject second) {
		if (first.equals(second)) {
			return false;
		}
		if (Relationship.class.isAssignableFrom(first.getClass())
				|| Relationship.class.isAssignableFrom(second.getClass())) {
			Relationship rel;
			if (Relationship.class.isAssignableFrom(first.getClass())) {
				rel = Relationship.class.cast(first);
			} else {
				rel = Relationship.class.cast(second);
			}
			boolean isRelated = false;
			for (Element relatedElement : rel.getRelatedElements()) {
				if (relatedElement.hashCode() == first.hashCode() || relatedElement.hashCode() == second.hashCode()) {
					isRelated = true;
				}
			}

			return isRelated;

		} else if (ActivityEdge.class.isAssignableFrom(first.getClass())
				|| ActivityEdge.class.isAssignableFrom(second.getClass())) {
			ActivityEdge activityEdge;
			if (ActivityEdge.class.isAssignableFrom(first.getClass())) {
				activityEdge = ActivityEdge.class.cast(first);
			} else {
				activityEdge = ActivityEdge.class.cast(second);
			}
			int sourceHash = activityEdge.getSource().hashCode();
			int targetHash = activityEdge.getTarget().hashCode();

			boolean relationContainsFirstElement = sourceHash == first.hashCode() || targetHash == first.hashCode();
			boolean relationContainsSecondElement = sourceHash == second.hashCode() || targetHash == second.hashCode();
			if (relationContainsFirstElement && relationContainsSecondElement) {
				return true;
			}
		} else if (Transition.class.isAssignableFrom(first.getClass())
				|| Transition.class.isAssignableFrom(second.getClass())) {
			Transition transition;
			if (Transition.class.isAssignableFrom(first.getClass())) {
				transition = Transition.class.cast(first);
			} else {
				transition = Transition.class.cast(second);
			}
			int sourceHash = transition.getSource().hashCode();
			int targetHash = transition.getTarget().hashCode();
			boolean relationContainsFirstElement = sourceHash == first.hashCode() || targetHash == first.hashCode();
			boolean relationContainsSecondElement = sourceHash == second.hashCode() || targetHash == second.hashCode();
			if (relationContainsFirstElement && relationContainsSecondElement) {
				return true;
			}
		} else if (Message.class.isAssignableFrom(first.getClass())
				|| Message.class.isAssignableFrom(second.getClass())) {
			Message msg;
			if (Message.class.isAssignableFrom(first.getClass())) {
				msg = Message.class.cast(first);
			} else {
				msg = Message.class.cast(second);
			}
			MessageOccurrenceSpecification receiver = (MessageOccurrenceSpecification) msg.getReceiveEvent();
			MessageOccurrenceSpecification sender = (MessageOccurrenceSpecification) msg.getSendEvent();
			if (receiver != null) {
				int sourceHash = sender.getCovered().hashCode();
				int targetHash = receiver.getCovered().hashCode();
				boolean relationContainsFirstElement = sourceHash == first.hashCode() || targetHash == first.hashCode();
				boolean relationContainsSecondElement = sourceHash == second.hashCode()
						|| targetHash == second.hashCode();
				if (relationContainsFirstElement && relationContainsSecondElement) {
					return true;
				}
			}
		} else if (Connector.class.isAssignableFrom(first.getClass())
				|| Connector.class.isAssignableFrom(second.getClass())) {
			Connector connector;
			if (Connector.class.isAssignableFrom(first.getClass())) {
				connector = Connector.class.cast(first);
			} else {
				connector = Connector.class.cast(second);
			}
			EList<ConnectorEnd> connectedEnds = connector.getEnds();
			for (ConnectorEnd connectedEnd : connectedEnds) {
				if ((connectedEnd.hashCode() == first.hashCode() || connectedEnd.hashCode() == second.hashCode())
						|| (connectedEnd.getPartWithPort() != null
								&& (connectedEnd.getPartWithPort().hashCode() == first.hashCode()
										|| connectedEnd.getPartWithPort().hashCode() == second.hashCode()))) {
					return true;
				}
			}

		} else {
			boolean relationContainsFirstElement = false;
			boolean relationContainsSecondElement = false;
			int numberofInternalLinks = 0;
			EObject root = EcoreUtil.getRootContainer(first);
			TreeIterator<EObject> modelContents = root.eAllContents();
			while (modelContents.hasNext()) {
				EObject content = modelContents.next();
				if (Relationship.class.isAssignableFrom(content.getClass())) {
					Relationship relation = Relationship.class.cast(content);
					for (Element relatedElement : relation.getRelatedElements()) {
						if (relatedElement.hashCode() == first.hashCode()) {
							relationContainsFirstElement = true;
						} else if (relatedElement.hashCode() == second.hashCode()) {
							relationContainsSecondElement = true;
						}
					}
					if (relationContainsFirstElement && relationContainsSecondElement) {
						numberofInternalLinks++;
					}
				} else if (ActivityEdge.class.isAssignableFrom(content.getClass())) {
					ActivityEdge activityEdge = ActivityEdge.class.cast(content);
					int sourceHash = activityEdge.getSource().hashCode();
					int targetHash = activityEdge.getTarget().hashCode();

					relationContainsFirstElement = sourceHash == first.hashCode() || targetHash == first.hashCode();
					relationContainsSecondElement = sourceHash == second.hashCode() || targetHash == second.hashCode();
					if (relationContainsFirstElement && relationContainsSecondElement) {
						numberofInternalLinks++;
					}
				} else if (Transition.class.isAssignableFrom(content.getClass())) {
					Transition transition = Transition.class.cast(content);
					int sourceHash = transition.getSource().hashCode();
					int targetHash = transition.getTarget().hashCode();
					relationContainsFirstElement = sourceHash == first.hashCode() || targetHash == first.hashCode();
					relationContainsSecondElement = sourceHash == second.hashCode() || targetHash == second.hashCode();
					if (relationContainsFirstElement && relationContainsSecondElement) {
						numberofInternalLinks++;
					}
				} else if (Message.class.isAssignableFrom(content.getClass())) {
					Message msg = Message.class.cast(content);
					MessageOccurrenceSpecification receiver = (MessageOccurrenceSpecification) msg.getReceiveEvent();
					MessageOccurrenceSpecification sender = (MessageOccurrenceSpecification) msg.getSendEvent();
					if (receiver != null) {
						int sourceHash = sender.getCovered().hashCode();
						int targetHash = receiver.getCovered().hashCode();
						relationContainsFirstElement = sourceHash == first.hashCode() || targetHash == first.hashCode();
						relationContainsSecondElement = sourceHash == second.hashCode()
								|| targetHash == second.hashCode();
						if (relationContainsFirstElement && relationContainsSecondElement) {
							numberofInternalLinks++;
						}
					} else if (Connector.class.isAssignableFrom(content.getClass())) {
						Connector connector = Connector.class.cast(content);
						EList<ConnectorEnd> connectedEnds = connector.getEnds();
						for (ConnectorEnd connectedEnd : connectedEnds) {
							if (connectedEnd.getPartWithPort() != null
									&& (connectedEnd.getPartWithPort().hashCode() == first.hashCode()
											|| connectedEnd.getPartWithPort().hashCode() == second.hashCode())) {
								relationContainsFirstElement = true;
							} else if (connectedEnd.hashCode() == first.hashCode()
									|| connectedEnd.hashCode() == second.hashCode()) {
								relationContainsSecondElement = true;
							}
						}
						if (relationContainsFirstElement && relationContainsSecondElement) {
							numberofInternalLinks++;
						}
					}
				}
			}
			if (numberofInternalLinks > 0) {
				return true;
			}
		}
		return false;

	}

	@Override
	public String generateMarkerMessage(IResourceDelta delta, String wrapperUri) {
		return null;
	}
}
