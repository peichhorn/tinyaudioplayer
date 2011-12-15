package de.fips.plugin.tinyaudioplayer.http;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.junit.Test;

public class URIBuilderTest {
	@Test
	public void testWithParameters() throws Exception {
		// setup
		final URIBuilder builder = URIBuilder.uri("http://doma.in/service?param1=42");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("param2", "foo");
		params.put("param3", "bar");
		// run + assert
		assertThat(builder.withParameters(params).build()).isEqualTo(new URI("http://doma.in/service?param1=42&param2=foo&param3=bar"));
	}

	@Test
	public void testWithBeanParameters() throws Exception {
		// setup
		final URIBuilder builder = URIBuilder.uri("http://doma.in/service?param1=42");
		final DemoBean bean = new DemoBean(21, "23");
		// run + assert
		assertThat(builder.withBeanParameters(bean).toString()).isEqualTo("http://doma.in/service?param1=42&foo=21&bar=23");
	}

	@Test
	public void testWithParameter() {
		// setup
		final URIBuilder builder = URIBuilder.uri("http://doma.in/service?param1=42");
		// run + assert
		assertThat(builder.withParameter("param2", "foo").toString()).isEqualTo("http://doma.in/service?param1=42&param2=foo");
	}

	@Test
	public void testWithoutParameter() {
		// setup
		final URIBuilder builder = URIBuilder.uri("http://doma.in/service?param1=42");
		// run + assert
		assertThat(builder.withoutParameter("param1").toString()).isEqualTo("http://doma.in/service");
	}

	@Test
	public void testGetParameter() {
		// setup
		final URIBuilder builder = URIBuilder.uri("http://doma.in/service?param1=42");
		// run + assert
		assertThat(builder.getParameter("param1")).isEqualTo("42");
	}
	
	@RequiredArgsConstructor
	@Getter
	private static class DemoBean {
		private final int foo;
		private final String bar;
	}
}
