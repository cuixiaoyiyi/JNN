package cn.ios.junit.generic.type;

import java.util.ArrayList;
import java.util.List;

public class TypeVariable extends IType {

	String typeLiteral;

	public ArrayList<IType> bounds = new ArrayList<>();
	
	public void setTypeLiteral(String typeLiteral) {
		this.typeLiteral = typeLiteral;
	}

	@Override
	public String getByteString() {
		return bounds.get(0).getByteString();
	}

	public IType[] getBounds() {
		return bounds.toArray(new IType[] {});
	}
	
	public List<soot.Type> getBoundsSootTypes() {
		ArrayList<soot.Type> list = new ArrayList<>();
		for(IType type : bounds) {
			list.add(type.getSootType());
		}
		return list;
	}
	
	@Override
	public String toString() {
		return "TypeVariable: " + typeLiteral + "=" + bounds;
	}

	public String getTypeLiteral() {
		return typeLiteral;
	}

	public TypeNode getRawTypeBound() {
		IType t = bounds.get(0);

		// The bounds can only be TypeNode or TypeVariableNode
		if (t instanceof TypeNode) {
			return (TypeNode) t;
		}
		return ((TypeVariable) t).getRawTypeBound();
	}

}