package cn.ios.junit.value.prim;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.github.curiousoddman.rgxgen.RgxGen;

import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;
import cn.ios.util.Randomness;

public class JUStringConstantValue extends JUPrimConstantValue {

	private JUStringConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.getType(String.class)), constraints, possibleValue);
	}

	@Override
	public String toString() {
		String result = super.toString();
		result = result.replace("\\", "\\\\");
		result = result.replace("\"", "\\\"");
		return ("\"" + result + "\"");
	}

	@Override
	protected ArrayList<Object> getAvailableValues() {
		if (!hasConstraint()) {
			return new ArrayList<Object>();
		}
		ArrayList<Object> results = new ArrayList<>();
		for (Pair<String, Object> pair : constraints) {
			if (IParser.MATCH == pair.getKey() && pair.getValue() != null) {
				String reg = pair.getValue().toString();
				String result = new RgxGen(reg).generate();
				results.add(result);
			}else if(IParser.SIZE_MIN == pair.getKey() || IParser.SIZE_MAX == pair.getKey()) {
				int min = getArraySizeMin();
				int max = getArraySizeMax();
				int length = Randomness.nextInt(min, max);
				String result = Randomness.nextString(length);
				results.add(result);
			}else if(IParser.EQUAL == pair.getKey()) {
				results.add(String.valueOf(pair.getValue()));
			}
		}
		return results;
	}

	public static JUStringConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUStringConstantValue(constraints, possibleValue);
	}

	@Override
	protected Object getMagicValue() {
		return MagicValues.getString();
	}

	@Override
	protected long getMaxValue() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return 0;
	}
}
