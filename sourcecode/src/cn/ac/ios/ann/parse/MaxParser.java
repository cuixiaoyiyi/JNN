package cn.ac.ios.ann.parse;

import cn.ios.ju.base.Pair;

public class MaxParser extends AbstractParser {

	public MaxParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(SIZE_MAX, getAnnotationValue());
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Max");
	}

}
