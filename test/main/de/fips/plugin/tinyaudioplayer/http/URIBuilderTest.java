package de.fips.plugin.tinyaudioplayer.http;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import org.junit.Test;

public class URIBuilderTest {
	@Test
	public void testAddParameters() throws Exception {
		// setup
		final URI uri = new URI("http://doma.in/service?param1=42");
		final URI expectedUri = new URI("http://doma.in/service?param1=42&param2=foo&param3=bar");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("param2", "foo");
		params.put("param3", "bar");
		
		// run + assert
		assertEquals(expectedUri, new URIBuilder().setURI(uri).addParameters(params).toURI());
	}

	@Test
	public void testAddBeanParameters() throws Exception {
		// setup
		@Data class DemoBean {
			private int foo = 21;
			private String bar = "23";
		}
		final URI uri = new URI("http://doma.in/service?param1=42");
		final URI expectedUri = new URI("http://doma.in/service?param1=42&foo=21&bar=23");
		final DemoBean bean = new DemoBean();
		
		// run + assert
		assertEquals(expectedUri, new URIBuilder().setURI(uri).addBeanParameters(bean).toURI());
	}

	@Test
	public void testAddParameter() {
		// setup
		final String uri = "http://doma.in/service?param1=42";
		final String expectedUri = "http://doma.in/service?param1=42&param2=foo";
		// run + assert
		assertEquals(expectedUri, new URIBuilder().setURI(uri).addParameter("param2", "foo").toString());
	}

	@Test
	public void testRemoveParameter() {
		// setup
		final String uri = "http://doma.in/service?param1=42";
		final String expectedUri = "http://doma.in/service";
		// run + assert
		assertEquals(expectedUri, new URIBuilder().setURI(uri).removeParameter("param1").toString());
	}

	@Test
	public void testGetParameter() {
		// setup
		final String uri = "http://doma.in/service?param1=42";
		// run + assert
		assertEquals("42", new URIBuilder().setURI(uri).getParameter("param1"));
	}
}
