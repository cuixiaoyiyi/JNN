package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUFloatConstantValue extends JUPrimConstantValue {

	private JUFloatConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.FLOAT_TYPE), constraints, possibleValue);
		value = value + "f";
	}

	public static JUFloatConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUFloatConstantValue(constraints, possibleValue);
	}

	@Override
	protected Object getMagicValue() {
		return MagicValues.getFloat();
	}

	@Override
	protected long getMaxValue() {
		return  (long) Float.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return (long) Float.MIN_VALUE;
	}
}
