package cn.ios.junit.value;

import java.util.List;

import cn.ac.ios.ann.parse.IParser;
import cn.ios.ju.base.Pair;
import cn.ios.junit.config.Config;
import cn.ios.junit.generic.type.IType;
import soot.Type;

public abstract class AbstractJUValue implements JUValue {

	protected IType type = null;

	private Type baseElementType = null;

	private int dimensions = 0;

	private Size size = null;

	protected List<Pair<String, Object>> constraints = null;
	protected List<Object> possibleValues = null;

	@Override
	public int getArrayDimensions() {
		processArrayType();
		return dimensions;
	}

	protected int getArraySizeMin() {
		return getArraySize().min;
	}

	protected int getArraySizeMax() {
		return getArraySize().max;
	}

	private Size getArraySize() {
		if (size == null) {
			size = new Size();
			int min = -1;
			int max = -1;
			if (constraints != null) {
				for (Pair<String, Object> pair : constraints) {
					if (pair.isNumberType()) {
						if (IParser.SIZE_MIN == pair.getKey()) {
							min = ((Number) pair.getValue()).intValue();
						} else if (IParser.SIZE_MAX == pair.getKey()) {
							max = ((Number) pair.getValue()).intValue();
						}
					}
				}
			}

			if (min < 0) {
				min = Config.REF_ARRAY_MIN_SIZE;
			}
			if (max < 0) {
				max = Config.REF_ARRAY_MAX_SIZE;
			}
			if (min > max) {
				min = Config.REF_ARRAY_MIN_SIZE;
				max = Config.REF_ARRAY_MAX_SIZE;
			}
			size.min = min;
			size.max = max;
		}
		return size;
	}

	@Override
	public soot.Type getBaseElementType() {
		processArrayType();
		return baseElementType;
	}

	private void processArrayType() {
		if (baseElementType == null) {
			baseElementType = type.getSootType();
			if (isArray()) {
				soot.ArrayType arrayType = (soot.ArrayType) baseElementType;
				dimensions = arrayType.numDimensions;
				baseElementType = arrayType.baseType;
			}
		}
	}

	@Override
	public IType getType() {
		return type;
	}

	private static class Size {
		public int min = Config.REF_ARRAY_MIN_SIZE;
		public int max = Config.REF_ARRAY_MAX_SIZE;
	}

}
