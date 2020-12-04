package org.eclipse.capra.ui.plantuml;

import java.util.Collection;
import java.util.List;
import org.eclipse.capra.core.adapters.Connection;
import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.ui.plantuml.Connections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class VisualizationHelper {
  public static String createMatrix(final EObject traceModel, final EObject artifactModel, final Collection<EObject> rows, final Collection<EObject> columns, final Boolean internalLinks) {
    String _xblockexpression = null;
    {
      final TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
      final ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("@startuml");
      _builder.newLine();
      _builder.append("salt");
      _builder.newLine();
      _builder.append("{#");
      _builder.newLine();
      {
        if (((rows != null) && (rows.size() > 0))) {
          _builder.append(".");
          {
            for(final EObject e : columns) {
              _builder.append("|");
              String _artifactLabel = artifactHelper.getArtifactLabel(e);
              _builder.append(_artifactLabel);
            }
          }
          _builder.newLineIfNotEmpty();
          {
            for(final EObject first : rows) {
              String _artifactLabel_1 = artifactHelper.getArtifactLabel(first);
              _builder.append(_artifactLabel_1);
              {
                for(final EObject second : columns) {
                  _builder.append(" |");
                  {
                    if ((internalLinks).booleanValue()) {
                      {
                        if ((traceAdapter.isThereATraceBetween(first, second, traceModel) || traceAdapter.isThereAnInternalTraceBetween(first, second))) {
                          _builder.append("X");
                        } else {
                          _builder.append(".");
                        }
                      }
                    } else {
                      {
                        boolean _isThereATraceBetween = traceAdapter.isThereATraceBetween(first, second, traceModel);
                        if (_isThereATraceBetween) {
                          _builder.append("X");
                        } else {
                          _builder.append(".");
                        }
                      }
                    }
                  }
                }
              }
              _builder.newLineIfNotEmpty();
            }
          }
        } else {
          _builder.append("Select a single element to view a traceability graph and at least two elements to show their traceability matrix.");
          _builder.newLine();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("@enduml");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public static String createNeighboursView(final List<Connection> connections, final List<EObject> selectedObjects, final EObject artifactModel) {
    String _xblockexpression = null;
    {
      Connections helper = new Connections(connections, selectedObjects, artifactModel);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("@startuml");
      _builder.newLine();
      _builder.append("object \"");
      String _originLabel = helper.originLabel();
      _builder.append(_originLabel);
      {
        boolean _originHasLocation = helper.originHasLocation();
        if (_originHasLocation) {
          _builder.append(" [[");
          String _replaceAll = helper.originLocation().replaceAll(" ", "%20");
          _builder.append(_replaceAll);
          _builder.append(" (Go to)]]");
        }
      }
      _builder.append("\" as ");
      String _originId = helper.originId();
      _builder.append(_originId);
      _builder.append(" #pink");
      _builder.newLineIfNotEmpty();
      {
        Collection<String> _objectIdsWithoutOrigin = helper.objectIdsWithoutOrigin();
        for(final String id : _objectIdsWithoutOrigin) {
          _builder.append("object \"");
          String _label = helper.label(id);
          _builder.append(_label);
          {
            boolean _hasLocation = helper.hasLocation(id);
            if (_hasLocation) {
              _builder.append(" [[");
              String _replaceAll_1 = helper.location(id).replaceAll(" ", "%20");
              _builder.append(_replaceAll_1);
              _builder.append(" (Go to)]]");
            }
          }
          _builder.append("\" as ");
          _builder.append(id);
          _builder.newLineIfNotEmpty();
        }
      }
      {
        List<String> _arrows = helper.arrows();
        for(final String a : _arrows) {
          _builder.append(a);
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
