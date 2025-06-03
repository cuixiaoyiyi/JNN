package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUDoubleConstantValue extends JUPrimConstantValue {

	private JUDoubleConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.DOUBLE_TYPE), constraints, possibleValue);
		value = value + "d";
	}

	public static JUDoubleConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUDoubleConstantValue(constraints, possibleValue);
	}

	@Override
	protected Object getMagicValue() {
		return MagicValues.getDouble();
	}

	@Override
	protected long getMaxValue() {
		return (long) Double.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return (long) Double.MIN_VALUE;
	}
}
