package SlaveNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import model.ModelClient;

public class SlaveServerNode extends Thread {

	//Fields:
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private ServerSocket serverSocket;
	private int port;
	
	//Utils;
	private Logger logger;
	private boolean runningServerThread = true;
	private String outLine;
	
	//Refs:
	private ModelClient modelClient;
	
	public SlaveServerNode(int _port, Logger _logger, ModelClient _modelClient) {
		port = _port;
		logger = _logger;
		modelClient = _modelClient;
		modelClient.setCommandsQueue(new LinkedBlockingQueue<String>());
		try {
			serverSocket = new ServerSocket(port);
			logger.debug("created server on client, port: "+port);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@Override
	public void run() {
		try {
			logger.debug("accepting connection");
			socket = serverSocket.accept();
			logger.debug("connection ok");
		} catch (IOException e) {
			logger.error(e);
		}
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			logger.error(e);
		}
		try {
			while (runningServerThread) {
				// WRITE commands to Client
				outLine=modelClient.getCommandsQueue().poll();
				if (outLine != null) {
					out.write(outLine+"\n");
					out.flush();
					logger.debug("C: " + outLine);
					outLine = null;
					// Read response from Client
					logger.debug("R: "+in.readLine());
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void close() {
		this.runningServerThread=false;
		
	}

}
