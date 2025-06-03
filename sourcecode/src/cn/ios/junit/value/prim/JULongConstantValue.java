package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JULongConstantValue extends JUPrimConstantValue {

	private JULongConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.LONG_TYPE), constraints, possibleValue);
		value = value + "L";
	}
	
	public static JULongConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JULongConstantValue(constraints, possibleValue);
	}
	
	@Override
	protected Object getMagicValue() {
		return MagicValues.getLong();
	}

	@Override
	protected long getMaxValue() {
		return Long.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Long.MIN_VALUE;
	}
}
