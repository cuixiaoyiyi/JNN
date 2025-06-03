package cn.ios.junit.generic.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.signature.SignatureReader;

import cn.ios.ju.base.Log;
import cn.ios.junit.generic.ClassSignatureVisitor;
import cn.ios.junit.util.StringSplicing;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.ShortType;
import soot.tagkit.SignatureTag;
import soot.tagkit.Tag;

public class ClassType {

	public static final Map<Type, ClassType> map = new HashMap<>();

	public static ClassType forSootClassType(Type type) {
		if (map.containsKey(type)) {
			return map.get(type);
		} else {
			ClassType classType = new ClassType(type);
			map.put(type, classType);
			return classType;
		}
	}

	Type type = null;

	// the ClassType value in map may be multiple; considering only one
	List<Map<String, ClassType>> genericElements = new ArrayList<>(2);

	ClassType superClassType = null;

	Set<ClassType> interfaceClassType = new HashSet<>(2);

	private ClassType(Type type) {
		this.type = type;
		if (type instanceof soot.ArrayType) {
			type = ((soot.ArrayType) type).baseType;
		}
		if (type instanceof RefType) {
			if(!Scene.v().containsClass(type.toString())) {
				Scene.v().addBasicClass(type.toString(), SootClass.SIGNATURES);
				Scene.v().loadBasicClasses();
			}
			if(!Object.class.getName().equals(type.toString())) {
				dealGeneric(((RefType)type).getSootClass());
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		String typeString = type.toString();
		int index = typeString.indexOf('[');
		if (index!=-1) {
			stringBuffer.append(typeString.substring(0, index));
			stringBuffer.append(getGenericInfo());
			stringBuffer.append(typeString.substring(index));
		}else {
			stringBuffer.append(type);
			stringBuffer.append(getGenericInfo());
		}
		return stringBuffer.toString();
	}
	
	public String getGenericInfo() {
		StringBuffer stringBuffer = new StringBuffer();
		if (genericElements.size() > 0) {
			stringBuffer.append("<");
			List<ClassType> getGenericInfo = new ArrayList<ClassType>(2);
			for(Map<String, ClassType> element:genericElements) {
				getGenericInfo.addAll(element.values());
			}
			stringBuffer.append(StringSplicing.splicingParameter(getGenericInfo));
			stringBuffer.append(">");
		}
		return stringBuffer.toString();
	}

	private void dealGeneric(SootClass sootClass) {
		for (Tag tag : sootClass.getTags()) {
			if (tag instanceof SignatureTag) {
				SignatureTag signatureTag = (SignatureTag) tag;
				Log.e("SignatureTag#",signatureTag);

				String className = signatureTag.getSignature();
				SignatureReader signatureReader = new SignatureReader(className);
				ClassSignatureVisitor mySignatureVisitor = new ClassSignatureVisitor();
				signatureReader.accept(mySignatureVisitor);
				List<TypeVariable> typeV = mySignatureVisitor.getTypeV();
				Iterator<TypeVariable> iterator = typeV.iterator();
				while (iterator.hasNext()) {
					TypeVariable typeVariable = iterator.next();
					
					Log.e(typeVariable.toString());
//					Map<String, ClassType> genMap = new HashMap<>(1);
//					ClassType classType = null;
//					Log.e("forTypeVariable#before#",sootClass);
//					classType = forTypeVariable(typeVariable);
//					Log.i(typeVariable.getRawTypeBound());
//					genMap.put(typeVariable.getTypeLiteral(), classType);
//					genericElements.add(genMap);
				}
//				superClassType = forIType(mySignatureVisitor.getSuperClass());
//				for(IType iType : mySignatureVisitor.getInterfaces()) {
//					interfaceClassType.add(forIType(iType));
//				}
				break;
			}
		}
	}

	private static ClassType forTypeVariable(TypeVariable typeVariable) {
		ClassType classType = null;
		Log.e("forTypeVariable#in#",typeVariable);
		Log.e("forTypeVariable#in#",typeVariable.getTypeLiteral());
		Log.e("forTypeVariable#in#",typeVariable.bounds);
		for (IType iType : typeVariable.bounds) {
			classType = forIType(iType);
		}
		if(classType == null) {
			classType = new ClassType(forSootType(typeVariable.getClassName()));
		}
		return classType;
	}

	private static ClassType forArrayType(ArrayType arrayType) {
		IType actualArrayType = arrayType.getActualArrayType();
		ClassType classType = forIType(actualArrayType);
		int dim = 0;
		String arrayTypeString = arrayType.getClassName();
		for (int i = 0; i < arrayTypeString.length(); i++) {
			if('[' == arrayTypeString.charAt(i)) {
				dim ++;
			}
		}
		classType.type = soot.ArrayType.v(classType.type, dim);
		return classType;
	}

	private static ClassType forWildcardType(WildcardType wildcardType) {
		Log.e("forWildcardType#");
		return null;
	}
	
	private static ClassType forIType(IType iType) {
		ClassType classType = null;
		if (iType instanceof ParameterizedType) {
			classType  = forParameterizedType((ParameterizedType) iType);
		} else if (iType instanceof TypeNode) {
			classType = forTypeNode((TypeNode) iType);
		} else if (iType instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) iType;
			classType = forWildcardType(wildcardType);
		} else if (iType instanceof ArrayType) {
			ArrayType arrayType = (ArrayType) iType;
			classType = forArrayType(arrayType);
		} else if (iType instanceof TypeVariable) {
			TypeVariable typeVariable = (TypeVariable) iType;
			classType = forTypeVariable(typeVariable);
		}
		return classType;
	}

	private static ClassType forTypeNode(TypeNode typeNode) {
		ClassType classType = new ClassType(forSootType(typeNode.getClassName()));
		return classType;
	}

	private static ClassType forParameterizedType(ParameterizedType parameterizedType) {
		ClassType classType = new ClassType(forSootType(parameterizedType.getClassName()));
		ClassType tmp = null;
		int i = 0;
		for (IType iType : parameterizedType.getActualTypeArguments()) {
			tmp = forIType(iType);
			Map<String, ClassType> genMap = null;
			if(classType.genericElements.size()>i) {
				genMap = classType.genericElements.get(i++);
				for (Map.Entry<String, ClassType> entry : genMap.entrySet()) {
					Log.e("entry.getKey()==",entry.getKey());
					entry.setValue(tmp);
				}
			}else {
				Log.e("fkkkkkkkk");
//				genMap = new HashMap<>(1);
//				genMap.put(String.valueOf(i++), tmp);
//				classType.genericElements.add(genMap);
		 	}
		}
		return classType;
	}

	public static Type forSootType(String typeName) {
		int index = typeName.indexOf('[');
		int dim = 0;
		if (index != -1) {
			dim = typeName.substring(index).length() / 2;
			typeName = typeName.substring(0, index);
		}
		Type type = null;
		if ("int".equals(typeName)) {
			type = IntType.v();
		} else if ("long".equals(typeName)) {
			type = LongType.v();
		} else if ("short".equals(typeName)) {
			type = ShortType.v();
		} else if ("float".equals(typeName)) {
			type = FloatType.v();
		} else if ("double".equals(typeName)) {
			type = DoubleType.v();
		} else if ("byte".equals(typeName)) {
			type = DoubleType.v();
		} else if ("char".equals(typeName)) {
			type = DoubleType.v();
		} else if ("boolean".equals(typeName)) {
			type = DoubleType.v();
		} else {
			type = RefType.v(typeName);
		}
		if (dim > 0) {
			type = soot.ArrayType.v(type, dim);
		}
		return type;
	}

}
