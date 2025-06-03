package cn.ios.junit.value;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import cn.ios.ju.base.Pair;

public interface JUVarable extends JUValue {

	public String getName();

	public List<Pair<String, Object>> getConstraints();
	public Map<Field, List<Pair<String, Object>>> getFieldConstraints();
	public List<Object> getPossibleValues();
	public void setPossibleValues(List<Object> possibleValues);

}
