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

	private static boolean run1 = true;
	public static boolean standby = false;
	private static double lastDate = (new Date()).getTime();
	private static final double presetDate = 600;
	Logger log;
	private ClientThread client;

	public UserInteractionListener(Logger _log, ClientThread clientThread) {
		log = _log;
		client=clientThread;
	}

	@Override
	public void run() {
		// Keyboard
		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // Use false here to switch to hook instead of
		// raw input
		log.info("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
		for (Entry<Long, String> keyboard : GlobalKeyboardHook.listKeyboards().entrySet()) {
			log.info(keyboard.getKey() + ": " + keyboard.getValue());
		}
		// Mouse
		GlobalMouseHook mouseHook = new GlobalMouseHook(); // Add true to the constructor, to switch to raw input mode
		log.info("Global mouse hook successfully started, press [middle] mouse button to shutdown. Connected mice:");
		for (Entry<Long, String> mouse : GlobalMouseHook.listMice().entrySet()) {
			log.info(mouse.getKey() + ": " + mouse.getValue());
		}
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override
			public void keyPressed(GlobalKeyEvent event) {
				if (standby) {
					log.info("Not in standby...");
					client.getCommandsQueue().add("not in standby");
				}
				standby = false;
				lastDate = (new Date()).getTime();
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
					run1 = false;
				}
			}
			@Override
			public void keyReleased(GlobalKeyEvent event) {
				if (standby) {
					log.info("Not in standby...");
					client.getCommandsQueue().add("not in standby");
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
					log.info("Both mouse buttons are currently pressed!");
				}
				if (standby) {
					log.info("Not in standby...");
					client.getCommandsQueue().add("not in standby");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
			@Override
			public void mouseReleased(GlobalMouseEvent event) {
				if (standby) {
					log.info("Not in standby...");
					client.getCommandsQueue().add("not in standby");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
			@Override
			public void mouseMoved(GlobalMouseEvent event) {
				if (standby) {
					log.info("Not in standby...");
					client.getCommandsQueue().add("not in standby");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
			@Override
			public void mouseWheel(GlobalMouseEvent event) {
				if (standby) {
					log.info("Not in standby...");
					client.getCommandsQueue().add("not in standby");
				}
				standby = false;
				lastDate = (new Date()).getTime();
			}
		});

		try {
			while (run1) {
				Thread.sleep(128);
				if ((new Date()).getTime() - lastDate > (presetDate * 1000)) {
					if (!standby) {
						log.info("in standby...");
						client.getCommandsQueue().add("in standby");
						standby = true;
					}
				}
			}
		} catch (InterruptedException e) {
			log.error(e);
		} finally {
			keyboardHook.shutdownHook();
			mouseHook.shutdownHook();
		}
	}
}
