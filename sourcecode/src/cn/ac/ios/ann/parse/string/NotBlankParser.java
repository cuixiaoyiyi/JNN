package cn.ac.ios.ann.parse.string;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class NotBlankParser extends AbstractParser{

	public NotBlankParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".NotBlank");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(NOT_EQUAL, "");
	}
}
