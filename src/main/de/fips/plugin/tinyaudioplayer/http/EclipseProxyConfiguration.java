package de.fips.plugin.tinyaudioplayer.http;

import java.net.URI;

import lombok.Cleanup;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import org.apache.commons.httpclient.HostConfiguration;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;

public final class EclipseProxyConfiguration implements IProxyConfiguration {

	@Override
	public void setupProxyFor(final HostConfiguration hostConfig, final URI uri) {
		@Cleanup final ServiceTracker proxyTracker = new ServiceTracker(FrameworkUtil.getBundle(this.getClass()).getBundleContext(), IProxyService.class.getName(), null);
		proxyTracker.open();
		IProxyService proxyService = (IProxyService) proxyTracker.getService();
		IProxyData[] proxyDataForHost = proxyService.select(uri);
		for (IProxyData data : proxyDataForHost) {
			if (data.getHost() == null) continue;
			hostConfig.setProxy(data.getHost(), data.getPort());
			break;
		}
	}
}
