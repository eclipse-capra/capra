package org.eclipse.capra.testsuite;

import static org.eclipse.capra.testsupport.TestHelper.clearWorkspace;
import static org.eclipse.capra.testsupport.TestHelper.createEClassInEPackage;
import static org.eclipse.capra.testsupport.TestHelper.createJavaProjectWithASingleJavaClass;
import static org.eclipse.capra.testsupport.TestHelper.createTraceForCurrentSelectionOfType;
import static org.eclipse.capra.testsupport.TestHelper.getProject;
import static org.eclipse.capra.testsupport.TestHelper.load;
import static org.eclipse.capra.testsupport.TestHelper.projectExists;
import static org.eclipse.capra.testsupport.TestHelper.purgeModels;
import static org.eclipse.capra.testsupport.TestHelper.resetSelectionView;
import static org.eclipse.capra.testsupport.TestHelper.save;
import static org.eclipse.capra.testsupport.TestHelper.thereIsATraceBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.capra.core.adapters.IPersistenceAdapter;
import org.eclipse.capra.core.handlers.IArtifactHandler;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.EditingDomainHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.generic.tracemodel.TracemodelPackage;
import org.eclipse.capra.testsupport.TestHelper;
import org.eclipse.capra.ui.asciidoc.AsciiDocArtifact;
import org.eclipse.capra.ui.asciidoc.IAsciiDocApiAccess;
import org.eclipse.capra.ui.views.SelectionView;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jcup.asciidoctoreditor.outline.Item;

public class TestDoesArtifactExist {

	private static final Logger LOG = LoggerFactory.getLogger(TestDoesArtifactExist.class);

	private static final String ASCII_DOCTOR_API_ACCESS_ID = "org.eclipse.capra.ui.asciidoctor.apiaccess";
	private static final String ASCII_DOCTOR_API_ACCESS_CONFIG = "class";

	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String C_PROJECT_NAME = "CProject";
	private static final String CLASS_A_NAME = "A";
	private static final String CLASS_B_NAME = "B";

	private static final String MODEL_A_FILENAME = "modelA.ecore";
	private static final String MODEL_B_FILENAME = "modelB.ecore";
	private static final String C_FILE_NAME = "test.c";
	private static final String ASCIIDOC_FILE_NAME = "test.adoc";

	private static final String MODEL_A_NAME = "modelA";
	private static final String MODEL_B_NAME = "modelB";

	private static final String ASCII_DOC_TEXT = "= AsciiDoc Article Title\n"
			+ "Firstname Lastname <author@asciidoctor.org>\n" + "\n" + "== First Level Heading\n" + "\n"
			+ "== Second Level Heading";

	private static final String ASCII_DOC_TEXT_SHORT = "= AsciiDoc Article Title\n"
			+ "Firstname Lastname <author@asciidoctor.org>\n" + "\n" + "== First Level Heading";

	@Before
	public void init() throws CoreException {
		clearWorkspace();
		resetSelectionView();
		purgeModels();
	}

	@Test
	public void testDoesJavaArtifactExist() throws CoreException, IOException, InterruptedException {
		// Create a project
		IType javaClass = createJavaProjectWithASingleJavaClass(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));

