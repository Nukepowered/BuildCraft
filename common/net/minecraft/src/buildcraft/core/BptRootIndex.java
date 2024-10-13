/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.minecraft.src.buildcraft.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.src.BuildCraftBuilders;

public class BptRootIndex {

	public TreeMap<String, Integer> filesSet = new TreeMap<>();
	public int maxBpt = 0;

	private final TreeMap<Integer, Path> bluePrintsFile = new TreeMap<>();
	private final TreeMap<Integer, BptBase> bluePrints = new TreeMap<>();
	private final Path baseDir;
	private final Path file;


	public BptRootIndex(String filename) throws IOException {
		baseDir = CoreProxy.getBuildCraftBase().toPath().resolve("blueprints/");
		file = baseDir.resolve(filename);
		Files.createDirectories(baseDir);

		if (Files.notExists(file)) {
			Files.createFile(file);
		}
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

				maxBpt++;

				filesSet.put(line, maxBpt);
				if (Files.exists(bptFile)) {
					bluePrintsFile.put(maxBpt, bptFile);
				}
			}
		}

		saveIndex();
	}

	public void importNewFiles() throws IOException {
		try (Stream<Path> stream = Files.list(baseDir)) {
			for (Path path : stream.collect(Collectors.toList())) {
				System.out.println(path);
				if (Files.isDirectory(path)) {
					continue;
				}

				String foundFile = path.getFileName().toString();
				String[] parts = foundFile.split("[.]");

				if (parts.length < 2 || !parts[1].equals("bpt")) {
					continue;
				}

				if (!filesSet.containsKey(foundFile)) {
					maxBpt++;
					filesSet.put(foundFile, maxBpt);

					bluePrintsFile.put(maxBpt, path);
					for (BptPlayerIndex playerIndex : BuildCraftBuilders.playerLibrary.values()) {
						playerIndex.addBlueprint(path);
					}
				}
			}
		}

		saveIndex();
	}

	public void saveIndex() throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			for (int i = 1; i <= maxBpt; ++i) {
				Path f = bluePrintsFile.get(i);

				if (f != null) {
					writer.write(f.getFileName().toString());
				}

				writer.newLine();
			}

			writer.flush();
		}
	}

	public BptBase getBluePrint(int number) {
		if (!bluePrints.containsKey(number)) {
			if (bluePrintsFile.containsKey(number)) {
				BptBase bpt = BptBase.loadBluePrint(bluePrintsFile.get(number), number);

				if (bpt != null) {
					bluePrints.put(number, bpt);
					bpt.file = bluePrintsFile.get(number);
				} else {
					bluePrintsFile.remove(number);
					return null;
				}
			}
		}

		return bluePrints.get(number);
	}

	public BptBase getBluePrint(String filename) {
		return getBluePrint(filesSet.get(filename));
	}

	public int storeBluePrint(BptBase bluePrint) {
		String name = bluePrint.name;

		if (name == null || name.equals(""))
			name = "unnamed";

		if (filesSet.containsKey(name + ".bpt")) {
			int n = 0;

			while (filesSet.containsKey(name + "_" + n + ".bpt"))
				n++;

			name = name + "_" + n;
		}

		maxBpt++;

		filesSet.put(name + ".bpt", maxBpt);

		name = name + ".bpt";
		Path bptFile = baseDir.resolve(name);

		bluePrintsFile.put(maxBpt, bptFile);
		bluePrints.put(maxBpt, bluePrint);
		bluePrint.file = bptFile;
		bluePrint.save();
		bluePrint.position = maxBpt;

		try {
			saveIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return maxBpt;
	}
}
