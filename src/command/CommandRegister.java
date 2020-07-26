package command;

import java.util.HashMap;

public class CommandRegister {
	private final HashMap<String, AbstractCommand>  commandMap = new HashMap<String, AbstractCommand> ();
	
	public void register(String commandName, AbstractCommand command) {
        commandMap.put(commandName, command);
    }

	public AbstractCommand getCommandByName(String reqName) {
		return commandMap.containsKey(reqName) ? commandMap.get(reqName) : null;
	}
	

}
