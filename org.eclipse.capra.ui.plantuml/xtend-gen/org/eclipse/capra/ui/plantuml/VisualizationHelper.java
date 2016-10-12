package org.eclipse.capra.ui.plantuml;

import java.util.Collection;
import java.util.List;

import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class VisualizationHelper {
	public static String createMatrix(final EObject traceModel, final Collection<EObject> firstElements,
			final Collection<EObject> secondElements) {
		throw new Error("Unresolved compilation problems:" + "\nThe method or field EMFHelper is undefined"
				+ "\nThe method or field EMFHelper is undefined" + "\ngetIdentifier cannot be resolved"
				+ "\ngetIdentifier cannot be resolved");
	}

	public static String createNeighboursView(final List<Connection> connections, final EObject selectedObject) {
		String _xblockexpression = null;
		{
			Connections helper = new Connections(connections, selectedObject);
			StringConcatenation _builder = new StringConcatenation();
			_builder.append("@startuml");
			_builder.newLine();
			_builder.append("object \"");
			String _originLabel = helper.originLabel();
			_builder.append(_originLabel, "");
			_builder.append("\" as ");
			String _originId = helper.originId();
			_builder.append(_originId, "");
			_builder.append(" #pink");
			_builder.newLineIfNotEmpty();
			{
				Collection<String> _objectIdsWithoutOrigin = helper.objectIdsWithoutOrigin();
				for (final String id : _objectIdsWithoutOrigin) {
					_builder.append("object \"");
					String _label = helper.label(id);
					_builder.append(_label, "");
					_builder.append("\" as ");
					_builder.append(id, "");
					_builder.newLineIfNotEmpty();
				}
			}
			{
				List<String> _arrows = helper.arrows();
				for (final String a : _arrows) {
					_builder.append(a, "");
					_builder.newLineIfNotEmpty();
				}
			}
			_builder.append("@enduml");
			_builder.newLine();
			_xblockexpression = _builder.toString();
		}
		return _xblockexpression;
	}
}
