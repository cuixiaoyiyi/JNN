package cn.ac.ios.ann.parse;

import cn.ios.ju.base.Pair;

public class MinParser extends AbstractParser {

	public MinParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(SIZE_MIN, getAnnotationValue());
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Min");
	}

}
