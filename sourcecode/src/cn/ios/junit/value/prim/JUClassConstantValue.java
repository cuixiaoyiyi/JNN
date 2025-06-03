package cn.ios.junit.value.prim;

import java.util.List;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUClassConstantValue extends JUPrimConstantValue {

    private JUClassConstantValue(Class<?> genericType, List<Pair<String, Object>> constraints, Object possibleValue) {
        super(new PrimTypeNode(Type.getType(genericType)), constraints, possibleValue);
        value = genericType.getName() + ".class";
    }

    public static JUClassConstantValue v(Class<?> genericType,List<Pair<String, Object>> constraints, Object possibleValue) {
        return new JUClassConstantValue(genericType, constraints, possibleValue);
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
