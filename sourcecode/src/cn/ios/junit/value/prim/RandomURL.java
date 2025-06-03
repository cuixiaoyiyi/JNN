package cn.ios.junit.value.prim;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;

public class RandomURL {

	public static final String URL_REG_EXPR = "(file|ftp|https?)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
	public static String generate() {
		RgxGen rgxGen = new RgxGen(URL_REG_EXPR);

		RgxGenProperties properties = new RgxGenProperties();
		properties.put("generation.infinite.repeat", "6");
		rgxGen.setProperties(properties);
		return rgxGen.generate();
	}
}
