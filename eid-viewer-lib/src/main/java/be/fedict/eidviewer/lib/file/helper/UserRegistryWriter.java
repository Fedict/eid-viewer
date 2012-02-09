/*
 * eID Middleware Project.
 * Copyright (C) 2010-2011 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see
 * http://www.gnu.org/licenses/.
 */
package be.fedict.eidviewer.lib.file.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/*
 *
 * @author Frank Marien
 * Reflection method Adapted from:
 * http://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java
 * http://lenkite.blogspot.com/2008/05/access-windows-registry-using-java.html
 * command-line method inspired by work from Oleg Ryaboy, which was based on work by Miguel Enriquez
 */
public class UserRegistryWriter
{
	private static final Logger				logger				=Logger.getLogger(UserRegistryWriter.class.getName());
	private static final int				HKEY_CURRENT_USER	=0x80000001;
	private static final int				REG_SUCCESS			=0;
	private static final int				KEY_ALL_ACCESS		=0xf003f;
	private static final int				KEY_READ			=0x20019;
	
	private Preferences						root;
	private Class<? extends Preferences>	userClass;
	private Method							regOpenKey, regCloseKey, regQueryValueEx, regCreateKeyEx, regSetValueEx;
	private boolean							reflectionStrategyAvailable=false;

	public UserRegistryWriter()
	{
		try
		{
			logger.finest("Creating userRegistryWriter");
			logger.finest("Setting Up Reflection Strategy");
			
			root			=Preferences.userRoot();
			userClass		=root.getClass();
			regOpenKey		=userClass.	getDeclaredMethod("WindowsRegOpenKey",		new Class[]{int.class,byte[].class,int.class});
			regCloseKey		=userClass.	getDeclaredMethod("WindowsRegCloseKey",		new Class[]{int.class});
			regQueryValueEx	=userClass.	getDeclaredMethod("WindowsRegQueryValueEx",	new Class[]{int.class,byte[].class});
			regCreateKeyEx	=userClass.	getDeclaredMethod("WindowsRegCreateKeyEx",	new Class[]{int.class,byte[].class});
			regSetValueEx	=userClass.	getDeclaredMethod("WindowsRegSetValueEx",	new Class[]{int.class,byte[].class,byte[].class});
			regOpenKey.					setAccessible(true);
			regCloseKey.				setAccessible(true);
			regQueryValueEx.			setAccessible(true);
			regCreateKeyEx.				setAccessible(true);
			regSetValueEx.				setAccessible(true);
			reflectionStrategyAvailable=true;
		}
		catch(Exception ex)
		{
			logger.log(Level.WARNING,"Reflection Strategy Not Available",ex);
		}
	}

	public boolean writeString(String key,String valueName,String value)
	{
		String verificationValue=null;
		logger.log(Level.FINEST,"WriteString {0} {1} {2}",new Object[]{key,valueName,value});

		try
		{
			if(reflectionStrategyAvailable)
			{
				logger.finest("Attempting write using reflection strategy");
				_createKeyUsingReflectionStrategy(key);
				_writeStringUsingReflectionStrategy(key,valueName,value);
				verificationValue=_readStringUsingReflectionStrategy(key,valueName);
			}

			if(verificationValue!=null&&verificationValue.equals(value))
			{
				logger.finest("Reflection strategy write succeeded");
				return true;
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Reflection Strategy Failed",ex);
		}

		if(verificationValue==null)
		{
			logger.finest("Reflection strategy write failed, attempting command line write");
			boolean clwSucceeded=_writeStringUsingRegCommand(key,valueName,value);
			logger.log(clwSucceeded?Level.FINEST:Level.SEVERE,"Command line write {0}",(clwSucceeded?"Succeeded":"Failed. Data Was Not Written!"));
			return clwSucceeded;
		}

		return false;
	}

	private String _readStringUsingReflectionStrategy(String key,String subKey) throws IllegalArgumentException,IllegalAccessException,InvocationTargetException
	{
		int[] handles=(int[])regOpenKey.invoke(root,new Object[]{new Integer(HKEY_CURRENT_USER),toCstr(key),new Integer(KEY_READ)});
		if(handles[1]!=REG_SUCCESS)
			return null;

		try
		{
			byte[] valb=(byte[])regQueryValueEx.invoke(root,new Object[]{new Integer(handles[0]),toCstr(subKey)});
			return (valb!=null?new String(valb).trim():null);
		}
		finally
		{
			if(handles[1]==REG_SUCCESS)
				regCloseKey.invoke(root,new Object[]{new Integer(handles[0])});
		}
	}

	private int[] _createKeyUsingReflectionStrategy(String key) throws IllegalArgumentException,IllegalAccessException,InvocationTargetException
	{
		return (int[])regCreateKeyEx.invoke(root,new Object[]{new Integer(HKEY_CURRENT_USER),toCstr(key)});
	}

	private void _writeStringUsingReflectionStrategy(String key,String valueName,String value) throws IllegalArgumentException,IllegalAccessException,InvocationTargetException
	{
		int[] handles=(int[])regOpenKey.invoke(root,new Object[]{new Integer(HKEY_CURRENT_USER),toCstr(key),new Integer(KEY_ALL_ACCESS)});
		if(handles[1]!=REG_SUCCESS)
			return;

		try
		{
			regSetValueEx.invoke(root,new Object[]{new Integer(handles[0]),toCstr(valueName),toCstr(value)});
		}
		finally
		{
			if(handles[1]==REG_SUCCESS)
				regCloseKey.invoke(root,new Object[]{new Integer(handles[0])});
		}
	}

	private boolean _writeStringUsingRegCommand(String key,String valueName,String value)
	{
		Process process=null;
		try
		{
			process=Runtime.getRuntime().exec("reg add HKCU\\"+key+" /v "+valueName+" /t REG_SZ /d "+value+" /f");
			process.waitFor();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,null,ex);
		}
		return ((process!=null)&&(process.exitValue()==0));
	}

	private static byte[] toCstr(String str)
	{
		byte[] result=new byte[str.length()+1];
		for(int i=0;i<str.length();i++)
			result[i]=(byte)str.charAt(i);
		result[str.length()]=0;
		return result;
	}
}

