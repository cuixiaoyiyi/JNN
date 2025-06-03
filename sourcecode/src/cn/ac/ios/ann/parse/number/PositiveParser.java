package cn.ac.ios.ann.parse.number;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class PositiveParser extends AbstractParser{

	public PositiveParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Positive");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(GREATER, 0);
	}
}
