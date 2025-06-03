package cn.ac.ios.ann.parse.number;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;

public class NegativeOrZeroParser extends AbstractParser{

	public NegativeOrZeroParser(IParser next) {
		super(next);
	}
	
	@Override
	protected boolean isCorrespondingAnnotation() {	 
		return isEndWith(".NegativeOrZero");
	}

	@Override
	protected Pair<String, Object> getConstraint() {
		return new Pair<String, Object>(LESS_EQUAL, 0);
	}
}
