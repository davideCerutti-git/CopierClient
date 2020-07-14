package model;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

import settings.Settings;

public class ModelClient {

	private InetAddress serverAddress;
	private int serverPort;
	private ClientThread clientThread;
	private Settings settings;
	public static final Logger log = Logger.getLogger(ModelClient.class.getName());
	private LocalFtpServer ftpServer;
	private UserInteractionListener uil;
	private String clientName;

	public ModelClient()  {
		readSettings();
		clientThread = new ClientThread(serverAddress, serverPort, log, clientName);
		clientThread.start();
		ftpServer=new LocalFtpServer(log);
		ftpServer.startServer();
		uil=new UserInteractionListener(log);
		uil.start();
	}

	/**
	 * 
	 */
	private void readSettings() {
		settings = new Settings();
		try {
			settings.load(new FileReader("properties/settings.cfg"));
		} catch (IOException e) {
			log.error(e);
		}
		try {
			serverAddress = InetAddress.getByName(settings.getProperty("copierServerIpAddress"));
		} catch (UnknownHostException e) {
			log.error(e);
		}
		serverPort = Integer.parseInt(settings.getProperty("copierServerPort"));
		clientName = settings.getProperty("clientName").trim();
	}
}
