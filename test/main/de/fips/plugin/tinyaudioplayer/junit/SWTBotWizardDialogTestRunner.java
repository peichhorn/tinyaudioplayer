package de.fips.plugin.tinyaudioplayer.junit;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.List;

import lombok.Rethrow;
import lombok.SneakyThrows;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class SWTBotWizardDialogTestRunner extends SWTBotJunit4ClassRunner {
	private IWizard newWizard;
	private String wizardFieldName;

	public SWTBotWizardDialogTestRunner(Class<?> clazz) throws Exception {
		super(clazz);
	}

	@SneakyThrows
	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
		final Shell parentShell = new Shell();
		prepareWizard();
		final boolean[] finishPressed = new boolean[1];
		final WizardDialog dialog = new WizardDialog(parentShell, newWizard) {
			@Override
			protected void finishPressed() {
				super.finishPressed();
				finishPressed[0] = true;
			}
		};
		final Exception[] ex = new Exception[1];
		final Runnable r = new Runnable() {
			public void run() {
				final String oldKeyboardLayout = SWTBotPreferences.KEYBOARD_LAYOUT;
				final long oldPlaybackDelay = SWTBotPreferences.PLAYBACK_DELAY;
				try {
					SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
					SWTBotPreferences.PLAYBACK_DELAY = 0;
					SWTBotWizardDialogTestRunner.super.runChild(method, notifier);
				} catch (Exception e) {
					ex[0] = e;
				} finally {
					SWTBotPreferences.KEYBOARD_LAYOUT = oldKeyboardLayout;
					SWTBotPreferences.PLAYBACK_DELAY = oldPlaybackDelay;
					if (dialog.getShell() != null && dialog.getShell().isDisposed() == false) {
						UIThreadRunnable.syncExec(new VoidResult() {
							public void run() {
								dialog.close();
							}
						});
					}
				}
			}
		};
		final Thread nonUIThread = new Thread(r);
		nonUIThread.setName("Runnning Test Thread");
		nonUIThread.start();
		dialog.open();

		waitForNonUIThreadFinished(parentShell, nonUIThread);
		if (ex[0] != null) {
			throw ex[0];
		}
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		if (newWizard != null) {
			try {
				test.getClass().getField(wizardFieldName).set(test, newWizard);
			} catch (NoSuchFieldException e) {
			}
		}
		return test;
	}

	private void waitForNonUIThreadFinished(final Shell parentShell, final Thread nonUIThread) {
		final Display display = parentShell.getDisplay();
		while (nonUIThread.isAlive()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Rethrow(as=IllegalStateException.class)
	private void prepareWizard() {
		wizardFieldName = null;
		newWizard = null;
		TestClass testClass = getTestClass();
		List<FrameworkField> wizards = testClass.getAnnotatedFields(InitWizard.class);
		if (wizards.size() != 1) {
			throw new IllegalStateException();
		}
		Field wizard = wizards.get(0).getField();
		Class<?> wizardType = wizard.getType();
		if (!IWizard.class.isAssignableFrom(wizardType)) {
			throw new IllegalStateException();
		}
		wizardFieldName = wizard.getName();
		newWizard = (IWizard) wizardType.newInstance();
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface InitWizard {
	}
}