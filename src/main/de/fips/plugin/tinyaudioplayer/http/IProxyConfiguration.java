package de.fips.plugin.tinyaudioplayer.http;

import java.net.URI;

import org.apache.commons.httpclient.HostConfiguration;

public interface IProxyConfiguration {
	public void setupProxyFor(HostConfiguration hostConfig, URI uri);
}
