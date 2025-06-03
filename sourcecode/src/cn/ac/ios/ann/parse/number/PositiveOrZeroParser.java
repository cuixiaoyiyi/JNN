package cn.ac.ios.ann.parse.number;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class PositiveOrZeroParser extends AbstractParser{

	public PositiveOrZeroParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".PositiveOrZero");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(GREATER_EQUAL, 0);
	}
}
