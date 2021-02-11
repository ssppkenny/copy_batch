package com.example.batch.copy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;

public class LinesReader {

	public List<String> readLines(Resource resource) throws IOException {
		Path path = Paths.get(resource.getURI());
		return Files.readAllLines(path);
	}

}
