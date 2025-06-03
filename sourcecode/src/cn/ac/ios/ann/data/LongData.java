package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class LongData extends NumberData {

	public LongData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Long.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Long .MIN_VALUE;
	}

}
