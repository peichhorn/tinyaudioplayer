package de.fips.plugin.tinyaudioplayer.wizards.soundcloud;

import static de.fips.plugin.tinyaudioplayer.junit.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.fips.plugin.tinyaudioplayer.PlaylistIOHandler;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.http.SoundCloudPlaylistProvider;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotWizardDialogTestRunner;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotWizardDialogTestRunner.InitWizard;

@RunWith(SWTBotWizardDialogTestRunner.class)
public class SoundCloudWizardTest {
	@InitWizard
	public SoundCloudWizard wizard;

	@Test
	public void onlyCancelButtonIsEnabledOnSearchPage() throws Exception {
		// setup
		final SWTBot bot = new SWTBot();
		bot.shell("Import from Soundcloud");
		// run + assert
		assertThat(bot.button(IDialogConstants.BACK_LABEL).isEnabled()).isFalse();
		assertThat(bot.button(IDialogConstants.NEXT_LABEL).isEnabled()).isFalse();
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled()).isFalse();
		assertThat(bot.button(IDialogConstants.CANCEL_LABEL).isEnabled()).isTrue();
	}

	@Test
	public void closesOnCancel() throws Exception {
		// setup
		final SWTBot bot = new SWTBot();
		final SWTBotShell target = bot.shell("Import from Soundcloud");
		// run
		bot.button(IDialogConstants.CANCEL_LABEL).click();
		// assert
		assertThat(Conditions.shellCloses(target).test()).isTrue();
	}

	@Test
	public void typingSearchStringEnablesNextButton() throws Exception {
		// setup
		final SWTBot bot = new SWTBot();
		bot.shell("Import from Soundcloud");
		// run
		bot.textWithId("searchText").typeText("Chromeo");
		// assert
		assertThat(bot.button(IDialogConstants.BACK_LABEL).isEnabled()).isFalse();
		assertThat(bot.button(IDialogConstants.NEXT_LABEL).isEnabled()).isTrue();
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled()).isFalse();
		assertThat(bot.button(IDialogConstants.CANCEL_LABEL).isEnabled()).isTrue();
	}

	@Test
	public void onlyNextButtonIsDisabledOnFilterResultsPage() throws Exception {
		// setup
		final SoundCloudPlaylistProvider playlistProvider = mock(SoundCloudPlaylistProvider.class);
		final Playlist playlist = new Playlist();
		playlist.add(new PlaylistItem("Night By Night \"&\"", new URI("http://media.soundcloud.com/stream/LHBNmmjRYBmq"), 225));
		playlist.add(new PlaylistItem("Hot Mess featuring Elly Jacson (Duck Sauce Remix)", new URI("http://media.soundcloud.com/stream/phq1RrkR47eE"), 327));
		doReturn(1).when(playlistProvider).getNumberOfPagesFor(anyString());
		doReturn(playlist).when(playlistProvider).getPlaylistFor(anyString(), anyInt());
		wizard.setPlaylistProvider(playlistProvider);
		final SWTBot bot = new SWTBot();
		bot.shell("Import from Soundcloud");
		// run
		bot.textWithId("searchText").typeText("Chromeo");
		bot.button(IDialogConstants.NEXT_LABEL).click();
		// assert
		assertThat(bot.button(IDialogConstants.BACK_LABEL).isEnabled()).isTrue();
		assertThat(bot.button(IDialogConstants.NEXT_LABEL).isEnabled()).isFalse();
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled()).isTrue();
		assertThat(bot.button(IDialogConstants.CANCEL_LABEL).isEnabled()).isTrue();
	}

	@Test
	public void onlyAddsSelectedTracksToPlaylist() throws Exception {
		// setup
		final PlaylistAudioPlayer internalPlayer = new PlaylistAudioPlayer();
		final Playlist playlist = internalPlayer.getPlaylist();
		final PlaylistIOHandler playlistIOHandler = mock(PlaylistIOHandler.class);
		final TinyAudioPlayer player = new TinyAudioPlayer(internalPlayer, playlistIOHandler);
		final BundleContext context = mock(BundleContext.class);
		doReturn(mock(Bundle.class)).when(context).getBundle();
		new TinyAudioPlayerPlugin(player).start(context);
		final SoundCloudPlaylistProvider playlistProvider = mock(SoundCloudPlaylistProvider.class);
		final Playlist soundCloudPlaylist = new Playlist();
		soundCloudPlaylist.add(new PlaylistItem("Night By Night \"&\"", new URI("http://media.soundcloud.com/stream/LHBNmmjRYBmq"), 225));
		soundCloudPlaylist.add(new PlaylistItem("Hot Mess featuring Elly Jacson (Duck Sauce Remix)", new URI("http://media.soundcloud.com/stream/phq1RrkR47eE"), 327));
		doReturn(1).when(playlistProvider).getNumberOfPagesFor(anyString());
		doReturn(soundCloudPlaylist).when(playlistProvider).getPlaylistFor(anyString(), anyInt());
		wizard.setPlaylistProvider(playlistProvider);
		final SWTBot bot = new SWTBot();
		bot.shell("Import from Soundcloud");
		// run
		bot.textWithId("searchText").typeText("Chromeo");
		bot.button(IDialogConstants.NEXT_LABEL).click();
		bot.table(0).getTableItem(1).check();
		bot.button(IDialogConstants.FINISH_LABEL).click();
		// assert
		assertThat(playlist).hasSize(1);
		assertThat(playlist.getNextTrack()).hasName("Hot Mess featuring Elly Jacson (Duck Sauce Remix)") //
			.hasLocation(new URI("http://media.soundcloud.com/stream/phq1RrkR47eE")) //
			.hasLength(327);
	}
}
