package commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import model.ModelClient;

public class GetComputerNameCommand implements Command {
	ModelClient model;
	
	public GetComputerNameCommand(ModelClient _model) {
		model=_model;
	}

	@Override
	public String execute() {
		String result="";
		try {
			result=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return e.getMessage();
		}
		model.getSlaveClientNode().getCommandsQueue().add("set name: " +result);
		
		return result;
	}
}
