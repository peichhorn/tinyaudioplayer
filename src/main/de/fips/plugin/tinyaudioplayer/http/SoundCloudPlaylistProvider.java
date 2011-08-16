/*
 * Copyright © 2011 Philipp Eichhorn.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.fips.plugin.tinyaudioplayer.http;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Cleanup;
import lombok.FluentSetter;
import lombok.Rethrow;
import lombok.Sanitize.Normalize;
import lombok.Validate;
import lombok.Validate.NotEmpty;
import lombok.Validate.NotNull;
import lombok.VisibleForTesting;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

/**
 * Let's assume that this is the json-api of the soundcloud player:
 * <pre>
 * {
 *   "id" : 7566583,
 *   "uid" : "EJRVFtCSyo5d",
 *   "user" : {
 *     "username" : "Fl\u01ddx",
 *     "permalink" : "flexlive"
 *   },
 *   "uri" : "/flexlive/chromeo-night-by-night-flex-remix",
 *   "duration" : 286932,
 *   "token" : "61VM1",
 *   "name" : "chromeo-night-by-night-flex-remix",
 *   "title" : "Chromeo - Night by Night (Flex Remix)",
 *   "commentable" : true,
 *   "revealComments" : true,
 *   "commentUri" : "/flexlive/chromeo-night-by-night-flex-remix/comments/",
 *   "streamUrl" : "http://media.soundcloud.com/stream/EJRVFtCSyo5d?stream_token=61VM1",
 *   "waveformUrl" : "http://w1.sndcdn.com/EJRVFtCSyo5d_m.png", 
 *   "propertiesUri" : "/flexlive/chromeo-night-by-night-flex-remix/properties/",
 *   "statusUri" : "/transcodings/EJRVFtCSyo5d",
 *   "replacingUid" : null,
 *   "preprocessingReady" : true,
 *   "renderingFailed" : false,
 *   "isPublic" : true,
 *   "commentableByUser" : true,
 *   "makeHeardUri" : false,
 *   "favorite" : false,
 *   "followingTrackOwner" : false,
 *   "conversations" : {
 *     "49578" : [7296086,7297937],
 *     "57015" : [7296047,7297910],
 *     "153181" : [7344121,7366606]
 *   }
 * }
 * </pre>
*/
public class SoundCloudPlaylistProvider {
	private static final String BUFFER_TRACKS_JSON_REGEXP = "<script type=\"text/javascript\">\\s*window.SC.bufferTracks.push\\((.*)\\);\\s*</script>";
	private static final String NUMBER_OF_PAGES_REGEXP = "/tracks/search\\?page=(\\d*)\\&";
	private static final String SOUNDCLOUD_SEARCH_QUERY = "http://soundcloud.com/search?page={0}&q%5Bfulltext%5D={1}";
	private static final int BUFFER_SIZE = 65536;
	
	private final Map<URI, String> pageCache = new Cache<URI, String>(30);
	
	@FluentSetter
	private IProxyConfiguration proxyConfiguration;

	public Playlist getPlaylistFor(final String searchText) {
		return getPlaylistFor(searchText, 1);
	}

	@Validate
	public Playlist getPlaylistFor(@NotEmpty final String searchText, final int pageNumber) {
		if ((pageNumber < 1) || (pageNumber > getNumberOfPagesFor(searchText))) {
			throw new IllegalArgumentException();
		}
		final Playlist playlist = new Playlist();
		final URI queryURI = getSearchQueryURIFor(searchText, pageNumber);
		final String soundCloudPage = getSoundCloudPageFor(queryURI);
		final List<String> bufferTracksAsJSON = getBufferTracksAsJSON(soundCloudPage);
		playlist.add(asPlaylist(bufferTracksAsJSON));
		return playlist;
	}

