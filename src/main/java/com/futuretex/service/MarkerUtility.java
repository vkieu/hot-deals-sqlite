package com.futuretex.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.futuretex.utils.Utility;

public class MarkerUtility {
	public static void addMarker(String site, String marker) throws IOException {
		//mark last know location
		Utility.writeToFile(getMarkerName(site), marker);
	}

	public static void clearMarker(String site) throws IOException {
		Files.delete(Paths.get(getMarkerName(site)));
	}

	public static boolean hasOtherMarkers() {
		return new File(".").listFiles((dir, name) -> name.toLowerCase().endsWith(".marker")).length > 0;
	}

	public static boolean hasMarker(String site) {
		return new File( getMarkerName(site)).exists();
	}

	public static String getMarkerName(String site) {
		return Utility.md5(site) + ".marker";
	}

	public static String getSiteMarker(String site) throws IOException {
		Path path = Paths.get(getMarkerName(site));
		if (Files.exists(path)) {
			List<String> lines = Files.readAllLines(path);
			if (lines != null && lines.size() > 0) {
				return lines.get(0);
			}
		}
		return site;
	}
}
