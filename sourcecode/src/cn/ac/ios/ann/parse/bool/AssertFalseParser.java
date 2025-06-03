package cn.ac.ios.ann.parse.bool;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class AssertFalseParser extends AbstractParser {

	public AssertFalseParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(ASSERT, false);
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".AssertFalse");
	}

}
