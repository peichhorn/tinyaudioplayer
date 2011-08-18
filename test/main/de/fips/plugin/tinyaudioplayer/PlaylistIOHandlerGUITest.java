package de.fips.plugin.tinyaudioplayer;

import static de.fips.plugin.tinyaudioplayer.junit.Assertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.junit.SWTBotApplication;

public class PlaylistIOHandlerGUITest {
	@Rule
	public final SWTBotApplication application = new SWTBotApplication(getClass().getSimpleName());
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void loadNewPlaylist() throws Exception {
		// setup
		FileChooser.IS_IN_TEST_MODE = true;
		final SWTBot bot = new SWTBot();
		final PlaylistIOHandler handler = new PlaylistIOHandler(bot.getDisplay());
		final File testFile = file("io/playlist.m3u");
		final Playlist[] playlists = new Playlist[1];
		// run
		bot.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				playlists[0] = handler.loadNewPlaylist();
			}
		});
		final SWTBotShell shell = bot.shell("FileChooser");
		shell.bot().text(0).setText(testFile.getAbsolutePath());
		shell.bot().button("OK").click();
		shell.close();
		// assert
		final Playlist playlist = playlists[0];
		assertThat(playlist).hasSize(3);
		assertThat(playlist.getCurrentTrack()).hasName("Artist - Track 01") //
				.hasLocation(new File("01 - Track 01.mp3")) //
				.hasLength(220);
		assertThat(playlist.getNextTrack()).hasName("Author - Book - Chapter 03 - Title") //
				.hasLocation(new File("Chapter 03 - Title.mp3")) //
				.hasLength(1167);
		assertThat(playlist.getNextTrack()).hasName("Chapter 04 - Title") //
				.hasLocation(new File("Chapter 04 - Title.mp3")) //
				.hasLength(0);
	}

	@Test
	public void savePlaylist() throws Exception {
		// setup
		FileChooser.IS_IN_TEST_MODE = true;
		final SWTBot bot = new SWTBot();
		final PlaylistIOHandler handler = new PlaylistIOHandler(bot.getDisplay());
		final File testDir = tempFolder.newFolder(getClass().getSimpleName());
		final File testFile = new File(testDir, "playlist.m3u");
		final Playlist playlist = createPlaylist(testDir);
		// run
		bot.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				handler.savePlaylist(playlist);
			}
		});
		final SWTBotShell shell = bot.shell("FileChooser");
		shell.bot().text(0).setText(testFile.getAbsolutePath());
		shell.bot().button("OK").click();
		// assert
		assertThat(testFile).hasSameContentAs(file("io/exported_playlist.m3u"));
	}

	private File file(final String path) throws Exception {
		return new File(getClass().getResource(path).toURI());
	}

	private Playlist createPlaylist(final File testDir) {
		final File track1 = new File(testDir, "01 - Track 01.mp3");
		final File track2 = new File(testDir, "Chapter 03 - Title.mp3");
		final Playlist playlist = new Playlist();
		playlist.add(new PlaylistItem("Artist - Track 01", track1.toURI(), 220));
		playlist.add(new PlaylistItem("Author - Book - Chapter 03 - Title", track2.toURI(), 1167));
		return playlist;
	}
}
