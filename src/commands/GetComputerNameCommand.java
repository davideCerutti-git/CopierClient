package commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetComputerNameCommand implements Command {

	@Override
	public String execute() {
		String result="";
		try {
			result=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return e.getMessage();
		}
		return result;
	}
}
