package org.rs2server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.rs2server.rs2.RS2Server;
import org.rs2server.rs2.model.World;
import org.rs2server.util.ConfigurationParser;


/**
 * A class to start both the file and game servers.
 * @author Graham Edgecombe
 *
 */
public class Server {
	
	/**
	 * The protocol version.
	 */
	public static final int VERSION = 474;
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	
	/**
	 * The entry point of the application.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) throws Exception {
		logger.info("Starting Hyperion...");
        ConfigurationParser.parse();
		World.getWorld(); // this starts off background loading
		try {
			new RS2Server().bind(RS2Server.PORT).start();
		} catch(Exception ex) {
			logger.log(Level.SEVERE, "Error starting Hyperion.", ex);
		//	System.exit(1);
		}
	}

}
