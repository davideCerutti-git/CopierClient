package commands;

import java.util.HashMap;

public class CommandRegister {
	private final HashMap<String, Command> commandMap = new HashMap<>();
	
	public void register(String commandName, Command command) {
        commandMap.put(commandName, command);
    }

	public Command getCommandByName(String reqName) {
		return commandMap.containsKey(reqName) ? commandMap.get(reqName) : null;
	}
	

}
