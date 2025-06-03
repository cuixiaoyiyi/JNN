package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class DoubleData extends NumberData {

	public DoubleData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Double.valueOf(Double.MAX_VALUE).longValue();
	}

	@Override
	protected long getMinValue() {
		return Double.valueOf(Double.MIN_VALUE).longValue();
	}

}
