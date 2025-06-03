package cn.ios.junit.value.array;

import java.util.List;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;
import cn.ios.junit.generic.type.PrimTypeNode;
import cn.ios.junit.value.JUValue;
import cn.ios.junit.value.ValueFactory;
import soot.Type;

public class JUPrimArrayTypeValue extends AbstractArrayTypeValue {


	public JUPrimArrayTypeValue(IType iType, List<Pair<String, Object>> constraints, Object possibleValue) {
		super(iType,constraints, null, possibleValue);
		
	}

	@Override
	protected JUValue getElementValue() {
		return ValueFactory.createValue(new PrimTypeNode(), null,null, null);
	}

}
