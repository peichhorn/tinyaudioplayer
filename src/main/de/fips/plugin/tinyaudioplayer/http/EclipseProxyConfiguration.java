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
