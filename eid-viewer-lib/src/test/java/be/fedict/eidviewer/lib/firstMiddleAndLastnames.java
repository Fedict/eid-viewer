package be.fedict.eidviewer.lib;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.fedict.eid.applet.service.Identity;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;

public class firstMiddleAndLastnames
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public final void testCreateFullNameString()
	{	
		String[][] testStrings={{"Alfred",	"Jodokus",	"Kwak",	"Alfred Jodokus Kwak"	},
								{"Alfred",	"Jodokus",	 null,	"Alfred Jodokus"		},
								{"Alfred",	 null,		"Kwak",	"Alfred Kwak"			},
								{"Alfred",	 null,		 null,	"Alfred"				},
								{ null,		"Jodokus",	"Kwak",	"Jodokus Kwak"			},
								{ null,		"Jodokus",	 null,	"Jodokus"				},
								{ null,		 null,	 	"Kwak",	"Kwak"					},
								{ null,		 null,	 	 null,	""						}};
		
		for(int i=0;i<testStrings.length;i++)
		{
			String fullName=TextFormatHelper.createFullNameString(testStrings[i][0],testStrings[i][1],testStrings[i][2]);
			System.err.println("[" + fullName + "]");
			assertTrue(fullName.equals(testStrings[i][3]));
		}
	}

	@Test
	public final void testSetFirstNamesFromStrings()
	{
		String[][] testStrings={{"Alfred Jodokus",	"H",	"Alfred",	"Jodokus H"	},
								{"Alfred Jodokus",	 null,	"Alfred",	"Jodokus"	},
								{ null,				 null,	"",			""			},
								{"Alfred Jodokus",	"",		"Alfred",	"Jodokus"	},
								{"",				"",		"",			""			}};
		
		
																		
		for(int i=0;i<testStrings.length;i++)
		{
			Identity identity=new Identity();
			int howMany=TextFormatHelper.setFirstNamesFromStrings(identity, testStrings[i][0], testStrings[i][1]);
			assertTrue(identity.firstName.equals(testStrings[i][2]) && identity.middleName.equals(testStrings[i][3]));
		}
	}
	
			

	@Test
	public final void testSetFirstNamesFromString()
	{
		String[][] testStrings={{"Alfred Jodokus H",	"Alfred",	"Jodokus H"	},
								{"Alfred Jodokus",		"Alfred",	"Jodokus"	},
								{ null,				 	"",			""			},
								{ "",				 	"",			""			}};


														
		for(int i=0;i<testStrings.length;i++)
		{
			Identity identity=new Identity();
			int howMany=TextFormatHelper.setFirstNamesFromString(identity, testStrings[i][0]);
			assertTrue(identity.firstName.equals(testStrings[i][1]) && identity.middleName.equals(testStrings[i][2]));
		}
	}

}