		// Create a model and persist
		IProject testProject = getProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		// Create a trace via the selection view
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(javaClass);
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, javaClass));

		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resourceSet));
		Optional<IArtifactHandler<?>> javaHandler = artifactHelper.getHandler(javaClass);
		assertTrue(javaHandler.isPresent());
		assertTrue(javaHandler.get().doesArtifactExist(artifactHelper.createWrapper(javaClass)));

		javaClass.delete(true, new NullProgressMonitor());
		// Sleep to allow deletion to succeed...
		Thread.sleep(2000);
		assertFalse(javaHandler.get().doesArtifactExist(artifactHelper.createWrapper(javaClass)));
	}

	@Test
	public void testDoesCDTArtifactExist() throws CoreException, IOException, InterruptedException, BuildException {
		// Create a project
		ICProject cProject = TestHelper.createCDTProject(C_PROJECT_NAME);
		assertTrue(projectExists(C_PROJECT_NAME));
		ITranslationUnit cElement = TestHelper.createCSourceFileInProject(C_FILE_NAME, cProject);

		// Create a model and persist
		IProject ecoreProject = TestHelper.createSimpleProject(TEST_PROJECT_NAME);
		assertTrue(projectExists(TEST_PROJECT_NAME));
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(ecoreProject, a);

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(ecoreProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		// Create a trace via the selection view
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(cElement);
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, cElement));

		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resourceSet));
		Optional<IArtifactHandler<?>> javaHandler = artifactHelper.getHandler(cElement);
		assertTrue(javaHandler.isPresent());
		assertTrue(javaHandler.get().doesArtifactExist(artifactHelper.createWrapper(cElement)));

		cElement.delete(true, new NullProgressMonitor());
		// Sleep to allow deletion to succeed...
		Thread.sleep(2000);
		assertFalse(javaHandler.get().doesArtifactExist(artifactHelper.createWrapper(cElement)));
	}

	@Test
	public void testDoesAsciiDocArtifactExist()
			throws CoreException, IOException, InterruptedException, BuildException, URISyntaxException {
		// Create a project
		IProject testProject = TestHelper.createSimpleProject(TEST_PROJECT_NAME);
		EPackage a = TestHelper.createEcoreModel(MODEL_A_NAME);
		createEClassInEPackage(a, CLASS_A_NAME);
		save(testProject, a);
		AsciiDocArtifact asciiDocArtifact = createAsciiDocArtifact(testProject, ASCIIDOC_FILE_NAME, 94);
		assertNotNull(asciiDocArtifact);
		assertNotNull(asciiDocArtifact.getItem());
		assertNotNull(asciiDocArtifact.getUri());

		// Choose the EClass
		ResourceSet rs = new ResourceSetImpl();

		EPackage _a = load(testProject, MODEL_A_FILENAME, rs);
		assertEquals(_a.getName(), MODEL_A_NAME);
		EClass _A = (EClass) _a.getEClassifier(CLASS_A_NAME);

		// Create a trace via the selection view
		SelectionView.getOpenedView().dropToSelection(_A);
		SelectionView.getOpenedView().dropToSelection(asciiDocArtifact);
		createTraceForCurrentSelectionOfType(TracemodelPackage.eINSTANCE.getRelatedTo());

		// Check if trace has been created
		assertTrue(thereIsATraceBetween(_A, asciiDocArtifact));

		IPersistenceAdapter persistenceAdapter = ExtensionPointHelper.getPersistenceAdapter().get();
		ResourceSet resourceSet = EditingDomainHelper.getResourceSet();
		ArtifactHelper artifactHelper = new ArtifactHelper(persistenceAdapter.getArtifactWrappers(resourceSet));
		Optional<IArtifactHandler<?>> asciiDocHandler = artifactHelper.getHandler(asciiDocArtifact);
		assertTrue(asciiDocHandler.isPresent());
		assertTrue(asciiDocHandler.get().doesArtifactExist(artifactHelper.createWrapper(asciiDocArtifact)));

		// Delete Asciidoc file
		IFile asciiDocFile = testProject.getFile(ASCIIDOC_FILE_NAME);
		assertNotNull(asciiDocFile);
		asciiDocFile.delete(true, new NullProgressMonitor());
		// Sleep to allow deletion to succeed...
		Thread.sleep(2000);
		assertFalse(asciiDocHandler.get().doesArtifactExist(artifactHelper.createWrapper(asciiDocArtifact)));

		// Create file again with shorter text
		TestHelper.createFileContentInProject(ASCIIDOC_FILE_NAME, testProject.getName(), ASCII_DOC_TEXT_SHORT);
		assertFalse(asciiDocHandler.get().doesArtifactExist(artifactHelper.createWrapper(asciiDocArtifact)));

	}

	private AsciiDocArtifact createAsciiDocArtifact(IProject project, String fileName, int offset)
			throws CoreException, URISyntaxException, InterruptedException {
		AsciiDocArtifact artifact = null;
		IFile asciiDocFile = TestHelper.createFileContentInProject(fileName, project.getName(), ASCII_DOC_TEXT);
		Optional<IAsciiDocApiAccess> asciiDocApiAccessOpt = getAsciiDoctorAccess();
		if (asciiDocApiAccessOpt.isPresent()) {
			IAsciiDocApiAccess apiAccess = asciiDocApiAccessOpt.get();
			Item item = apiAccess.getItemFromAsciiDocText(offset, ASCII_DOC_TEXT);

			if (asciiDocFile != null) {
				URI uri = new URIBuilder().setScheme("platform").setPath("/resource" + asciiDocFile.getFullPath())
						.setFragment(Integer.toString(item.getOffset())).build();
				artifact = new AsciiDocArtifact(uri.toString(), item);
			}
		}
		return artifact;

	}

	private Optional<IAsciiDocApiAccess> getAsciiDoctorAccess() {
		try {
			Object extension = ExtensionPointHelper
					.getExtensions(ASCII_DOCTOR_API_ACCESS_ID, ASCII_DOCTOR_API_ACCESS_CONFIG).get(0);
			return Optional.of((IAsciiDocApiAccess) extension);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

}
