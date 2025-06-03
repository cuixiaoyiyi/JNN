package cn.ac.ios.ann.parse.set;

import java.util.List;

import cn.ac.ios.ann.parse.*;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

public class SetParserFactory {

	public static void start(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		IParser iParser =  new NotNullParser(null);
		iParser = new NullParser(iParser);
		iParser = new MaxParser(iParser);
		iParser = new MinParser(iParser);
		iParser = new SizeParser(iParser);
		iParser = new NotEmptyParser(iParser);
		iParser = new DefaultValueParser(iParser);
		iParser.start(annotation, parameterConstraints);
	}
}
