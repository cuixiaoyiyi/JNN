package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUIntConstantValue extends JUPrimConstantValue {

	private JUIntConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.INT_TYPE), constraints, possibleValue);
	}
	
	public static JUIntConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUIntConstantValue(constraints, possibleValue);
	}
	
	@Override
	protected Object getMagicValue() {
		return MagicValues.getInt();
	}

	@Override
	protected long getMaxValue() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Integer.MIN_VALUE;
	}
}
