package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public class ByteData extends NumberData {

	public ByteData(List<Pair<String, Object>> constraints) {
		super(constraints);
	}

	@Override
	protected long getMaxValue() {
		return Byte.MAX_VALUE;
	}

	@Override
	protected long getMinValue() {
		return Byte.MIN_VALUE;
	}

}
