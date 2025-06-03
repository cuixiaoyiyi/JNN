package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class IntData extends NumberData {

	public IntData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Integer.MIN_VALUE;
	}

}
