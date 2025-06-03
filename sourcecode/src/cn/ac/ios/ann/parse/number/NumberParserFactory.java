package cn.ac.ios.ann.parse.number;

import java.util.List;

import cn.ac.ios.ann.parse.*;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

public class NumberParserFactory {
	
	

	public static void start(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {

		IParser iParser = new NegativeOrZeroParser(null);
		iParser = new NegativeParser(iParser);
		iParser = new PositiveParser(iParser);
		iParser = new PositiveOrZeroParser(iParser);
		iParser = new DecimalMaxParser(iParser);
		iParser = new DecimalMinParser(iParser);
		iParser = new MaxParser(iParser);
		iParser = new MinParser(iParser);
		iParser = new SizeParser(iParser);
		iParser = new DefaultValueParser(iParser);
		iParser.start(annotation, parameterConstraints);
	}
}
