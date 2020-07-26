package command;

import model.ModelClient;

public abstract class AbstractCommand implements CommandInterface{

	protected ModelClient model;

	protected AbstractCommand(ModelClient _model) {
		this.model = _model;
	}

}
