package cn.ios.junit.value.array;

import java.util.ArrayList;
import java.util.List;

import cn.ios.ju.base.Pair;
import cn.ios.junit.value.Dependable;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUValue;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableImpl;
import cn.ios.junit.value.VarableNameFactory;

public class JURefArrayTypeValue extends AbstractArrayTypeValue implements Dependable {

	public JURefArrayTypeValue(FullClassType arrayType,List<Pair<String, Object>> constraints, VarableNameFactory varableNameFactory, Object possibleValue) {
		super(arrayType, constraints, varableNameFactory,possibleValue);
	}

	@Override
	public List<JUVarable> getDependentVarable() {
		List<JUVarable> list = new ArrayList<JUVarable>();
		for (JUValue value : parameters) {
			if (value instanceof JUVarable) {
				list.add((JUVarable) value);
			}
		}
		return list;
	}

	@Override
	protected JUValue getElementValue() {
		FullClassType fullClassType = new FullClassType(getBaseElementType());
		fullClassType.getGeneicElements().clear();
		fullClassType.getGeneicElements().addAll(type.getGeneicElements());
		//TODO constraints may be not null
		return VarableImpl.v(fullClassType,null, null, null, varableNameFactory);
	}
}
