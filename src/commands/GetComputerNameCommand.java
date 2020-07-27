package commands;

import java.net.InetAddress;
import java.net.UnknownHostException;
import command.AbstractCommand;
import model.ModelClient;

public class GetComputerNameCommand extends AbstractCommand {

	public GetComputerNameCommand(ModelClient _model) {
		super(_model);
	}

	@Override
	public String execute(String args) {
		String result="";
		try {
			result=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			model.getLogger().error(e);
			return e.getMessage();  //TODO rivedere stringa di ritorno
		}
		model.getCommandsQueue().add("set name: " +result);
		return result;
	}
	
}
