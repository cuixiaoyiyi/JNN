package cn.ac.ios.ann.parse.string;

import java.util.List;

import cn.ac.ios.ann.parse.*;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

public class StringParserFactory {

	public static void start(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		IParser iParser = new NotBlankParser(null);
		iParser = new NotNullParser(iParser);
		iParser = new NullParser(iParser);
		iParser = new PatternParser(iParser);
		iParser = new PatternListParser(iParser);
		iParser = new EmailParser(iParser);
		iParser = new MaxParser(iParser);
		iParser = new MinParser(iParser);
		iParser = new SizeParser(iParser);
		iParser = new DigitsParser(iParser);
		iParser = new DefaultValueParser(iParser);
		iParser.start(annotation, parameterConstraints);
	}
}
