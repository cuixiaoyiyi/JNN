package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUByteConstantValue extends JUPrimConstantValue {

	private JUByteConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.BYTE_TYPE), constraints, possibleValue);
	}
	
	public static JUByteConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUByteConstantValue(constraints, possibleValue);
	}

	@Override
	protected Object getMagicValue() {
		return MagicValues.getByte();
	}

	@Override
	protected long getMaxValue() {
		return Byte.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Byte.MIN_VALUE;
	}
}
