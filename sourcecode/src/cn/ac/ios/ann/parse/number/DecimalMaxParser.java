package cn.ac.ios.ann.parse.number;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;


public class DecimalMaxParser extends AbstractParser {

	public DecimalMaxParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(SIZE_MAX, getAnnotationValue());
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".DecimalMax");
	}


}
