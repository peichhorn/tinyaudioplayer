package de.fips.plugin.tinyaudioplayer;

import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;

import lombok.SneakyThrows;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.fips.plugin.tinyaudioplayer.VolumeControl.IImageLocator;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotApplication;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotVolumeControl;

public class VolumeControlGUITest {
	@Rule
	public final SWTBotApplication application = new SWTBotApplication(getClass().getSimpleName()) {
		@Override
		protected void configureShell(final Shell shell) {
			final IImageLocator locator = new IImageLocator() {
				@SneakyThrows
				@Override
				public Image getImage(final String imagePath) {
					return ImageDescriptor.createFromURL(new File(imagePath).toURI().toURL()).createImage();
				}
			};
			new VolumeControl(locator).createControl(shell);
		}
	};

	@Test
	public void test() throws Exception {
		// setup
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
