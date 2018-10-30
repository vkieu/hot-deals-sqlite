package com.futuretex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.futuretex.service.ProductServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductServices services;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//backup the db for safe keeping
		backupDb("test.db");

		LOG.info("starting to extract product list");
		LOG.info("processing new");
		services.captureProductList("https://www.hotukdeals.com/new", -1);
		LOG.info("processing hot");
		services.captureProductList("https://www.hotukdeals.com/hot", -1);
		LOG.info("processing discussed");
		services.captureProductList("https://www.hotukdeals.com/discussed", -1);
		LOG.info("processing highlights");
		services.captureProductList("https://www.hotukdeals.com", -1);
		LOG.info("Processing product list ended");
	}


	public static void backupDb(final String dbName) throws IOException {
		final DateFormat df = new SimpleDateFormat("ddMMMyyyy");
		final String date = df.format(Calendar.getInstance().getTime());
		try (
				FileOutputStream fos = new FileOutputStream(dbName + date + ".zip", false);
				ZipOutputStream zos = new ZipOutputStream(fos);
				FileInputStream in = new FileInputStream(dbName)
		) {
			byte[] buffer = new byte[1024];
			ZipEntry ze = new ZipEntry(dbName);
			zos.putNextEntry(ze);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
		}
	}

}
