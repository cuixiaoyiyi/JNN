 package cn.ac.ios.ann.parse.bool;

import java.util.List;

import cn.ac.ios.ann.parse.DefaultValueParser;
import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

public class BoolParserFactory {

	public static void start(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		IParser iParser = new AssertTrueParser(null);
		iParser = new AssertFalseParser(iParser);
		iParser = new DefaultValueParser(iParser);
		iParser.start(annotation, parameterConstraints);
	}
}
