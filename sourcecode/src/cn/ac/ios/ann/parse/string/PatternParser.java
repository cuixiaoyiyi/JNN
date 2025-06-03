package cn.ac.ios.ann.parse.string;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class PatternParser extends AbstractParser{

	public PatternParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Pattern") || isEndWith(".RegEx");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		Object regexp = getAnnotationValue("regexp");
		if( regexp != null) {
			return new Pair<String, Object>(MATCH, regexp);
		}
		return new Pair<String, Object>(MATCH, getAnnotationValue());
	}

}
