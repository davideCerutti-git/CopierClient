package commands;

import java.io.IOException;

public class RemoveStartupCommand implements Command {

	@Override
	public String execute() {
		String[] CMD={"del", "%userprofile%/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup/CopierClient"};
		try {
			Runtime.getRuntime().exec(CMD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
