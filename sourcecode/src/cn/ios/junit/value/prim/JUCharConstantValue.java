package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUCharConstantValue extends JUPrimConstantValue {

	private JUCharConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.CHAR_TYPE), constraints, possibleValue);
		value = "" + value;
		value = "'";
		value = value.replace("\\", "\\\\");
		value = value.replace("\'", "\\\'");
	}
	
	@Override
	public String toString() {
		return "\'" + super.toString() + "\'";
	}

	public static JUCharConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUCharConstantValue(constraints, possibleValue);
	}
	
	@Override
	protected Object getMagicValue() {
		return MagicValues.getChar();
	}

	@Override
	protected long getMaxValue() {
		return Character.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Character.MIN_VALUE;
	}
}
