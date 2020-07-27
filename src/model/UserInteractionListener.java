package model;

import java.util.Date;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

public class UserInteractionListener extends Thread {

	//Fields:
	public static boolean standby = false;
	
	//Utils:
	private Logger logger;
	private static double lastDate = (new Date()).getTime();
	private static final double presetDate = 5;
	private static boolean running = true;
	
	//Refs:
	private ModelClient modelClient;

	public UserInteractionListener(Logger _logger, ModelClient _modelClient) {
		logger = _logger;
		modelClient=_modelClient;
	}

	@Override
	public void run() {
		// Keyboard
		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // Use false here to switch to hook instead of
		// raw input
		logger.info("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
		for (Entry<Long, String> keyboard : GlobalKeyboardHook.listKeyboards().entrySet()) {
			logger.info(keyboard.getKey() + ": " + keyboard.getValue());
		}
		// Mouse
		GlobalMouseHook mouseHook = new GlobalMouseHook(); // Add true to the constructor, to switch to raw input mode
		logger.info("Global mouse hook successfully started, press [middle] mouse button to shutdown. Connected mice:");
		for (Entry<Long, String> mouse : GlobalMouseHook.listMice().entrySet()) {
			logger.info(mouse.getKey() + ": " + mouse.getValue());
		}
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override
			public void keyPressed(GlobalKeyEvent event) {
				if (standby) {
					//not in standby...
					modelClient.getCommandsQueue().add("not in standby: null");
				}
				standby = false;
				lastDate = (new Date()).getTime();
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
					running = false;
				}
			}
			@Override
			public void keyReleased(GlobalKeyEvent event) {
				if (standby) {
					//not in standby...
					modelClient.getCommandsQueue().add("not in standby: null");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
		});

		mouseHook.addMouseListener(new GlobalMouseAdapter() {
			@Override
			public void mousePressed(GlobalMouseEvent event) {
				if ((event.getButtons() & GlobalMouseEvent.BUTTON_LEFT) != GlobalMouseEvent.BUTTON_NO
						&& (event.getButtons() & GlobalMouseEvent.BUTTON_RIGHT) != GlobalMouseEvent.BUTTON_NO) {
					logger.info("Both mouse buttons are currently pressed!");
				}
				if (standby) {
					//not in standby...
					modelClient.getCommandsQueue().add("not in standby: null");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
			@Override
			public void mouseReleased(GlobalMouseEvent event) {
				if (standby) {
					//not in standby...
					modelClient.getCommandsQueue().add("not in standby: null");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
			@Override
			public void mouseMoved(GlobalMouseEvent event) {
				if (standby) {
					//not in standby...
					modelClient.getCommandsQueue().add("not in standby: null");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
			@Override
			public void mouseWheel(GlobalMouseEvent event) {
				if (standby) {
					//not in standby...
					modelClient.getCommandsQueue().add("not in standby: null");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
		});

		try {
			while (running) {
				Thread.sleep(128);
				if ((new Date()).getTime() - lastDate > (presetDate * 1000)) {
					if (!standby) {
						//in standby...
						modelClient.getCommandsQueue().add("in standby: null");
						standby = true;
					}
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
		} finally {
			keyboardHook.shutdownHook();
			mouseHook.shutdownHook();
		}
	}
}
