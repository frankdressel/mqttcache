package de.moduliertersingvogel.mqttcache.model;

import java.time.Instant;

public class CacheEntry {
	public final Instant timestamp;
	public final String message;
	
	public CacheEntry(final String message) {
		this.timestamp = Instant.now();
		this.message = message;
	}
}
