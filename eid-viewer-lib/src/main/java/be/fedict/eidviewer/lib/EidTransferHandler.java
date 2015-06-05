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
	private static final Logger logger = Logger.getLogger(EidTransferHandler.class.getName());
	private PCSCEidController ctrl;
	public EidTransferHandler(PCSCEidController eid) {
		ctrl = eid;
	}
	@Override
	public Transferable createTransferable(JComponent comp) {
		logger.fine("Attempting drag-and-drop operation");
		switch(ctrl.getState()) {
		case EID_PRESENT:
		case FILE_LOADED:
			break;
		default:
			logger.fine("No data, aborting drag-and-drop operation");
			return null;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ctrl.saveToXMLFile(os);
		} catch (Exception e) {
			return null;
		}
		logger.fine("Creating data handler for drag-and-drop operation...");
		return new StringSelection(os.toString());
	}
	@Override
	public int getSourceActions(JComponent c) {
		if(eid.equals(null)) {
			return javax.swing.TransferHandler.NONE;
		}
		logger.fine("enabling drag-and-drop");
		return javax.swing.TransferHandler.COPY;
	}
}
