package cn.ac.ios.ann.parse.string;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class EmailParser extends AbstractParser{
	
	final String regexp = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";

	public EmailParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Email");
	}


	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(MATCH, regexp);
	}
}
