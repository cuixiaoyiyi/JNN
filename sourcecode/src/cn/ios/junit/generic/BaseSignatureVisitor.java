/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package cn.ios.junit.generic;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import cn.ios.junit.generic.type.IType;
import cn.ios.junit.generic.type.ArrayType;
import cn.ios.junit.generic.type.ParameterizedType;
import cn.ios.junit.generic.type.TypeNode;
import cn.ios.junit.generic.type.TypeVariable;
import cn.ios.junit.generic.type.WildcardType;

/**
 * Follow the visiting path of ASM to visit getter and setter method signature
 *
 * ClassSignature = ( visitFormalTypeParameter visitClassBound?
 * visitInterfaceBound* )* ( visitSuperClass visitInterface* ) MethodSignature =
 * ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* (
 * visitParameterType* visitReturnType visitExceptionType* ) TypeSignature =
 * visitBaseType | visitTypeVariable | visitArrayType | ( visitClassType
 * visitTypeArgument* ( visitInnerClassType visitTypeArgument* )* visitEnd ) )
 *
 * @since 2.1
 */
public abstract class BaseSignatureVisitor extends SignatureVisitor {

	protected List<TypeVariable> typeV = new LinkedList<>();

	protected int stage = -1;

	public static final int VISIT_FORMAL_TYPE = 0;

	protected Stack<IType> visitingStack = new Stack<>();

	public BaseSignatureVisitor() {
		super(Opcodes.ASM9);
	}

	@Override
	public SignatureVisitor visitArrayType() {
		ArrayType at = new ArrayType();
		visitingStack.push(at);
		return this;
	}

	@Override
	public void visitBaseType(char baseType) {

		TypeNode tn = new TypeNode();
		tn.setObjByteCode(baseType + "");
		visitingStack.push(tn);
		resolveStack();
		// base type could only appear in method parameter list or return type
//    if(stage == VISIT_PARAM) {
//      visitingStack.push(tn);
//    }
//    if(stage == VISIT_RETURN) {
//      returnType = tn;
//    }
	}

	@Override
	public void visitClassType(String classType) {

		TypeNode tn = new TypeNode();
		tn.setObjByteCode("L" + classType + ";");
		visitingStack.push(tn);
		// base type could only appear in method parameter list or return type
//    if(stage == VISIT_PARAM) {
//      visitingStack.push(tn);
//    }
//    if(stage == VISIT_RETURN) {
//      returnType = tn;
//    } if(stage == VISIT_EXCEPTION) {
//      exceptionType = tn;
//    }
	}

	private void resolveStack() {
		if (visitingStack.isEmpty() || visitingStack.size() == 1) {
			return;
		}
		IType top = visitingStack.pop();
		IType peek = visitingStack.peek();

		if (peek instanceof ParameterizedType) {
			((ParameterizedType) peek).actualTypeArguments.add(top);
			return;
		} else if (peek instanceof ArrayType) {
			((ArrayType) peek).setActualArrayType(top);
			resolveStack();
		} else if (peek instanceof WildcardType) {
			((WildcardType) peek).bounds.add(top);
			resolveStack();
		} else if (peek instanceof TypeVariable) {
			((TypeVariable) peek).bounds.add(top);
			resolveStack();
		} else {
			visitingStack.push(top);
			return;
		}

	}

	@Override
	public void visitEnd() {
		resolveStack();
	}

	@Override
	public void visitInnerClassType(String classType) {
		visitClassType(classType);
	}

	@Override
	public void visitTypeArgument() {
		// For unbounded wild card entries, add wild card entry upper bound: extends
		// Object
		visitTypeArgument(SignatureVisitor.EXTENDS);
		visitClassType(Object.class.getName());
		visitEnd();
	}

	@Override
	public SignatureVisitor visitTypeArgument(char typeArg) {
		TypeNode t = (TypeNode) visitingStack.pop();
		if (t instanceof ParameterizedType) {
			visitingStack.push(t);
		} else {
			ParameterizedType pt = new ParameterizedType();
			pt.setObjByteCode(t.getObjByteCode());
			visitingStack.push(pt);
		}

		if (typeArg == SignatureVisitor.INSTANCEOF) {
			return this;
		}
		WildcardType wtn = new WildcardType();
		wtn.setBoundChar(typeArg);
		visitingStack.push(wtn);

		return this;
	}

	@Override
	public void visitTypeVariable(String typeVariable) {
		boolean found = false;
		for (TypeVariable typeVariableNode : typeV) {
			if (typeVariableNode.getTypeLiteral().equals(typeVariable)) {
				visitingStack.push(typeVariableNode);
				found = true;
				break;
			}
		}
		if (!found) {
			TypeNode tn = new TypeNode();
			tn.setObjByteCode("T" + typeVariable + ";");
			visitingStack.push(tn);

		}
		resolveStack();
	}

	@Override
	public SignatureVisitor visitInterface() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		return this;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		return this;
	}

	@Override
	public void visitFormalTypeParameter(String typeVariable) {
		if (stage == VISIT_FORMAL_TYPE && !visitingStack.isEmpty()) {
			visitingStack.pop();
		}
		stage = VISIT_FORMAL_TYPE;
		TypeVariable tvn = new TypeVariable();
		tvn.setTypeLiteral(typeVariable);
		visitingStack.push(tvn);
		typeV.add(tvn);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		return this;
	}

	public List<TypeVariable> getTypeV() {
		return typeV;
	}

}
