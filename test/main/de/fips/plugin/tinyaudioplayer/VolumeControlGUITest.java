package de.fips.plugin.tinyaudioplayer;

import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;

import lombok.SneakyThrows;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.fips.plugin.tinyaudioplayer.VolumeControl.IImageLocator;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotApplicationTestRunner;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotVolumeControl;

@RunWith(SWTBotApplicationTestRunner.class)
public class VolumeControlGUITest {

	@Test
	public void changesVolumeOfPlayer() throws Exception {
		// setup
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override
			public void run() {
				final IImageLocator locator = new IImageLocator() {
					@SneakyThrows
					@Override
					public Image getImage(final String imagePath) {
						return ImageDescriptor.createFromURL(new File(imagePath).toURI().toURL()).createImage();
					}
				};
				new VolumeControl(locator).createControl(SWTUtils.display().getActiveShell());
			}
		});
		final SWTBot bot = new SWTBot();
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final BundleContext context = mock(BundleContext.class);
		doReturn(mock(Bundle.class)).when(context).getBundle();
		new TinyAudioPlayerPlugin(player).start(context);
		InOrder inOrder = inOrder(player);
		// run
		SWTBotVolumeControl.findWith(bot).click(0).click(40).click(10).click(63).click(31);
		// assert
		inOrder.verify(player).setVolume(eq(0.0f, 0.01f));
		inOrder.verify(player).setVolume(eq(1.25f, 0.01f));
		inOrder.verify(player).setVolume(eq(0.31f, 0.01f));
		inOrder.verify(player).setVolume(eq(1.96f, 0.01f));
		inOrder.verify(player).setVolume(eq(0.96f, 0.01f));
	}
}
