package cn.ac.ios.ann.parse.set;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class NotEmptyParser extends AbstractParser{

	public NotEmptyParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".NotEmpty");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(NOT_EMPTY, null);
	}


}
