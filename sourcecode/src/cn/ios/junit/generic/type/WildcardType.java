package cn.ios.junit.generic.type;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.signature.SignatureVisitor;

public class WildcardType extends IType {

	char boundChar;

	public ArrayList<IType> bounds = new ArrayList<>();

	public void setBoundChar(char boundChar) {
		this.boundChar = boundChar;
	}

	public IType[] getUpperBounds() {
		if (boundChar == SignatureVisitor.EXTENDS) {
			return bounds.toArray(new IType[] {});
		} else {
			return null;
		}
	}

	public List<soot.Type> getUpperBoundsSootTypes() {
		ArrayList<soot.Type> list = new ArrayList<>();
		if (boundChar == SignatureVisitor.EXTENDS) {
			if (bounds != null) {
				for (IType type : bounds) {
					list.add(type.getSootType());
				}
			}
		}
		return list;
	}

	public List<soot.Type> getLowerBoundsSootTypes() {
		ArrayList<soot.Type> list = new ArrayList<>();
		if (boundChar == SignatureVisitor.SUPER) {
			if (bounds != null) {
				for (IType type : bounds) {
					list.add(type.getSootType());
				}
			}
		}
		return list;
	}

	public IType[] getLowerBounds() {
		if (boundChar == SignatureVisitor.SUPER) {
			return bounds.toArray(new IType[] {});
		} else {
			return null;
		}
	}

	@Override
	public String getByteString() {
		return boundChar + "";
	}

}