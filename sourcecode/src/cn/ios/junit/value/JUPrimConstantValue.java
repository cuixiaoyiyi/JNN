package cn.ios.junit.value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;
import cn.ios.util.Randomness;

public abstract class JUPrimConstantValue extends AbstractConstantValue {

	protected JUPrimConstantValue(IType iType, List<Pair<String, Object>> constraints, Object possibleValue) {
		super(iType, constraints);
		if (possibleValue != null) {
			value = String.valueOf(possibleValue);
		} else {
			Object i = getConstraintValue();
			value = String.valueOf(i);
		}
	}

	protected Object getConstraintValue() {
		Object value = null;
		ArrayList<Object> availableValues = getAvailableValues1();
		if (!availableValues.isEmpty()) {
			return Randomness.choice(availableValues);
		} else {
			value = getMagicValue();
		}
		return value;
	}

	protected ArrayList<Object> getAvailableValues() {
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

	protected ArrayList<Object> getAvailableValues1() {
		ArrayList<Object> valueList = new ArrayList<>();
		if (!hasConstraint()) {
			return valueList;
		}
		List<Object> sortValueList = constraints.stream().filter(Pair::isNumberType).map(Pair::getValue).sorted()
				.collect(Collectors.toList());

		if (getType().isIntType()) {
			sortValueList.add(0, Integer.MIN_VALUE);
			sortValueList.add(Integer.MAX_VALUE);
		} else if (getType().isShortType()) {
			sortValueList.add(0, Short.MIN_VALUE);
			sortValueList.add(Short.MAX_VALUE);
		} else if (getType().isLongType() || getType().sameClass(BigDecimal.class)) {
			sortValueList.add(0, Long.MIN_VALUE);
			sortValueList.add(Long.MAX_VALUE);
		}

		for (int i = 0; i < sortValueList.size(); i++) {
			valueList.add(sortValueList.get(i));
			if (i != sortValueList.size() - 1) {
				valueList.add(Randomness.nextLong(Long.parseLong(sortValueList.get(i).toString()),
						Long.parseLong(sortValueList.get(i + 1).toString())));
			}
		}
		return valueList;
	}

	abstract protected Object getMagicValue();

	abstract protected long getMaxValue();

	abstract protected long getMinValue();

}