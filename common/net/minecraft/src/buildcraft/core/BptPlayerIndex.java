package net.minecraft.src.buildcraft.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

public class BptPlayerIndex {

	private final TreeMap<String, Path> bluePrintsFile = new TreeMap<>();

	private final Path baseDir;
	private final Path file;

	public BptPlayerIndex(String filename, BptRootIndex rootIndex) throws IOException {
		baseDir = CoreProxy.getBuildCraftBase().toPath().resolve("blueprints/");
		file = baseDir.resolve(filename);
		Files.createDirectories(baseDir);

		if (Files.notExists(file)) {
			Files.createFile(file);

			for (String file : rootIndex.filesSet.keySet()) {
				bluePrintsFile.put(file, baseDir.resolve(file));
			}

			saveIndex();
		} else
			loadIndex();
	}

	public void loadIndex() throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				line = line.replaceAll("\\n", "");
				Path bptFile = baseDir.resolve(line);
				bluePrintsFile.put(line, bptFile);
			}
		}
	}

	public void addBlueprint(Path file) throws IOException {
		bluePrintsFile.put(file.getFileName().toString(), file);
		saveIndex();
	}

	public void saveIndex() throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			for (String line : bluePrintsFile.keySet()) {
				writer.write(line);
				writer.newLine();
			}

			writer.flush();
		}
	}

	public void deleteBluePrint(String fileName) {
		bluePrintsFile.remove(fileName);

		try {
			saveIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String nextBpt(String name) {
		if (bluePrintsFile.size() == 0)
			return null;
		else if (name == null)
			return bluePrintsFile.firstKey();
		else
			return bluePrintsFile.higherKey(name);
	}

	public String prevBpt(String name) {
		if (bluePrintsFile.size() == 0)
			return null;
		else if (name == null)
			return bluePrintsFile.lastKey();
		else
			return bluePrintsFile.lowerKey(name);
	}
}
