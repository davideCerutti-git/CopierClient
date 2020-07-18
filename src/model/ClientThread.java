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

import org.apache.log4j.Logger;

import commands.Command;

public class ClientThread extends Thread {

	private Socket socketReceiver = null;
	private Socket socketSender = null;
	private BufferedReader inReceiver = null;
	private PrintWriter outReceiver = null;
	private BufferedReader inSender = null;
	private PrintWriter outSender = null;
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
	private BlockingQueue<String> commandsQueue;
	private String strLine;

	public ClientThread(InetAddress _serverAddress, int _port, Logger _log, String _clientName, ModelClient _m) {
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
				socketReceiver = null;
				outReceiver = null;
				inReceiver = null;
				osw = null;
				bw = null;
				isr = null;
				try {
					socketReceiver = new Socket(serverAddress, port);
					socketSender = new Socket(serverAddress, port);
					connectedToServer = true;
				} catch (IOException e) {
					//log.debug("Connect failed, waiting and trying again...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						log.error(ie);
					}
				}
			}
			
			log.debug("Client Socket: " + socketReceiver);
			// creation input/output streams from socket
			try {
				inReceiver = new BufferedReader(new InputStreamReader(socketReceiver.getInputStream()));
				outReceiver = new PrintWriter(new OutputStreamWriter(socketReceiver.getOutputStream()), true);
				inSender = new BufferedReader(new InputStreamReader(socketSender.getInputStream()));
				outSender = new PrintWriter(new OutputStreamWriter(socketSender.getOutputStream()), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				while (true) {
					//Receiver
					log.debug("waiting commands...");
					strLine=inReceiver.readLine();
					log.debug("recived: "+strLine);
					Command c=m.getCommandRegister().getCommandByName(strLine);
					log.debug("responding...");
					outReceiver.write(c.execute()+"\n");
					outReceiver.flush();
					
					//Sender
					strLine=commandsQueue.poll();
					if (strLine != null) {
						outSender.write(strLine+"\n");
						outSender.flush();
						log.debug("printed: " + strLine);
						strLine = null;
						log.debug("Response: "+inSender.readLine());
					}
					
					
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
		outReceiver.close();
		try {
			inReceiver.close();
			socketReceiver.close();
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * 
	 */
	private void createOutputStream() {
		try {
			osw = new OutputStreamWriter(socketReceiver.getOutputStream());
		} catch (IOException e1) {
			log.error(e1);
		}
		bw = new BufferedWriter(osw);
		outReceiver = new PrintWriter(bw, true);
	}

	/**
	 * 
	 */
	private void createInputStream() {
		try {
			isr = new InputStreamReader(socketReceiver.getInputStream());
		} catch (IOException e1) {
			log.error(e1);
		}
		inReceiver = new BufferedReader(isr);
	}

	/**
	 * @throws IOException
	 */
	private void checkForServerCommands() throws IOException {
		if (inReceiver.readLine().trim().startsWith(Message.CopyFiles)) {
			log.debug(" Command \"CopyFile\" recived");
		}else if(inReceiver.readLine().trim().startsWith(Message.Alive)) {	
		} 
	}

	public BlockingQueue<String> getCommandsQueue() {
		return commandsQueue;
	}
	
	
}
