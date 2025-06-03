package cn.ac.ios.ann.parse;

import cn.ios.ju.base.Pair;

public class SizeParser extends AbstractParser {

	public SizeParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {

		 parameterConstraints.add(new Pair<String, Object>(SIZE_MAX, getAnnotationValue("max")));
         parameterConstraints.add(new Pair<String, Object>(SIZE_MIN, getAnnotationValue("min")));
         return null;
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Size") || isEndWith(".Range");
	}
}
