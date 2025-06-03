package cn.ios.junit.value;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;

public class VarableImpl extends AbstractJUValue implements JUVarable {

	private String name = null;

	private List<Pair<String, Object>> constraints = null;
	private Map<Field, List<Pair<String, Object>>> fieldConstraints = null;
	private List<Object> possibleValues = null;

	private VarableImpl(IType type, List<Pair<String, Object>> paraConstraints, Map<Field, List<Pair<String, Object>>> fieldConstraints,
						List<Object> possibleValues, VarableNameFactory varableNameFactory) {
		this.type = type;
		this.constraints = paraConstraints;
		this.possibleValues = possibleValues;
		this.fieldConstraints = fieldConstraints;
		name = varableNameFactory.getName(getType());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static VarableImpl v(IType type, List<Pair<String, Object>> paraConstraints, Map<Field, List<Pair<String, Object>>> fieldConstraints,
								List<Object> possibleValues, VarableNameFactory varableNameFactory) {
		return new VarableImpl(type, paraConstraints, fieldConstraints, possibleValues, varableNameFactory);
	}

	@Override
	public List<Pair<String, Object>> getConstraints() {
		return constraints;
	}

	@Override
	public Map<Field, List<Pair<String, Object>>> getFieldConstraints() {
		return fieldConstraints;
	}

	@Override
	public List<Object> getPossibleValues() {
		return possibleValues;
	}

	@Override
	public void setPossibleValues(List<Object> possibleValues) {
		this.possibleValues = possibleValues;
	}

}
