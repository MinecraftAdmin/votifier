package com.vexsoftware.votifier.model;

import java.io.File;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import com.vexsoftware.votifier.Votifier;


/**
 * Loads vote listeners. Listeners that cannot be instantiated will be skipped.
 * 
 * @author Blake Beaupain
 */
public class ListenerLoader {

	/**
	 * Loads all listener class files from a directory.
	 * 
	 * @param directory The directory
	 */
	public static List<VoteListener> load( String directory ) /* throws Exception */{
		List<VoteListener> listeners = new ArrayList<VoteListener>();
		File dir = new File( directory );

		// Load the vote listener instances.
		ClassLoader loader;
		try {
			loader = new URLClassLoader( new URL[] { dir.toURI().toURL() }, VoteListener.class.getClassLoader() );
		}
		catch ( MalformedURLException ex ) {
			Votifier.log( Level.SEVERE, "Error while configuring listener class loader", ex );
			return listeners;
		}
		for ( File file : dir.listFiles() ) {
			if ( !file.getName().endsWith( ".class" ) ) {
				continue; // Only load class files!
			}
			String name = file.getName().substring( 0, file.getName().lastIndexOf( "." ) );

			try {
				Class<?> clazz = loader.loadClass( name );
				Object object = clazz.newInstance();
				if ( !(object instanceof VoteListener) ) {
					Votifier.logInfo( "Not a vote listener: " + clazz.getSimpleName() );
					continue;
				}
				VoteListener listener = (VoteListener) object;
				listeners.add( listener );
				Votifier.logInfo( "Loaded vote listener: " + listener.getClass().getSimpleName() );
			}
			/*
			 * Catch the usual definition and dependency problems with a loader and skip the problem listener.
			 */
			catch ( Exception ex ) {
				Votifier.log( Level.WARNING, "Error loading '" + name + "' listener! Listener disabled." );
			}
			catch ( Error ex ) {
				Votifier.log( Level.WARNING, "Error loading '" + name + "' listener! Listener disabled." );
			}
		}
		return listeners;
	}
}
