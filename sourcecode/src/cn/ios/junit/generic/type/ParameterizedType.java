package cn.ios.junit.generic.type;

import java.util.ArrayList;
import java.util.List;

import cn.ios.junit.util.StringSplicing;

public class ParameterizedType extends TypeNode {

	public ArrayList<IType> actualTypeArguments = new ArrayList<>();

	public IType[] getActualTypeArguments() {
		return actualTypeArguments.toArray(new IType[] {});
	}
	
	public List<soot.Type> getActualTypeArgumentSootTypes() {
		ArrayList<soot.Type> list = new ArrayList<>();
		for(IType type : actualTypeArguments) {
			list.add(type.getSootType());
		}
		return list;
	}
	
	@Override
	public String toString() {
		return StringSplicing.splicingParameterParentheses(actualTypeArguments);
	}
}