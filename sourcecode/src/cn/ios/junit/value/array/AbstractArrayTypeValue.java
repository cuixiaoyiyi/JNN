package cn.ios.junit.value.array;

import java.util.ArrayList;
import java.util.List;

import cn.ios.ju.base.Pair;
import cn.ios.junit.generic.type.IType;
import cn.ios.junit.util.StringSplicing;
import cn.ios.junit.value.AbstractJUValue;
import cn.ios.junit.value.JUValue;
import cn.ios.junit.value.VarableNameFactory;
import cn.ios.util.Randomness;

public abstract class AbstractArrayTypeValue extends AbstractJUValue {

	protected List<JUValue> parameters = null;

	protected VarableNameFactory varableNameFactory = null;

	private List<Integer> demensions = null;

	private Object possibleValue = null;

	private String stringValue = null;

	public AbstractArrayTypeValue(IType arrayType, List<Pair<String, Object>> constraints,
			VarableNameFactory varableNameFactory, Object possibleValue) {
		this.type = arrayType;
		this.constraints = constraints;
		this.varableNameFactory = varableNameFactory;
		this.possibleValue = possibleValue;
		createDimensionArray();
		createVarable();
	}

	private void createDimensionArray() {
		demensions = new ArrayList<Integer>();
		int min = getArraySizeMin();
		int max = getArraySizeMax();
		for (int i = 0; i < getArrayDimensions(); i++) {
			int size = Randomness.nextInt(min, max);
			try {
				if (possibleValue != null) {
					size = Integer.parseInt(possibleValue.toString());
				}
			} catch (NumberFormatException e) {

			}
			if (size < 1) {
				size = 1;
			}
			demensions.add(size);
		}
	}

	private void createVarable() {
		parameters = new ArrayList<JUValue>();
		int size = 1;
		for (int i : demensions) {
			size *= i;
		}
		for (int i = 0; i < size; i++) {
			parameters.add(getElementValue());
		}
	}

	private int varableIndex = 0;

	private String getVarablesString(List<Integer> numDimensions) {
		if (numDimensions.size() == 0) {
			return "";
		}
		int length = numDimensions.get(0);
		if (numDimensions.size() == 1) {
			String result = null;
			result = StringSplicing.splicingParameterBrace(parameters, varableIndex, length);
			varableIndex += length;
			return result;
		}
		List<String> sub = new ArrayList<String>();
		ArrayList<Integer> subnumDimensions = new ArrayList<Integer>();
		for (int i = 1; i < numDimensions.size(); i++) {
			subnumDimensions.add(numDimensions.get(i));
		}
		for (int i = 0; i < length; i++) {
			String subString = getVarablesString(subnumDimensions);
			sub.add(subString);
		}
		return StringSplicing.splicingParameterBrace(sub, 0, length);
	}

	@Override
	public String toString() {

		if (stringValue == null) {
			varableIndex = 0;
			stringValue = getVarablesString(demensions);
		}

		return stringValue;
	}

	abstract protected JUValue getElementValue();

}
