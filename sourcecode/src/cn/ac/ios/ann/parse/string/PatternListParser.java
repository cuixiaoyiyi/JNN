package cn.ac.ios.ann.parse.string;

import cn.ac.ios.ann.parse.AbstractParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

public class PatternListParser extends AbstractParser {

	public PatternListParser(IParser next) {
		super(next);
	}

	@Override
	protected boolean isCorrespondingAnnotation() {
		return isEndWith(".Pattern$List");
	}

	@Override
	protected Pair<String, Object> getConstraint() {

		Object value = getAnnotationValue();
		if (value instanceof Object[]) {
			IParser iParser = null;
			Object[] values = (Object[]) value;
			for (Object pattern : values) {
				iParser = new PatternParser(null);
				iParser.start((AnnotationTag) pattern, parameterConstraints);
			}
		}

		return null;
	}

}
