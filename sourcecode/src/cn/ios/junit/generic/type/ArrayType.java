package cn.ios.junit.generic.type;

public class ArrayType extends IType {

	private IType actualArrayType;

	public void setActualArrayType(IType actualArrayType) {
		this.actualArrayType = actualArrayType;
	}

	public IType getActualArrayType() {
		return actualArrayType;
	}
	
	@Override
	public soot.Type getSootType() {
		return soot.ArrayType.v(actualArrayType.getSootType(), 1) ;
	}

	@Override
	public String getByteString() {
		return "[" + actualArrayType.getByteString();
	}

}
