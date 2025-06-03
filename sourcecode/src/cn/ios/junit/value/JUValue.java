package cn.ios.junit.value;

import cn.ios.junit.generic.type.IType;

public interface JUValue {

	public IType getType();

	default public boolean isArray() {
		return getType().getSootType() instanceof soot.ArrayType;
	}

	/**
	 */
	default soot.Type getSootType(){
		return getType().getSootType();
	}
	
	public soot.Type getBaseElementType();

	public int getArrayDimensions();

	default String getGenericParametersString() {
		return getType().toString();
	}

}
