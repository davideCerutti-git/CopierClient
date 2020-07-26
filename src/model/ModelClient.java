package model;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

import SlaveNode.SlaveClientNode;
import commands.Command;
import commands.CommandRegister;
import commands.GetComputerNameCommand;
import commands.RestartCommand;
import commands.SetStartupCommand;
import commands.RemoveStartupCommand;
import settings.Settings;

public class ModelClient {

	private Settings settings;
	public static final Logger logger = Logger.getLogger(ModelClient.class.getName());
	private LocalFtpServer ftpServer;
	private UserInteractionListener uil;
	private SlaveClientNode slaveClientNode;
	private InetAddress serverAddress;
	private int serverPort, clientServerPort;
	private CommandRegister commandRegister ;
	private String clientName;
	
	public ModelClient()  {
		readSettings();
		this.slaveClientNode = new SlaveClientNode(serverAddress, serverPort, clientServerPort, logger, clientName, this);
		this.commandRegister= new CommandRegister();
		this.commandRegister.register("get name", new GetComputerNameCommand(this));
		this.commandRegister.register("set startup", new SetStartupCommand());
		this.commandRegister.register("remove startup", new RemoveStartupCommand());
		this.commandRegister.register("restart", new RestartCommand());
		this.slaveClientNode.start();
		this.ftpServer=new LocalFtpServer(logger);
		this.ftpServer.startServer();
		this.uil=new UserInteractionListener(logger,this);
		this.uil.start();
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
		clientServerPort = Integer.parseInt(settings.getProperty("clientServerPort"));
		clientName = settings.getProperty("clientName").trim();
	}

	public CommandRegister getCommandRegister() {
		return commandRegister;
	}
	
	public SlaveClientNode getClient() {
		return slaveClientNode;
	}

	public void close() {
		slaveClientNode.close();
		
	}

	public SlaveClientNode getSlaveClientNode() {
		return slaveClientNode;
	}

	public void setSlaveClientNode(SlaveClientNode slaveClientNode) {
		this.slaveClientNode = slaveClientNode;
	}
	
}
