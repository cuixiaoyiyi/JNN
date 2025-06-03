package cn.ac.ios.ann.parse;

import cn.ios.ju.base.Pair;

public class NotNullParser extends AbstractParser{

	public NotNullParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Notnull")   || isEndWith(".Nonnull");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(NOT_EQUAL, null);
	}


}
