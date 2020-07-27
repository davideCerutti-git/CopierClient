package model;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import SlaveNode.SlaveClientNode;
import SlaveNode.SlaveServerNode;
import command.CommandRegister;
import commands.GetComputerNameCommand;
import commands.RemoveStartupCommand;
import commands.RestartCommand;
import commands.SetStartupCommand;
import settings.Settings;

public class ModelClient {

	//Utils:
	private Settings settings;
	public static final Logger logger = Logger.getLogger(ModelClient.class.getName());
	
	//Fileds:
	private InetAddress serverAddress;
	private int serverPort, clientServerPort;
	
	//Owns:
	private SlaveClientNode slaveClientNode;
	private SlaveServerNode slaveServerNode;
	private LocalFtpServer ftpServer;
	private UserInteractionListener uil;
	private BlockingQueue<String> commandsQueue;
	private CommandRegister commandRegister ;
	
	public ModelClient()  {
		readSettings();
		this.slaveClientNode = new SlaveClientNode(serverAddress, serverPort, clientServerPort, logger, this);
		setupCommands();
		this.slaveClientNode.start();
		this.ftpServer=new LocalFtpServer(logger);
		this.ftpServer.startServer();
		this.uil=new UserInteractionListener(logger,this);
		this.uil.start();
	}

	private void setupCommands() {
		this.commandRegister= new CommandRegister();
		this.commandRegister.register("get name", new GetComputerNameCommand(this));
		this.commandRegister.register("set startup", new SetStartupCommand(this));
		this.commandRegister.register("remove startup", new RemoveStartupCommand(this));
		this.commandRegister.register("restart", new RestartCommand(this));
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

	public Logger getLogger() {
		return ModelClient.logger;
	}
	
	public SlaveServerNode getSlaveServerNode() {
		return slaveServerNode;
	}

	public void setSlaveServerNode(SlaveServerNode slaveServerNode) {
		this.slaveServerNode = slaveServerNode;
	}
	
	public BlockingQueue<String> getCommandsQueue() {
		return commandsQueue;
	}

	public void setCommandsQueue(LinkedBlockingQueue<String> _commandQueue) {
		this.commandsQueue=_commandQueue;;
		
	}
	
}
