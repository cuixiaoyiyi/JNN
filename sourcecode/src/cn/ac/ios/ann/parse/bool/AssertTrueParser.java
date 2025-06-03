package cn.ac.ios.ann.parse.bool;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class AssertTrueParser extends AbstractParser {

	public AssertTrueParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(ASSERT, true);
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".AssertTrue") ;
	}

}
