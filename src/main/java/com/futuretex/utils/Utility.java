package com.futuretex.utils;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.util.DigestUtils;

public class Utility {

	public static String md5(String theString) {
		return DigestUtils.md5DigestAsHex(theString.getBytes());
	}

	public static void writeToFile(String file, String content) throws IOException {
		try (FileWriter writer = new FileWriter(file, false)) {
			writer.write(content);
		}
	}

}
