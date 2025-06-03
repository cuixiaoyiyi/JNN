package cn.ac.ios.ann.parse;

import cn.ios.ju.base.Pair;

public class NullParser extends AbstractParser{

	public NullParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Null")   || isEndWith(".Nullable");
		}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(EQUAL, null);
	}


}
