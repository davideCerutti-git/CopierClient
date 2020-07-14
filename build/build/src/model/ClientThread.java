package model;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class ClientThread extends Thread {

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
	private Logger log;
	private boolean stopThread;
	private String clientName;

	public ClientThread(InetAddress _serverAddress, int _port, Logger _log, String _clientName) {
		serverAddress = _serverAddress;
		port = _port;
		log = _log;
		clientName=_clientName;
	}

	@Override
	public void run() {
		log.debug("MainClientThred: started");
		while (!stopThread) {
			while (!connectedToServer && !stopThread) {
				socket = null;
				out = null;
				in = null;
				osw = null;
				bw = null;
				isr = null;
				try {
					socket = new Socket(serverAddress, port);
					connectedToServer = true;
				} catch (IOException e) {
					log.debug("Connect failed, waiting and trying again...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						log.error(ie);
					}
				}
			}
			
			log.debug("Client Socket: " + socket);
			// creation input stream from socket
			createInputStream();
			// creation output stream from socket
			createOutputStream();
			out.write(clientName);
			log.debug("sending name "+clientName+" to server..");
			try {
				while (true) {
					checkForServerCommands();
				}
			} catch (UnknownHostException e) {
				System.err.println("Don’t know about host " + serverAddress);
				System.exit(1);
			} catch (IOException e) {
				log.debug("Lost connection to: " + serverAddress);
				connectedToServer = false;
			}
		}

		log.debug("EchoClient: closing...");
		out.close();
		try {
			in.close();
			socket.close();
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * 
	 */
	private void createOutputStream() {
		try {
			osw = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e1) {
			log.error(e1);
		}
		bw = new BufferedWriter(osw);
		out = new PrintWriter(bw, true);
	}

	/**
	 * 
	 */
	private void createInputStream() {
		try {
			isr = new InputStreamReader(socket.getInputStream());
		} catch (IOException e1) {
			log.error(e1);
		}
		in = new BufferedReader(isr);
	}

	/**
	 * @throws IOException
	 */
	private void checkForServerCommands() throws IOException {
		if (in.readLine().trim().startsWith(Message.CopyFiles)) {
			log.debug(" Command \"CopyFile\" recived");
		}else if(in.readLine().trim().startsWith(Message.Alive)) {	
		} 
	}
}
