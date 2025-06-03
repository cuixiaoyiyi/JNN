package cn.ac.ios.ann.parse.number;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class NegativeParser extends AbstractParser{

	public NegativeParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Negative");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(LESS, 0);
	}
}
