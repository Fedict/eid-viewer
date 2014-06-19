package be.fedict.eidviewer.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import be.fedict.eidviewer.gui.ViewerPrefs;

public class VersionChecker extends Observable implements Runnable {
	private static final Logger log = Logger.getLogger(VersionChecker.class.getName());
	private boolean running = false;
	private Thread t;
	
	private class Version implements Comparable<Version> {
		private int[] components;
		private String presentation;

		public Version(String vers) {
			presentation = vers;
			StringTokenizer tok = new StringTokenizer(vers, ".");
			int i;
			components = new int[tok.countTokens()];
			for(i=0; tok.countTokens() > 0; i++) {
				components[i] = Integer.parseInt(tok.nextToken());
			}
		}
		
		public int compareTo(Version arg0) {
			int retval = 0;
			int i;
			for(i=0; i < components.length && i < arg0.components.length && retval == 0; i++) {
				retval = components[i] - arg0.components[i];
			}
			return retval;
		}
		public String toString() {
			return presentation;
		}
	}

	private Version myVersion;
	private Version latestVersion;
	
	public VersionChecker(String currVersion) {
		myVersion = new Version(currVersion);
		latestVersion = new Version("0.0.0");
	}
	
	public String getLatestVersion() {
		return latestVersion.toString();
	}

	private void doCheckVersions() {
		log.fine("Checking for new viewer version");
		URL u = null;
		try {
			u = new URL("https://dist.eid.belgium.be/versions");
		} catch (MalformedURLException e) {
			// can not happen (bar documentation or implementation errors)
			return;
		}
		Properties p = new Properties();
		try {
			Proxy proxy = ViewerPrefs.getProxy();
			URLConnection c = u.openConnection(proxy);
			c.connect();
			InputStream i = c.getInputStream();
			p.load(i);
		} catch (IOException e) {
			// try again later
			log.warning("Could not reach eID software repository website");
			return;
		}
		Version v = new Version(p.getProperty("eid-viewer"));
		if(latestVersion.compareTo(v) != 0) {
			if(myVersion.compareTo(v) != 0) {
				log.info("new version found: " + v.toString());
			} else {
				log.info("current version is most recent: " + v);
			}
			setChanged();
		}
		latestVersion = v;
	}

	public void run() {
		running = true;
		while(running) {
			doCheckVersions();
			try {
				Thread.sleep(60*60*1000); // one hour
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public void start() {
		t = new Thread(this);
		t.start();
	}
	
	public void stop() {
		running = false;
		t.interrupt();
	}
	
	public boolean hasNewVersion() {
		boolean retval = (myVersion.compareTo(latestVersion) < 0);
		log.finest("Comparing " + myVersion.toString() + " to " + latestVersion.toString() + ": " + retval);
		return retval;
	}
}
