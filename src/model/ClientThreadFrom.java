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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import commands.Command;

public class ClientThreadFrom extends Thread {

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
	private ModelClient m;
//	private BlockingQueue<String> commandsQueue;
	private String strLine;
	private ClientThreadTo clientTo;

	public ClientThreadFrom(InetAddress _serverAddress, int _port, Logger _log, String _clientName, ModelClient _m) {
//		commandsQueue=new LinkedBlockingQueue<>();
		serverAddress = _serverAddress;
		port = _port;
		log = _log;
		clientName=_clientName;
		m=_m;
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
					clientTo=new ClientThreadTo(socket.getInetAddress(),1051,log,"",m);
					clientTo.start();
				} catch (IOException e) {
					//log.debug("Connect failed, waiting and trying again...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						log.error(ie);
					}
				}
			}
			
			log.debug("Client Socket: " + socket);
			// creation input/output streams from socket
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				while (true) {
					//Receiver
					log.debug("waiting commands...");
					strLine=in.readLine();
					log.debug("recived: "+strLine);
					Command c=m.getCommandRegister().getCommandByName(strLine);
					log.debug("responding...");
					out.write(c.execute()+"\n");
					out.flush();
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

	public BlockingQueue<String> getCommandsQueue() {
		return clientTo.getCommandsQueue();
	}
	
}
