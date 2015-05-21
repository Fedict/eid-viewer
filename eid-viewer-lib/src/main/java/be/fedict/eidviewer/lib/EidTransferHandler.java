package be.fedict.eidviewer.lib;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayOutputStream;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class EidTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -402732694948104329L;
	private PCSCEidController ctrl;
	public EidTransferHandler(PCSCEidController eid) {
		ctrl = eid;
	}
	@Override
	public Transferable createTransferable(JComponent comp) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ctrl.saveToXMLFile(os);
		return new StringSelection(os.toString());
	}
	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}
}
