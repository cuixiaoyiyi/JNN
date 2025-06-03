package cn.ac.ios.ann.parse;

import cn.ios.ju.base.Pair;

public class DefaultValueParser extends AbstractParser {

	public DefaultValueParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(DEFAULT_VALUE, getAnnotationValue());
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".DefaultValue");
	}

}
