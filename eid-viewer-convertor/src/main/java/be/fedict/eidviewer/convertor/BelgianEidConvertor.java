/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fedict.eidviewer.convertor;
import be.fedict.eid.applet.Messages;
import be.fedict.eid.applet.Status;
import be.fedict.eid.applet.View;
import be.fedict.eidviewer.lib.PCSCEid;
import be.fedict.eidviewer.lib.PCSCEidController;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author wouter
 */
public class BelgianEidConvertor implements View {

    /**
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        if(args.length < 2) {
            throw new Exception("Need two arguments: input filename and output filename");
        }
        View v = new BelgianEidConvertor();
        PCSCEid eid;
        PCSCEidController control;
        eid = new PCSCEid(v, Locale.US);
        control = new PCSCEidController(eid);
        File input = new File(args[0]);
        File output = new File(args[1]);
        control.loadFromFile(input);
        control.saveToXMLFile(output);
    }

    @Override
    public void addDetailMessage(String string) {
        System.out.println(string);
    }

    @Override
    public void setStatusMessage(Status status, Messages.MESSAGE_ID msgd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean privacyQuestion(boolean bln, boolean bln1, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Component getParentComponent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressIndeterminate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetProgress(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void increaseProgress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void confirmAuthenticationSignature(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int confirmSigning(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
