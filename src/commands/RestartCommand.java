package commands;

import java.io.IOException;

import command.AbstractCommand;
import model.ModelClient;

public class RestartCommand extends AbstractCommand {

	public RestartCommand(ModelClient _model) {
		super(_model);
	}

	@Override
	public String execute(String args) { //TODO comando corretto?
		String CMD="shutdown /r";
		try {
			Runtime.getRuntime().exec(CMD);
		} catch (IOException e) {
			model.getLogger().error(e);
		}
		return null;
	}

}
