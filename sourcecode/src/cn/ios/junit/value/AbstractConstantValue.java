package cn.ios.junit.value;

import java.util.Collections;
import java.util.List;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;

public abstract class AbstractConstantValue extends AbstractJUValue {

	protected String value = null;
	protected List<Pair<String, Object>> constraints = null;

	protected AbstractConstantValue(IType iType, List<Pair<String, Object>> constraints) {
		this.constraints  = constraints;
		if(null != this.constraints ) {
			Collections.sort(this.constraints);
		}
		this.type = iType;
	}

	public Object getValue() {
		return null;
	}

	@Override
	public String toString() {
		getValue();
		return value;
	}

	protected boolean hasConstraint() {
		return constraints != null && !constraints.isEmpty();
	}

}
