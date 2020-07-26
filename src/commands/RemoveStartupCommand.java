package commands;

import java.io.IOException;

import command.AbstractCommand;
import model.ModelClient;

public class RemoveStartupCommand extends AbstractCommand {

	public RemoveStartupCommand(ModelClient _model) {
		super(_model);
	}

	@Override
	public String execute(String args) { //TODO non funziona
		String[] CMD={"del", "%userprofile%/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup/CopierClient"};
		try {
			Runtime.getRuntime().exec(CMD);
		} catch (IOException e) {
			(model.getLogger()).error(e);
		}
		return null;
	}

}
