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
package cn.ios.junit.generic.type;

import java.math.BigDecimal;

import org.objectweb.asm.Type;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;

/**
 * Data structure for java type
 *
 * @since 2.1
 */
public abstract class IType {

	public abstract String getByteString();

	public String getClassName() {
		try {
			return Type.getType(getByteString()).getClassName();
		} catch (Exception e) {
		}
		return getByteString();
	}

	@Override
	public String toString() {
		return getClassName();
	}
	
	public boolean isArray() {
		return getSootType() instanceof ArrayType;
	}
	
	public soot.Type getBaseElementType() {
		if(isArray()) {
			ArrayType arrayType = (ArrayType) getSootType();
			return arrayType.baseType;
		}
		return getSootType();
	}

	public soot.Type getSootType() {
		return forSootType(getClassName());
	}

	public static final soot.Type forSootType(String typeName) {
		int index = typeName.indexOf('[');
		int dim = 0;
		if (index != -1) {
			dim = typeName.substring(index).length() / 2;
			typeName = typeName.substring(0, index);
		}
		soot.Type type = null;
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

	public final boolean isPrimType() {
		return isPrimType(this);
	}

	public final boolean isIntType() {
		return isIntType(this);
	}

	public final boolean isShortType() {
		return isShortType(this);
	}

	public final boolean isByteType() {
		return isByteType(this);
	}

	public final boolean isCharType() {
		return isCharType(this);
	}

	public final boolean isLongType() {
		return isLongType(this);
	}

	public final boolean isFloatType() {
		return isFloatType(this);
	}

	public final boolean isDoubleType() {
		return isDoubleType(this);
	}

	public final boolean isBooleanType() {
		return isBooleanType(this);
	}

	public final boolean isStringType() {
		return isStringType(this);
	}

	public final boolean sameClass(Class<?> clazz) {
		return sameClass(clazz.getName());
	}

	public final boolean sameClass(String clazzName) {
		return sameClass(clazzName, this);
	}

	public final boolean isPrimOrStringType() {
		return isPrimOrStringType(this);
	}

	public final boolean isBigDecimal() {
		return isBigDecimal(this);
	}

	public static final boolean isIntType(IType iType) {
		return iType.getSootType() instanceof IntType || sameClass(Integer.class, iType);
	}

	public static final boolean isByteType(IType iType) {
		return iType.getSootType() instanceof ByteType || sameClass(Byte.class, iType);
	}

	public static final boolean isShortType(IType iType) {
		return iType.getSootType() instanceof ShortType || sameClass(Short.class, iType);
	}

	public static final boolean isCharType(IType iType) {
		return iType.getSootType() instanceof CharType || sameClass(Character.class, iType);
	}

	public static final boolean isLongType(IType iType) {
		return iType.getSootType() instanceof LongType || sameClass(Long.class, iType);
	}

	public static final boolean isFloatType(IType iType) {
		return iType.getSootType() instanceof FloatType || sameClass(Float.class, iType);
	}

	public static final boolean isDoubleType(IType iType) {
		return iType.getSootType() instanceof DoubleType || sameClass(Double.class, iType);
	}

	public static final boolean isBooleanType(IType iType) {
		return iType.getSootType() instanceof BooleanType || sameClass(Boolean.class, iType);
	}

	public static final boolean isStringType(IType iType) {
		return sameClass(String.class, iType);
	}

	public static final boolean isBigDecimal(IType iType) {
		return sameClass(BigDecimal.class, iType);
	}

	public static final boolean isPrimType(IType iType) {
		return isIntType(iType) || isBooleanType(iType) || isCharType(iType) || isByteType(iType) || isDoubleType(iType)
				|| isFloatType(iType) || isShortType(iType) || isLongType(iType);
	}

	public static final boolean isPrimOrStringType(IType iType) {
		return isPrimType(iType) || isStringType(iType);
	}

	public static final boolean sameClass(Class<?> clazz, IType iType) {
		return sameClass(clazz.getName(), iType);
	}

	public static final boolean sameClass(String clazzName, IType iType) {
		return clazzName.equals(iType.getSootType().toString());
	}

}
