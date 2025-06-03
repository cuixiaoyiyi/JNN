package cn.ac.ios.ann.parse.object;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;


public class FormatWithParser extends AbstractParser {

	public FormatWithParser(IParser next) {
		super(next);
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(FORMAT_WITH, getAnnotationValue());
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".FormatWith");
	}

}
