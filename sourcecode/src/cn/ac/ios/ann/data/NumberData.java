package cn.ac.ios.ann.data;

import java.util.ArrayList;
import java.util.List;

import cn.ios.ju.base.Pair;
import cn.ios.util.Randomness;

public abstract class NumberData extends AbstractData {

	public NumberData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	public List<?> getAvailableObjects(List<Pair<String, Object>> constraints) {
		if (!hasConstraint()) {
			return new ArrayList<Object>();
		}
		ArrayList<Object> doubles = new ArrayList<>();
		for (int i = 0; i < constraints.size(); i++) {
			Pair<String, Object> pair = constraints.get(i);
			if (pair.isNumberType()) {
				long value = ((Number) pair.getValue()).longValue();
				if (value < getMinValue() || value > getMaxValue()) {
					continue;
				}
				doubles.add(value);
				long min = getMinValue();
				long max = getMaxValue();
				if (i == 0 && value != getMinValue()) {
					doubles.add(getMinValue());
					max = value;
					doubles.add(Randomness.nextLong(min, max));
				} else if (i < constraints.size() - 1) {
					Pair<String, Object> next = constraints.get(i + 1);
					if (next.isNumberType()) {
						max = ((Number) next.getValue()).longValue();
						doubles.add(Randomness.nextLong(min, max));
					} else {
						if (value != getMaxValue()) {
							min = value;
							max = getMaxValue();
							doubles.add(Randomness.nextLong(min, max));
						}
						doubles.add(getMaxValue());
						break;
					}
				} else {
					if (value != getMaxValue()) {
						min = value;
						max = getMaxValue();
						doubles.add(Randomness.nextLong(min, max));
					}
					doubles.add(getMaxValue());
				}
			}

		}
		return doubles;
	}

	protected abstract long getMaxValue();

	protected abstract long getMinValue();


}
