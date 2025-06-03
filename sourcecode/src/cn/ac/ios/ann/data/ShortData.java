package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class ShortData extends NumberData {

	public ShortData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Short.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Short.MIN_VALUE;
	}

}
