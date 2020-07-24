package model;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

import SlaveNode.SlaveClientNode;
import commands.Command;
import commands.CommandRegister;
import commands.GetComputerNameCommand;
import settings.Settings;

public class ModelClient {

	private InetAddress serverAddress;
	private int serverPort;
	private SlaveClientNode slaveClientNode;
	private Settings settings;
	public static final Logger logger = Logger.getLogger(ModelClient.class.getName());
	private LocalFtpServer ftpServer;
	private UserInteractionListener uil;
	private String clientName;
	CommandRegister commandRegister ;

	public ModelClient()  {
		readSettings();
		slaveClientNode = new SlaveClientNode(serverAddress, serverPort, logger, clientName, this);
		commandRegister= new CommandRegister();
		commandRegister.register("get name", new GetComputerNameCommand());
		slaveClientNode.start();
		ftpServer=new LocalFtpServer(logger);
		ftpServer.startServer();
		uil=new UserInteractionListener(logger,this);
		uil.start();
	}

	private void readSettings() {
		settings = new Settings();
		try {
			settings.load(new FileReader("properties/settings.cfg"));
		} catch (IOException e) {
			logger.error(e);
		}
		try {
			serverAddress = InetAddress.getByName(settings.getProperty("copierServerIpAddress"));
		} catch (UnknownHostException e) {
			logger.error(e);
		}
		serverPort = Integer.parseInt(settings.getProperty("copierServerPort"));
		clientName = settings.getProperty("clientName").trim();
	}

	public CommandRegister getCommandRegister() {
		return commandRegister;
	}
	
	public SlaveClientNode getClient() {
		return slaveClientNode;
	}
	
}
