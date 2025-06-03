package cn.ac.ios.ann.parse.string;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class DigitsParser extends AbstractParser{
	
	final String regexp = "^[0-9]*$";

	public DigitsParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Digits");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(MATCH, regexp);
	}
}
