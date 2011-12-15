package de.fips.plugin.tinyaudioplayer.junit;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withId;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;

@SWTBotWidget(clasz = Control.class, preferredName = "volumeControl")//$NON-NLS-1$
public class SWTBotVolumeControl extends AbstractSWTBotControl<Control> {

	private SWTBotVolumeControl(final Control w) throws WidgetNotFoundException {
		super(w);
	}

	public SWTBotVolumeControl click(final int x) {
		clickXY(x, 1);
		syncExec(new VoidResult() {
			public void run() {
				widget.update();
			}
		});
		return this;
	}

	public static SWTBotVolumeControl findWith(final SWTBot bot) {
		return new SWTBotVolumeControl((Control) bot.widget(withId("volumeControl")));
	}
}