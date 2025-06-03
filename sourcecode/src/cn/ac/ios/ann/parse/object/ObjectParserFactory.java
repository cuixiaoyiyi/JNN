package cn.ac.ios.ann.parse.object;

import cn.ac.ios.ann.parse.*;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

import java.util.List;

/**
 * @description: TODO
 * @author: wangmiaomiao
 * @create: 2021-11-05 14:10
 **/

public class ObjectParserFactory {
    public static void start(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
        IParser iParser =  new NotNullParser(null);
        iParser = new NullParser(iParser);
        iParser = new DefaultValueParser(iParser);
        iParser = new FormatWithParser(iParser);
        iParser.start(annotation, parameterConstraints);
    }
}
