package cn.ios.junit.generic.type;

import org.objectweb.asm.Type;


public class PrimTypeNode extends TypeNode {

	public PrimTypeNode(Type asmType) {
		setObjByteCode(asmType.toString());
	}

}
