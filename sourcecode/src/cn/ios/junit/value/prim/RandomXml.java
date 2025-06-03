package cn.ios.junit.value.prim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;

import cn.ios.ju.base.Log;
import cn.ios.junit.util.Randomness;

public class RandomXml {
	
	public static void main(String[] args) {
		String str = generate();
		Log.i(str);
	}
	
	public static String generate() {
		return new Node().toString();
	}

	private static class Node {
		public static final int ATTRIBUTE_NUM = 3;
		public static final String regex = "[a-zA-Z_][a-zA-Z0-9_]*";
		List<Node> subNodes = new ArrayList<>();
		String tagName = null;
		String value = "";
		Map<String, String> attributes = new HashMap<>();
		RgxGen rgxGen = null;

		private static final int MAX_level_num = 3;

		Node() {
			this(MAX_level_num);
		}

		Node(int level_current) {
			rgxGen = new RgxGen(regex);

			RgxGenProperties properties = new RgxGenProperties();
			properties.put("generation.infinite.repeat", "6");
			rgxGen.setProperties(properties);

			tagName = rgxGen.generate();

			if (Randomness.nextBoolean()) {
				int attriNum = Randomness.nextInt(0, ATTRIBUTE_NUM);
				while (attriNum > 0) {
					attriNum--;
					attributes.put(rgxGen.generate(), Randomness.nextString(Randomness.nextInt(0, 6)));
				}
			}

			if (level_current > 0 && Randomness.nextBoolean()) {
				int subNodeNum = Randomness.nextInt(1, ATTRIBUTE_NUM);
				level_current--;
				while (subNodeNum > 0) {
					subNodeNum--;
					subNodes.add(new Node(level_current));
				}
			}

			if (subNodes.isEmpty() && Randomness.nextBoolean()) {
				value = Randomness.nextString(Randomness.nextInt(0, 6));
			}

		}

		@Override
		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append("<" + tagName);

			for (Entry<String, String> attribute : attributes.entrySet()) {
				stringBuilder.append(" ");
				stringBuilder.append(attribute.getKey());
				stringBuilder.append("=");
				stringBuilder.append("\"");
				stringBuilder.append(attribute.getValue());
				stringBuilder.append("\"");
			}
			if (subNodes.isEmpty() && value == "") {
				stringBuilder.append("/>");
			} else {
				stringBuilder.append(">");
				for (Node node : subNodes) {
					stringBuilder.append(node.toString());
				}
				stringBuilder.append(value);
				stringBuilder.append("</" + tagName + ">");
			}
			return stringBuilder.toString();
		}
	}

}
