package net.minecraft.src.buildcraft.core.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import net.minecraft.src.buildcraft.core.CoreProxy;

/**
 * Simple mod localization class.
 *
 * @author Jimeo Wan
 * @license Public domain
 */
public class Localization {

	public static Localization instance = new Localization();

	private static final String DEFAULT_LANGUAGE = "en_US";

	private String loadedLanguage = null;
	private Properties defaultMappings = new Properties();
	private Properties mappings = new Properties();

	/**
	 * Loads the mod's localization files. All language files must be stored in
	 * "[modname]/lang/", in .properties files. (ex: for the mod 'invtweaks',
	 * the french translation is in: "invtweaks/lang/fr_FR.properties")
	 *
	 * @param modName
	 *            The mod name
	 */
	public Localization() {
		load(getCurrentLanguage());
	}

	/**
	 * Get a string for the given key, in the currently active translation.
	 *
	 * @param key
	 * @return
	 */
	public synchronized String get(String key) {
		String currentLanguage = getCurrentLanguage();
		if (!currentLanguage.equals(loadedLanguage)) {
			load(currentLanguage);
		}

		return mappings.getProperty(key, defaultMappings.getProperty(key, key));
	}

	private void load(String newLanguage) {
		defaultMappings.clear();
		mappings.clear();
		try {
			ClassLoader loader = Localization.class.getClassLoader();
			Enumeration<URL> resources = loader.getResources("/lang/buildcraft/" + newLanguage + ".properties");
			while (resources.hasMoreElements()) {
				URL res = resources.nextElement();
				try (InputStream stream = res.openStream()) {
					Properties props = new Properties();
					props.load(stream);
					this.mappings.putAll(props);
				}
			}

			resources = loader.getResources("/lang/buildcraft/" + DEFAULT_LANGUAGE + ".properties");
			while (resources.hasMoreElements()) {
				URL res = resources.nextElement();
				try (InputStream stream = res.openStream()) {
					Properties props = new Properties();
					props.load(stream);
					this.defaultMappings.putAll(props);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadedLanguage = newLanguage;
	}

	private static String getCurrentLanguage() {
		return CoreProxy.getCurrentLanguage();
	}
}
