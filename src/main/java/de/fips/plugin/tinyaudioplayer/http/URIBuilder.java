/*
 * Copyright � 2011-2012 Philipp Eichhorn.
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

import java.beans.Introspector;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import lombok.*;

/**
 * <p>
 * Given a URI and a set of parameters, provide abilities to add and remove parameters (either directly, from a Map, or
 * from a Bean), and finally reconstruct the proper URI. For example:
 * </p>
 * <p>
 * <pre>
 * URI uri = URIBuilder.uri(strUri))
 *                     .withParameters(request.getParameterMap())
 *                     .withourParameter("id")
 *                     .withParameter("session", strSession)
 *                     .build();
 * </pre>
 * </p>
 * 
 * Note that this class currently does not support URI rewriting of cookie strings (though it should).
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class URIBuilder {
	private final static List<String> RESTRICTED = Arrays.asList(new String[] { "j_username", "j_password", "username", "password" });

	private final String urlWithoutParameters;
	private final String encoding;
	private final Map<String, String> parameters = new HashMap<String, String>();

	public static URIBuilder uri(final URI uri) {
		return uri(uri.toString());
	}

	public static URIBuilder uri(final String uri) {
		return uri(uri, "UTF-8");
	}

	public static URIBuilder uri(final String uri, final String encoding) {
		if (uri == null || uri.isEmpty()) {
			return new URIBuilder("", encoding);
		} else {
			val tokenizerQueryString = new StringTokenizer(uri, "?");
			val builder = new URIBuilder(tokenizerQueryString.nextToken(), encoding);
			if (tokenizerQueryString.hasMoreTokens()) {
				val queryString = tokenizerQueryString.nextToken();
				if (queryString != null) {
					val tokenizerNameValuePair = new StringTokenizer(queryString, "&");
					while (tokenizerNameValuePair.hasMoreTokens()) {
						val nameValuePair = tokenizerNameValuePair.nextToken();
						val tokenizerValue = new StringTokenizer(nameValuePair, "=");
						val name = tokenizerValue.nextToken();
						val value = tokenizerValue.nextToken();
						builder.withParameter(name, value);
					}
				}
			}
			return builder;
		}
	}

	/**
	 * Add parameters from a map
	 */
	public URIBuilder withParameters(Map<String, String> params) {
		if (params != null) for (Map.Entry<String, String> param : params.entrySet()) {
			withParameter(param.getKey(), param.getValue());
		}
		return this;
	}

	/**
	 * Add parameters defined by a bean
	 */
	@Rethrow(as = IllegalArgumentException.class, message = "Unable to parse bean.")
	public URIBuilder withBeanParameters(Object obj) {
		val beanClass = obj.getClass();
		val beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
		val descriptors = beanInfo.getPropertyDescriptors();
		for (val descriptor : descriptors) {
			if (descriptor.isHidden()) continue;
			val name = descriptor.getName();
			val value = descriptor.getReadMethod().invoke(obj);
			withParameter(name, value.toString());
		}
		return this;
	}

	/**
	 * Add a single parameter
	 */
	public URIBuilder withParameter(String name, String value) {
		if ((name != null) && (value != null)) {
			try {
				parameters.put(name, URLEncoder.encode(value, encoding));
			} catch (UnsupportedEncodingException e) {
				// to bad then
			}
		}
		return this;
	}

	public URIBuilder withoutParameter(String strName) {
		parameters.remove(strName);
		return this;
	}

	public String getParameter(String strName) {
		return parameters.get(strName);
	}

	public URI build() throws URISyntaxException {
		return new URI(toString());
	}

	public String toString() {
		val queryString = new StringBuilder();
		queryString.append(urlWithoutParameters);
		boolean firstTime = true;
		for (val parameter : parameters.entrySet()) {
			if (RESTRICTED.contains(parameter.getKey())) continue;
			if (parameter.getKey().isEmpty()) continue;
			if (firstTime) {
				firstTime = false;
				queryString.append("?");
			} else {
				queryString.append("&");
			}
			queryString.append(parameter.getKey());
			queryString.append("=");
			queryString.append(parameter.getValue());
		}
		return queryString.toString();
	}
}
