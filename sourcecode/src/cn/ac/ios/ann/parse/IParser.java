package cn.ac.ios.ann.parse;

import java.util.List;

import cn.ios.ju.base.IConstraintKey;
import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationTag;

public interface IParser extends IConstraintKey{

	public void start(AnnotationTag annotation,List<Pair<String, Object>> parameterConstraints);
	
	public boolean hasNextParser();
	
}
