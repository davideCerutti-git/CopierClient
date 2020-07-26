package SlaveNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import model.ModelClient;

public class SlaveServerNode extends Thread {

	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private InetAddress serverAddress;
	private int port;
	private boolean connectedToServer = false;
	private InputStreamReader isr;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	private String userInput;
	private Logger logger;
	private boolean stopThread;
	private String clientName;
	private ModelClient m;
	private BlockingQueue<String> commandsQueue;
	private String strLine;
	private ServerSocket serverSocket;
	private String outLine;
	private boolean runningServerThread = true;

	public SlaveServerNode(InetAddress _serverAddress, int _port, Logger _log, String _clientName, ModelClient _m) {
		
		serverAddress = _serverAddress;
		port = _port;
		logger = _log;
		clientName = _clientName;
		m = _m;
		this.commandsQueue = new LinkedBlockingQueue<>();
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
				//log.debug("waiting commands");
				outLine=commandsQueue.poll();
				if (outLine != null) {
					out.write(outLine+"\n");
					out.flush();
					logger.debug("C: " + outLine);
					outLine = null;
					// Read response from Client
					//log.debug("waiting response");
					logger.debug("R: "+in.readLine());
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public BlockingQueue<String> getCommandsQueue() {
		return commandsQueue;
	}

	public void close() {
		this.runningServerThread=false;
		
	}

}
