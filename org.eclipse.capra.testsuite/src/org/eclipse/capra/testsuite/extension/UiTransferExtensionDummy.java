package org.eclipse.capra.testsuite.extension;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class UiTransferExtensionDummy extends Transfer {

	private static final String MIME_TYPE = "UiTransferExtensionDummy";
	private static final int MIME_TYPE_ID = registerType(MIME_TYPE);

	@Override
	public TransferData[] getSupportedTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSupportedType(TransferData transferData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MIME_TYPE_ID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MIME_TYPE };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		// TODO Auto-generated method stub
		return null;
	}

}
