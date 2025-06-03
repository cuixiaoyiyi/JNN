package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.IConstraintKey;
import cn.ios.ju.base.Pair;
import cn.ios.junit.config.MagicValues;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUBooleanConstantValue extends JUPrimConstantValue {

	private JUBooleanConstantValue(List<Pair<String, Object>> constraints, Object possibleValue) {
		super(new PrimTypeNode(Type.BOOLEAN_TYPE), constraints, possibleValue);
		boolean i = false;
		if (possibleValue != null) {
			value = String.valueOf(possibleValue);
		} else {
			if(constraints!=null && !constraints.isEmpty()) {
				for(Pair<String, Object> pair:constraints) {
					if(IConstraintKey.ASSERT == pair.getKey() && pair.getValue() instanceof Boolean) {
						i = (boolean) pair.getValue();
					}
				}
			}else {
				i = MagicValues.getBoolean();
			}
			value = String.valueOf(i);
		}
	}
	
	public static JUBooleanConstantValue v(List<Pair<String, Object>> constraints, Object possibleValue) {
		return new JUBooleanConstantValue(constraints, possibleValue);
	}
	
	@Override
	protected Object getMagicValue() {
		return null;
	}

	@Override
	protected long getMaxValue() {
		return 0;
	}

	@Override
	protected long getMinValue() {
		return 0;
	}
}
