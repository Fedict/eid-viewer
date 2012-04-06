package be.fedict.eidviewer.gui;

import javax.swing.AbstractAction;

public abstract class DynamicLocaleAbstractAction extends AbstractAction
{
	private static final long	serialVersionUID	= -2017438927248089386L;
   
    public DynamicLocaleAbstractAction(String text)
    {
        super(text);
    }
    
    public DynamicLocaleAbstractAction setName(String text)
    {
        super.putValue(NAME, text);
        return this;
    }
}
