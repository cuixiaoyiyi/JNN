package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class FloatData extends NumberData {

	public FloatData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Float.valueOf(Float.MAX_VALUE).longValue();
	}

	@Override
	protected long getMinValue() {
		return Float.valueOf(Float.MIN_VALUE).longValue();
	}

}
