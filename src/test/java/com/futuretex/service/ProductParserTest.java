package com.futuretex.service;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProductParserTest {

	@Test
	public void parsePrice() {
	}

	@Test
	public void parseTemperature() {
	}

	@Test
	public void parseExpired() {
	}

	@Test
	public void parseAgo() {
		String ago = "Found 1 s ago";
		long ts = ProductParser.parseAgo(ago);
		assertEquals((System.currentTimeMillis() - 1000)/1000, ts/1000);
		ago = "Found 1 m ago";
		ts = ProductParser.parseAgo(ago);
		assertEquals((System.currentTimeMillis() - (1000 * 60))/1000, ts/1000);
		ago = "Found 1 h, 0 m ago";
		ts = ProductParser.parseAgo(ago);
		assertEquals((System.currentTimeMillis() - (1000 * 60 * 60))/1000, ts/1000);
		ago = "Found 1 h, 1 m ago";
		ts = ProductParser.parseAgo(ago);
		assertEquals((System.currentTimeMillis() - (1000 * 60 * 60) - (1000 * 60))/1000, ts/1000);
	}

	@Test
	public void parseThisYearDate() {
		String theString ="12th Sep";
		long ts = ProductParser.parseThisYearDate(theString);
		assertTrue(new Date(ts).toString().contains("Sep 12"));
	}
}