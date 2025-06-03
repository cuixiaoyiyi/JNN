package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class CharData extends NumberData {

	public CharData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Character.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Character.MIN_VALUE;
	}

}
