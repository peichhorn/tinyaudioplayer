package de.fips.plugin.tinyaudioplayer.junit;

import lombok.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

@RequiredArgsConstructor
public class SWTBotApplication implements MethodRule {
	private final String title;

	public final Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				final long oldDelay = SWTBotPreferences.PLAYBACK_DELAY;
				SWTBotPreferences.PLAYBACK_DELAY = 10;
				UIThread uiThread = null;
				try {
					uiThread = new UIThread();
					uiThread.start();
					base.evaluate();
				} finally {
					SWTBotPreferences.PLAYBACK_DELAY = oldDelay;
				}
			}
		};
	}

	protected void configureShell(final Shell shell) {
		
	}

	private class UIThread extends Thread {
		@Getter
		private Shell shell;
		private Display display;

		public void run() {
			display = new Display();
			shell = new Shell(display, SWT.ON_TOP | SWT.SHELL_TRIM);
			shell.setText(title);
			configureShell(shell);
			shell.pack();
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) display.sleep();
			}
			display.dispose();
		}
	}
}