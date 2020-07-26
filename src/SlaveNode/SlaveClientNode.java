package SlaveNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

import command.AbstractCommand;
import model.ModelClient;

public class SlaveClientNode extends Thread {

	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private InetAddress serverAddress;
	private int serverPort, clientServerPort;
	private boolean connectedToServer = false;
	private InputStreamReader isr;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	private String userInput;
	private final Logger logger;
	private boolean stopThread;
	private String clientName;
	private ModelClient m;
//	private BlockingQueue<String> commandsQueue;
	private String strLine;
	private SlaveServerNode clientTo;

	public SlaveClientNode(InetAddress _serverAddress, int _serverPort, int _clientServerPort, Logger _log, String _clientName, ModelClient _m) {
//		commandsQueue=new LinkedBlockingQueue<>();
		serverAddress = _serverAddress;
		serverPort = _serverPort;
		clientServerPort=_clientServerPort;
		logger = _log;
		clientName = _clientName;
		m = _m;
	}

	@Override
	public void run() {
		logger.debug("MainClientThred: started");
		while (!stopThread) {
			while (!connectedToServer && !stopThread) {
				socket = null;
				out = null;
				in = null;
				osw = null;
				bw = null;
				isr = null;
				try {
					socket = new Socket(serverAddress, serverPort);
					connectedToServer = true;
					clientTo = new SlaveServerNode(socket.getInetAddress(), clientServerPort, logger, "", m);
					clientTo.start();
				} catch (IOException e) {
					// log.debug("Connect failed, waiting and trying again...");
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
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				while (!stopThread) {
					// Receiver
					logger.debug("waiting commands...");
					strLine = in.readLine();
					logger.debug("recived: " + strLine);
					AbstractCommand c = m.getCommandRegister().getCommandByName(strLine);
					logger.debug("responding...");
					out.write(c.execute("") + "\n");
					out.flush();
				}
			} catch (UnknownHostException e) {
				System.err.println("Don’t know about host " + serverAddress);
				System.exit(1);
			} catch (IOException e) {
				logger.debug("Lost connection to: " + serverAddress);
				connectedToServer = false;
			}
		}

		logger.debug("EchoClient: closing...");
		out.close();
		try {
			in.close();
			socket.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public BlockingQueue<String> getCommandsQueue() {
		return clientTo.getCommandsQueue();
	}

	public void close() {
		if (clientTo != null)
			clientTo.close();
		this.stopThread = true;
		this.connectedToServer = false;

	}

	public SlaveServerNode getClientTo() {
		return clientTo;
	}

	public void setClientTo(SlaveServerNode clientTo) {
		this.clientTo = clientTo;
	}
	
	

}
