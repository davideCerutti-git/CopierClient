package commands;

import java.io.IOException;

public class RestartCommand implements Command {

	@Override
	public String execute() {
		String CMD="shutdown /r";
		try {
			Runtime.getRuntime().exec(CMD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
