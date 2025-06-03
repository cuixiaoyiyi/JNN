package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUShortConstantValue extends JUPrimConstantValue {

	private JUShortConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.SHORT_TYPE), constraints, possibleValue);
	}
	
	public static JUShortConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUShortConstantValue(constraints, possibleValue);
	}
	
	@Override
	protected Object getMagicValue() {
		return MagicValues.getShort();
	}

	@Override
	protected long getMaxValue() {
		return Short.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Short.MIN_VALUE;
	}
}
