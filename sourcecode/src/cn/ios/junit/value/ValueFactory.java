package cn.ios.junit.value;

import cn.ac.ios.ann.AAPI;
import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;
import cn.ios.junit.value.array.JUPrimArrayTypeValue;
import cn.ios.junit.value.array.JURefArrayTypeValue;
import cn.ios.junit.value.expr.JUNewExprImpl;
import cn.ios.junit.value.prim.JUPrimTypeFactory;
import soot.PrimType;
import soot.Type;

import java.math.BigDecimal;
import java.util.List;

public class ValueFactory {

	public static JUValue createValue(IType iType, VarableNameFactory nameFactory,
			List<Pair<String, Object>> constraints,  Object possibleValue) {
		if (iType.isPrimOrStringType()) {
			return JUPrimTypeFactory.createPrimTypeValue(iType, constraints, possibleValue);
		} else if (iType.sameClass(Class.class)) {
//			if (!fullClassType.getGeneicElements().isEmpty()) {
//				// TODO whether fullClassType.getGeneicElements() always is one element
//				return JUPrimTypeFactory.createPrimTypeValue(iType,
//						fullClassType.getGeneicElements().get(0).getClassType(), constraints, possibleValue);
//			}
			return null;
		} else if (iType.isArray()) {
			Type baseType = iType.getBaseElementType();
			if (baseType instanceof PrimType || String.class.getName().equals(baseType.toString())) {
				return new JUPrimArrayTypeValue(iType, constraints, possibleValue);
			} else {
				return new JURefArrayTypeValue(iType, constraints, nameFactory, possibleValue);
			}
		} else {
			if (possibleValue != null) {
				return JUNewExprImpl.v1(iType, nameFactory, possibleValue);
			} else if (AAPI.getFieldConstraints().containsKey(type)) {
				return JUNewExprImpl.v1(iType, nameFactory,AAPI.getFieldConstraints().get(type));
			} else {
				return JUNewExprImpl.v(iType, nameFactory);
			}
		}
	}
}
