package de.fips.plugin.tinyaudioplayer.junit;

import lombok.SneakyThrows;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;

public class SWTBotApplicationTestRunner extends SWTBotJunit4ClassRunner {

	public SWTBotApplicationTestRunner(Class<?> clazz) throws Exception {
		super(clazz);
	}

	@SneakyThrows
	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
		final Shell parentShell = new Shell();
		final Exception[] ex = new Exception[1];
		final Runnable r = new Runnable() {
			public void run() {
				final String oldKeyboardLayout = SWTBotPreferences.KEYBOARD_LAYOUT;
				final long oldPlaybackDelay = SWTBotPreferences.PLAYBACK_DELAY;
				try {
					SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
					SWTBotPreferences.PLAYBACK_DELAY = 0;
					SWTBotApplicationTestRunner.super.runChild(method, notifier);
				} catch (Exception e) {
					ex[0] = e;
				} finally {
					SWTBotPreferences.KEYBOARD_LAYOUT = oldKeyboardLayout;
					SWTBotPreferences.PLAYBACK_DELAY = oldPlaybackDelay;
				}
			}
		};
		final Thread nonUIThread = new Thread(r);
		nonUIThread.setName("Runnning Test Thread");
		nonUIThread.start();
		parentShell.pack();
		parentShell.open();
		waitForNonUIThreadFinished(parentShell, nonUIThread);
		if (ex[0] != null) {
			throw ex[0];
		}
	}

	private void waitForNonUIThreadFinished(final Shell parentShell, final Thread nonUIThread) {
		final Display display = parentShell.getDisplay();
		while (nonUIThread.isAlive()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}