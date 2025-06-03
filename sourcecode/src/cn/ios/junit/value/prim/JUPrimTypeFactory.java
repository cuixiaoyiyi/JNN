package cn.ios.junit.value.prim;

import java.util.List;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;
import cn.ios.junit.value.JUPrimConstantValue;

public class JUPrimTypeFactory {

	public static JUPrimConstantValue createPrimTypeValue(IType iType, List<Pair<String, Object>> constraints, Object possibleValue) {
		if (iType.isIntType()) {
			return JUIntConstantValue.v(constraints,possibleValue);
		} else if (iType.isLongType()) {
			return JULongConstantValue.v(constraints,possibleValue);
		} else if (iType.isShortType()) {
			return JUShortConstantValue.v(constraints, possibleValue);
		} else if (iType.isFloatType()) {
			return JUFloatConstantValue.v(constraints, possibleValue);
		} else if (iType.isDoubleType()) {
			return JUDoubleConstantValue.v(constraints, possibleValue);
		} else if (iType.isCharType()) {
			return JUCharConstantValue.v(constraints, possibleValue);
		} else if (iType.isBooleanType()) {
			return JUBooleanConstantValue.v(constraints, possibleValue);
		} else if (iType.isByteType()) {
			return JUByteConstantValue.v(constraints, possibleValue);
		}else if (iType.isStringType()) {
			return JUStringConstantValue.v(constraints, possibleValue);
		}

		return null;
	}

	/**
	 * for example: Class<Object>
	 * 
	 * @param type        is Class type
	 * @param genericType is Object type
	 * @param constraints
	 * @return
	 */
	public static JUPrimConstantValue createPrimTypeValue(Class<?> type, Class<?> genericType,
			List<Pair<String, Object>> constraints, Object possibleValue) {
		if (type == Class.class) {
			return JUClassConstantValue.v(genericType, constraints, possibleValue);
		}
		return null;
	}

}
