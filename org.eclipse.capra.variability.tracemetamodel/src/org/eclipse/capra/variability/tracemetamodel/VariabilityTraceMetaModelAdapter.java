package org.eclipse.capra.variability.tracemetamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.eclipse.app4mc.variability.ample.Alternative;
import org.eclipse.app4mc.variability.ample.AlternativeGroup;
import org.eclipse.app4mc.variability.ample.Mandatory;
import org.eclipse.app4mc.variability.ample.Optional;
import org.eclipse.app4mc.variability.ample.Or;
import org.eclipse.app4mc.variability.components.ComponentInstance;
import org.eclipse.app4mc.variability.components.Variant;
import org.eclipse.app4mc.variability.components.VariationPoint;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeFeature2Variant;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.AlternativeGroup2VariationPoint;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.GenericTraceLink;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.Mandatory2ComponentInstances;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.Optional2ComponentInstances;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.RelatedTo;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModel;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelFactory;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.TraceModelPackage;
import org.eclipse.capra.variability.tracemetamodel.TraceModel.VariabilityTraceLink;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public class VariabilityTraceMetaModelAdapter implements TraceMetaModelAdapter {

	public VariabilityTraceMetaModelAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public EObject createModel() {
		return TraceModelFactory.eINSTANCE.createTraceModel();
	}

	@Override
	public Collection<EClass> getAvailableTraceTypes(List<EObject> selection) {
		Collection<EClass> traceTypes = new ArrayList<>();
		/*
		 * Based on the selection, only suitable trace links are shown
		 */
		if (selection.stream().filter(getInvalidElementsPredicate()).count() == 0) {
			int mandatoryFeatures = 0;
			int optionalFeatures = 0;
			int orFeatures = 0;
			int alternativeFeatures = 0;
			int alternativeGroups = 0;
			int componentInstances = 0;
			int variationPoints = 0;
			int variants = 0;
			List<ComponentInstance> componentInstancesList = new ArrayList<ComponentInstance>();

			for (EObject obj : selection) {
				if (obj instanceof Mandatory) {
					mandatoryFeatures++;
				} else if (obj instanceof Optional) {
					optionalFeatures++;
				} else if (obj instanceof Or) {
					orFeatures++;
				} else if (obj instanceof Alternative) {
					alternativeFeatures++;
				} else if (obj instanceof AlternativeGroup) {
					alternativeGroups++;
				} else if (obj instanceof ComponentInstance) {
					componentInstances++;
					componentInstancesList.add((ComponentInstance) obj);
				} else if (obj instanceof VariationPoint) {
					variationPoints++;
				} else if (obj instanceof Variant) {
					variants++;
				}
			}

			if (mandatoryFeatures == 1 && componentInstances >= 1) {
				// checks if the component are none-optional
				if (componentInstancesList.parallelStream().filter(c -> c.getIsOptional()).count() == 0) {
					traceTypes.add(TraceModelPackage.eINSTANCE.getMandatory2ComponentInstances());
				}
			}
			if ((optionalFeatures == 1 || orFeatures == 1) && componentInstances >= 1) {
				// checks if the component are optional
				if (componentInstancesList.parallelStream().filter(c -> !c.getIsOptional()).count() == 0) {
					traceTypes.add(TraceModelPackage.eINSTANCE.getOptional2ComponentInstances());
				}
			}
			if (alternativeGroups == 1 && variationPoints == 1) {
				traceTypes.add(TraceModelPackage.eINSTANCE.getAlternativeGroup2VariationPoint());
			}
			if (alternativeFeatures == 1 && variants == 1) {
				traceTypes.add(TraceModelPackage.eINSTANCE.getAlternativeFeature2Variant());
			}
		}
		traceTypes.add(TraceModelPackage.eINSTANCE.getRelatedTo());
		return traceTypes;
	}

	private Predicate<Object> getInvalidElementsPredicate() {
		Predicate<Object> predicate = new Predicate<Object>() {
			public boolean test(Object object) {
				if (object instanceof Mandatory || object instanceof Optional || object instanceof Or
						|| object instanceof Alternative || object instanceof AlternativeGroup
						|| object instanceof VariationPoint || object instanceof Variant
						|| object instanceof ComponentInstance) {
					return false;
				}
				return true;
			}
		};
		return predicate;
	}

	@Override
	public EObject createTrace(EClass traceType, EObject traceModel, List<EObject> selection) {
		if (traceModel instanceof TraceModel) {
			TraceModel tm = (TraceModel) traceModel;
			EObject trace = TraceModelFactory.eINSTANCE.create(traceType);
			if (trace instanceof GenericTraceLink) {
				if (trace instanceof RelatedTo) {
					((RelatedTo) trace).getItem().addAll(selection);
					tm.getGenericTraceLinks().add((RelatedTo) trace);
				}
			} else if (trace instanceof VariabilityTraceLink) {
				if (trace instanceof Mandatory2ComponentInstances) {
					for (EObject sel : selection) {
						if (sel instanceof Mandatory) {
							((Mandatory2ComponentInstances) trace).setMandatoryFeature((Mandatory) sel);
						} else if (sel instanceof ComponentInstance) {
							((Mandatory2ComponentInstances) trace).getComponents().add((ComponentInstance) sel);
						}
					}
					tm.getVariableTraceLinks().add((VariabilityTraceLink) trace);
				} else if (trace instanceof Optional2ComponentInstances) {
					for (EObject sel : selection) {
						if (sel instanceof Optional) {
							((Optional2ComponentInstances) trace).setOptionalFeature((Optional) sel);
						} else if (sel instanceof Or) {
							((Optional2ComponentInstances) trace).setOrFeature((Or) sel);
						} else if (sel instanceof ComponentInstance) {
							((Optional2ComponentInstances) trace).getComponents().add((ComponentInstance) sel);
						}
					}
					tm.getVariableTraceLinks().add((VariabilityTraceLink) trace);
				} else if (trace instanceof AlternativeGroup2VariationPoint) {
					for (EObject sel : selection) {
						if (sel instanceof AlternativeGroup) {
							((AlternativeGroup2VariationPoint) trace).setAlternativeGroup((AlternativeGroup) sel);
						} else if (sel instanceof VariationPoint) {
							((AlternativeGroup2VariationPoint) trace).setVariationPoint((VariationPoint) sel);
						}
					}
					tm.getVariableTraceLinks().add((VariabilityTraceLink) trace);
				} else if (trace instanceof AlternativeFeature2Variant) {
					for (EObject sel : selection) {
						if (sel instanceof Alternative) {
							((AlternativeFeature2Variant) trace).setFeature((Alternative) sel);
						} else if (sel instanceof Variant) {
							((AlternativeFeature2Variant) trace).setVariant((Variant) sel);
						}
					}
					tm.getVariableTraceLinks().add((VariabilityTraceLink) trace);
				}
			}
			return tm;
		}
		return null;
	}

	@Override
	public void deleteTrace(EObject first, EObject second, EObject traceModel) {
	}

	@Override
	public boolean isThereATraceBetween(EObject first, EObject second, EObject traceModel) {
		if (traceModel instanceof TraceModel) {
			TraceModel vtm = (TraceModel) traceModel;
			if (first != second) {
				for (VariabilityTraceLink trace : vtm.getVariableTraceLinks()) {
					if (trace instanceof GenericTraceLink) {
						if (trace instanceof RelatedTo) {
							return ((RelatedTo) trace).getItem().contains(first)
									&& ((RelatedTo) trace).getItem().contains(second);
						}
					} else if (trace instanceof VariabilityTraceLink) {
						if (trace instanceof Mandatory2ComponentInstances) {
							return isThereATraceBetweenMandatoryComponent((Mandatory2ComponentInstances) trace, first,
									second);
						} else if (trace instanceof Optional2ComponentInstances) {
							return isThereATraceBetweenOptionalComponent((Optional2ComponentInstances) trace, first,
									second);
						} else if (trace instanceof AlternativeFeature2Variant) {
							return isThereATraceBetweenAlternativeFeature((AlternativeFeature2Variant) trace, first,
									second);
						} else if (trace instanceof AlternativeGroup2VariationPoint) {
							return isThereATraceBetweenAlternativeGroupVP((AlternativeGroup2VariationPoint) trace,
									first, second);
						}
					}
				}
			}
		}
		return false;
	}

	private Boolean isThereATraceBetweenMandatoryComponent(Mandatory2ComponentInstances trace, EObject first,
			EObject second) {
		if ((trace.getMandatoryFeature() != null
				&& (trace.getMandatoryFeature() == first || trace.getMandatoryFeature() == second))
				&& (trace.getComponents() != null
						&& (trace.getComponents().contains(first) || trace.getComponents().contains(second)))) {
			return true;
		}
		return false;
	}

	private boolean isThereATraceBetweenOptionalComponent(Optional2ComponentInstances trace, EObject first,
			EObject second) {
		if ((trace.getOptionalFeature() != null
				&& (trace.getOptionalFeature() == first || trace.getOptionalFeature() == second))
				&& (trace.getComponents() != null
						&& (trace.getComponents().contains(first) || trace.getComponents().contains(second)))) {
			return true;
		} else if ((trace.getOrFeature() != null && (trace.getOrFeature() == first || trace.getOrFeature() == second))
				&& (trace.getComponents() != null
						&& (trace.getComponents().contains(first) || trace.getComponents().contains(second)))) {
			return true;
		}
		return false;
	}

	private boolean isThereATraceBetweenAlternativeFeature(AlternativeFeature2Variant trace, EObject first,
			EObject second) {
		if ((trace.getFeature() != null && (trace.getFeature() == first || trace.getFeature() == second))
				&& (trace.getVariant() != null && (trace.getVariant() == first || trace.getVariant() == second))) {
			return true;
		}
		return false;
	}

	private boolean isThereATraceBetweenAlternativeGroupVP(AlternativeGroup2VariationPoint trace, EObject first,
			EObject second) {
		if ((trace.getAlternativeGroup() != null
				&& (trace.getAlternativeGroup() == first || trace.getAlternativeGroup() == second))
				&& (trace.getVariationPoint() != null
						&& (trace.getVariationPoint() == first || trace.getVariationPoint() == second))) {
			return true;
		}
		return false;
	}

	@Override
	public List<Connection> getConnectedElements(EObject element, EObject traceModel) {
		List<Connection> connections = new ArrayList<>();
		if (traceModel instanceof TraceModel) {
			TraceModel tm = (TraceModel) traceModel;
			if (element instanceof GenericTraceLink) {
				if (element instanceof RelatedTo) {
					RelatedTo trace = (RelatedTo) element;
					connections.add(new Connection(element, trace.getItem(), trace));
				}
			} else if (element instanceof VariabilityTraceLink) {
				if (element instanceof Mandatory2ComponentInstances) {
					Mandatory2ComponentInstances link = (Mandatory2ComponentInstances) element;
					if (link.getMandatoryFeature() != null && link.getComponents() != null) {
						List<EObject> list = new ArrayList<EObject>();
						list.addAll(link.getComponents());
						list.add(link.getMandatoryFeature());
						connections.add(new Connection(link, list, link));
					}
				} else if (element instanceof Optional2ComponentInstances) {
					Optional2ComponentInstances link = (Optional2ComponentInstances) element;
					List<EObject> list = new ArrayList<EObject>();
					if (link.getOptionalFeature() != null) {
						list.add(link.getOptionalFeature());
					}
					if (link.getOrFeature() != null) {
						list.add(link.getOrFeature());
					}
					if (link.getComponents() != null && !link.getComponents().isEmpty()) {
						list.addAll(link.getComponents());
					}
					connections.add(new Connection(link, list, link));
				} else if (element instanceof AlternativeFeature2Variant) {
					AlternativeFeature2Variant link = (AlternativeFeature2Variant) element;
					if (link.getFeature() != null && link.getVariant() != null) {
						List<EObject> list = new ArrayList<EObject>();
						list.add(link.getFeature());
						list.add(link.getVariant());
						connections.add(new Connection(link, list, link));
					}
				} else if (element instanceof AlternativeGroup2VariationPoint) {
					AlternativeGroup2VariationPoint link = (AlternativeGroup2VariationPoint) element;
					if (link.getAlternativeGroup() != null && link.getVariationPoint() != null) {
						List<EObject> list = new ArrayList<EObject>();
						list.add(link.getAlternativeGroup());
						list.add(link.getVariationPoint());
						connections.add(new Connection(link, list, link));
					}
				}
			}
			for (VariabilityTraceLink trace : tm.getVariableTraceLinks()) {
				if (trace instanceof Mandatory2ComponentInstances) {
					Mandatory2ComponentInstances link = (Mandatory2ComponentInstances) trace;
					if (link.getMandatoryFeature() != null && link.getComponents() != null
							&& !link.getComponents().isEmpty()) {
						if (link.getMandatoryFeature() == element) {
							List<EObject> list = new ArrayList<EObject>();
							list.addAll(link.getComponents());
							connections.add(new Connection(element, list, link));
						} else if (link.getComponents().contains(element)) {
							connections.add(new Connection(element, Arrays.asList(link.getMandatoryFeature()), link));
						}
					}
				} else if (trace instanceof Optional2ComponentInstances) {
					Optional2ComponentInstances link = (Optional2ComponentInstances) trace;
					if (link.getOptionalFeature() != null && link.getComponents() != null
							&& !link.getComponents().isEmpty()) {
						if (link.getOptionalFeature() == element) {
							List<EObject> list = new ArrayList<EObject>();
							list.addAll(link.getComponents());
							connections.add(new Connection(element, list, link));
						} else if (link.getComponents().contains(element)) {
							connections.add(new Connection(element, Arrays.asList(link.getOptionalFeature()), link));
						}
					} else if (link.getOrFeature() != null && link.getComponents() != null
							&& !link.getComponents().isEmpty()) {
						if (link.getOrFeature() == element) {
							List<EObject> list = new ArrayList<EObject>();
							list.addAll(link.getComponents());
							connections.add(new Connection(element, list, link));
						} else if (link.getComponents().contains(element)) {
							connections.add(new Connection(element, Arrays.asList(link.getOrFeature()), link));
						}
					}
				} else if (trace instanceof AlternativeFeature2Variant) {
					AlternativeFeature2Variant link = (AlternativeFeature2Variant) trace;
					if (link.getFeature() != null && link.getVariant() != null) {
						if (link.getFeature() == element) {
							connections.add(new Connection(element, Arrays.asList(link.getVariant()), link));
						} else if (link.getVariant() == element) {
							connections.add(new Connection(element, Arrays.asList(link.getFeature()), link));
						}
					}
				} else if (trace instanceof AlternativeGroup2VariationPoint) {
					AlternativeGroup2VariationPoint link = (AlternativeGroup2VariationPoint) trace;
					if (link.getAlternativeGroup() != null && link.getVariationPoint() != null) {
						if (link.getAlternativeGroup() == element) {
							connections.add(new Connection(element, Arrays.asList(link.getVariationPoint()), link));
						} else if (link.getVariationPoint() == element) {
							connections.add(new Connection(element, Arrays.asList(link.getAlternativeGroup()), link));
						}
					}
				}
			}
			for (GenericTraceLink trace : tm.getGenericTraceLinks()) {
				if (trace instanceof RelatedTo) {
					if (((RelatedTo) trace).getItem().contains(element)) {
						connections.add(new Connection(element, ((RelatedTo) trace).getItem(), trace));
					}
				}
			}
		}
		return connections;
	}

	private List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel,
			List<Object> accumulator) {
		List<Connection> directElements = getConnectedElements(element, traceModel);
		List<Connection> allElements = new ArrayList<>();

		directElements.forEach(connection -> {
			if (!accumulator.contains(connection.getTlink())) {
				allElements.add(connection);
				accumulator.add(connection.getTlink());
				connection.getTargets().forEach(e -> {
					allElements.addAll(getTransitivelyConnectedElements(e, traceModel, accumulator));
				});
			}
		});

		return allElements;
	}

	@Override
	public List<Connection> getTransitivelyConnectedElements(EObject element, EObject traceModel) {
		List<Object> accumulator = new ArrayList<>();
		return getTransitivelyConnectedElements(element, traceModel, accumulator);
	}

}