	@Validate
	public int getNumberOfPagesFor(@NotEmpty final String searchText) {
		final URI queryURI = getSearchQueryURIFor(searchText, 1);
		final String soundCloudPage = getSoundCloudPageFor(queryURI);
		final Pattern pattern = Pattern.compile(NUMBER_OF_PAGES_REGEXP);
		final Matcher matcher = pattern.matcher(soundCloudPage);
		int numberOfPagesFor = 1;
		while(matcher.find()) {
			numberOfPagesFor = Math.max(numberOfPagesFor, Integer.valueOf(matcher.group(1)));
		}
		return numberOfPagesFor;
	}
	
	@Rethrow(value=URISyntaxException.class, as=IllegalArgumentException.class)
	@VisibleForTesting URI getSearchQueryURIFor(@NotNull @Normalize(Form.NFKC) final String searchText, final int pageNumber) {
		if (!Pattern.matches("[a-zA-Z0-9 ]+", searchText)) {
			throw new IllegalArgumentException();
		} 
		return new URI(MessageFormat.format(SOUNDCLOUD_SEARCH_QUERY, pageNumber, toSearchString(searchText)));
	}

	@VisibleForTesting String getSoundCloudPageFor(final URI queryURI) {
		final String cachedPage = pageCache.get(queryURI);
		if (cachedPage != null) {
			return cachedPage;
		}
		final HttpClient client = new HttpClient();
		try {
			if (proxyConfiguration != null) proxyConfiguration.setupProxyFor(client.getHostConfiguration(), queryURI);
			
			@Cleanup("releaseConnection") HttpMethod method = new GetMethod(queryURI.toString());
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				@Cleanup InputStream response = method.getResponseBodyAsStream();
				final String page = readStreamAsString(response);
				pageCache.put(queryURI, page);
				return page;
			}
		} catch (IOException ignore) {
			// to bad then
		}
		return "";
	}

	@VisibleForTesting List<String> getBufferTracksAsJSON(final String soundCloudPage) {
		final List<String> bufferTracksAsJSON = new ArrayList<String>();
		final Pattern pattern = Pattern.compile(BUFFER_TRACKS_JSON_REGEXP);
		final Matcher matcher = pattern.matcher(soundCloudPage);
		while(matcher.find()) {
			bufferTracksAsJSON.add(matcher.group(1));
		}
		return bufferTracksAsJSON;
	}

	@VisibleForTesting Playlist asPlaylist(List<String> bufferTracksAsJSON) {
		final Playlist playlist = new Playlist();
		final Set<URI> uniqueURIs = new HashSet<URI>();
		for (String bufferTrackAsJSON : bufferTracksAsJSON) {
			try {
				final JSONParser parser = new JSONParser();
				JSONObject object = (JSONObject) parser.parse(bufferTrackAsJSON);
				final String title = unescapeHtml((String) object.get("title"));
				final URI location = URIBuilder.uri((String) object.get("streamUrl")).withoutParameter("stream_token").build();
				final long seconds = TimeUnit.MILLISECONDS.toSeconds((Long) object.get("duration"));
				if (uniqueURIs.add(location)) {
					playlist.add(new PlaylistItem(title, location, seconds));
				}
			} catch (ParseException e) {
				// to bad then
			} catch (URISyntaxException e) {
				// to bad then;
			}
		}
		return playlist;
	}

	@VisibleForTesting String toSearchString(final String searchText) {
		return searchText.replace(" ", "+");
	}

	@VisibleForTesting String readStreamAsString(final InputStream is) throws IOException {
		final char[] buffer = new char[BUFFER_SIZE];
		final StringBuilder out = new StringBuilder();
		final Reader in = new BufferedReader(new InputStreamReader(is));
		for (int read = in.read(buffer); read >= 0; read = in.read(buffer)) {
			out.append(buffer, 0, read);
		}
		return out.toString();
	}
	
	@VisibleForTesting static class Cache<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = -1586143843840611967L;

		private final int maxEntries;

		public Cache(final int maxEntries) {
			super(maxEntries + 1, .75F, true);
			this.maxEntries = maxEntries;
		}

		public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxEntries;
		}
	}
}
