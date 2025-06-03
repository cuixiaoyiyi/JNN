package cn.ac.ios.ann.data;

import java.util.ArrayList;
import java.util.List;

import cn.ios.ju.base.IConstraintKey;
import cn.ios.ju.base.Pair;

public class BooleanData extends AbstractData {

	private BooleanData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	public List<?> getAvailableObjects(List<Pair<String, Object>> constraints) {
		List<Boolean> result = new ArrayList<>();
		if (constraints != null && !constraints.isEmpty()) {
			for (Pair<String, Object> pair : constraints) {
				if (IConstraintKey.ASSERT == pair.getKey() && pair.getValue() instanceof Boolean) {
					result.add((boolean) pair.getValue());
				}
			}
		}
		return result;
	}

}
