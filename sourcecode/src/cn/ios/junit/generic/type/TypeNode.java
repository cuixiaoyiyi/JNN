package cn.ios.junit.generic.type;

public class TypeNode extends IType {
	private transient org.objectweb.asm.Type typeObj;

	private String objByteCode;
	
	public TypeNode() {}

	public org.objectweb.asm.Type getTypeObj() {
		if (typeObj == null) {
			typeObj = org.objectweb.asm.Type.getType(objByteCode);
		}
		return typeObj;
	}

	@Override
	public String toString() {
		if (typeObj == null) {
			typeObj = org.objectweb.asm.Type.getType(objByteCode);
		}

		if (typeObj.getSort() == org.objectweb.asm.Type.OBJECT) {
			return "class " + typeObj.getClassName();
		} else {
			return typeObj.getClassName();
		}
	}
	
	

	@Override
	public String getByteString() {
		if (typeObj == null) {
			typeObj = org.objectweb.asm.Type.getType(objByteCode);
		}
		if (typeObj.getSort() == org.objectweb.asm.Type.OBJECT) {
			return "L" + typeObj.getClassName() + ";";
		} else {
			return typeObj.toString();
		}
	}

	public String getObjByteCode() {
		return objByteCode;
	}

	public void setObjByteCode(String objByteCode) {
		this.objByteCode = objByteCode;
	}

}