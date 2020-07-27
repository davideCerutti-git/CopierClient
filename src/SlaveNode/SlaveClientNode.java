package SlaveNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import command.AbstractCommand;
import model.ModelClient;

public class SlaveClientNode extends Thread {

	//Fields:
	private Socket socket = null;

	private boolean stopThread;
	private boolean connectedToServer = false;
	private InetAddress serverAddress;
	private int serverPort, clientServerPort;
	
	//Refs:
	private ModelClient modelClient;
	//Utils:
	private String strLine;
	private final Logger logger;
	
	//Owns:
	private BufferedReader inStream = null;
	private PrintWriter outStream = null;

	public SlaveClientNode(InetAddress _serverAddress, int _serverPort, int _clientServerPort, Logger _logger, ModelClient _modelClient) {
		serverAddress = _serverAddress;
		serverPort = _serverPort;
		clientServerPort=_clientServerPort;
		logger = _logger;
		modelClient = _modelClient;
	}

	@Override
	public void run() {
		logger.debug("MainClientThred: started");
		while (!stopThread) {
			while (!connectedToServer && !stopThread) {
				socket = null;
				outStream = null;
				inStream = null;
				try {
					socket = new Socket(serverAddress, serverPort);
					connectedToServer = true;
					modelClient.setSlaveServerNode(new SlaveServerNode(clientServerPort, logger, modelClient));
					modelClient.getSlaveServerNode().start();
				} catch (IOException e) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						logger.error(ie);
					}
				}
			}

			logger.debug("Client Socket: " + socket);
			// creation input/output streams from socket
			try {
				inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				outStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				while (!stopThread) {
					// Receiver
					logger.debug("waiting commands...");
					strLine = inStream.readLine();
					logger.debug("recived: " + strLine);
					AbstractCommand c = modelClient.getCommandRegister().getCommandByName(strLine);
					logger.debug("responding...");
					outStream.write(c.execute("") + "\n");
					outStream.flush();
				}
			} catch (UnknownHostException e) {
				logger.error("Don’t know about host " + serverAddress);
				System.exit(1);
			} catch (IOException e) {
				logger.debug("Lost connection to: " + serverAddress);
				connectedToServer = false;
			}
		}

		logger.debug("EchoClient: closing...");
		outStream.close();
		try {
			inStream.close();
			socket.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void close() {
		if (modelClient.getSlaveServerNode() != null)
			modelClient.getSlaveServerNode().close();
		this.stopThread = true;
		this.connectedToServer = false;
	}

}
