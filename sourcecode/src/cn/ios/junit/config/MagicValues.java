package cn.ios.junit.config;

import com.github.curiousoddman.rgxgen.RgxGen;

import cn.ios.junit.value.prim.RandomURL;
import cn.ios.junit.value.prim.RandomXml;
import cn.ios.util.Randomness;

public final class MagicValues {

	private static final Object[] INTEGERS = { 1, 0, -1, Integer.MAX_VALUE, Integer.MIN_VALUE };

	private static final Object[] BYTES = { (byte) 1, (byte) 0, (byte) -1, Byte.MAX_VALUE, Byte.MIN_VALUE };

	private static final Object[] SHORTS = { (short) 1, (short) 0, (short) -1, Short.MAX_VALUE, Short.MIN_VALUE };

	private static final Object[] LONGS = { 1l, 0l, -1l, Long.MAX_VALUE, Long.MIN_VALUE };

	public static final Object[] CHARS = { Character.MAX_VALUE, Character.MIN_VALUE };

	private static final Object[] DOUBLES = { 1d, 0d, -1d, Double.MAX_VALUE, Double.MIN_VALUE };

	private static final Object[] FLOATS = { 1.0f, 0.0f, -1.0f, Float.MAX_VALUE, Float.MIN_VALUE };

	private static final String MAIL_REGU = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

	private static final String PHONE_REGU = "^[1]([3-9])[0-9]{9}$";

	private static final String JSON_1 = "[0,1]";

	private static final String JSON_2 = "{\"key\":2 }";

	private static final String JSON_3 = "{\"key\":null}";


	private static final Object[] STRINGS = { "", " ", "\\n", " #", "a ", MAIL_REGU, PHONE_REGU, JSON_1, JSON_2, JSON_3};

	private static Object getMagicValue(Object defaultValue, Object... magicValues) {
		if (magicValues == null) {
			return defaultValue;
		}
		int i = Randomness.nextInt(0, magicValues.length + 6);
		if (i < magicValues.length) {
			return magicValues[i];
		}
		return defaultValue;
	}

	public static boolean getBoolean() {
		return Randomness.nextBoolean();
	}

	public static int getInt() {
		int defaultValue = Randomness.nextInt();
		return (int) getMagicValue(defaultValue, INTEGERS);
	}

	public static byte getByte() {
		byte defaultValue = Randomness.nextByte();
		return (byte) getMagicValue(defaultValue, BYTES);
	}

	public static short getShort() {
		short defaultValue = Randomness.nextShort();
		return (short) getMagicValue(defaultValue, SHORTS);
	}

	public static char getChar() {
		char defaultValue = Randomness.nextChar();
//		return (char) getMagicValue(defaultValue, CHARS);
		return defaultValue;
	}

	public static long getLong() {
		long defaultValue = Randomness.nextLong();
		return (long) getMagicValue(defaultValue, LONGS);
	}

	public static float getFloat() {
		float defaultValue = Randomness.nextFloat();
		return (float) getMagicValue(defaultValue, FLOATS);
	}

	public static double getDouble() {
		double defaultValue = Randomness.nextDouble();
		return (double) getMagicValue(defaultValue, DOUBLES);
	}

	public static String getString() {
		int length = Randomness.nextInt(Config.STRING_MIN_LENGTH, Config.STRING_MAX_LENGTH);
		String defaultValue = Randomness.nextString(length);
		switch (Randomness.nextInt() % 4) {
		case 1:	
			return RandomXml.generate();
		case 2:	
			return RandomURL.generate();
		case 3:	
			if(Randomness.nextBoolean()) {
				return new RgxGen(MAIL_REGU).generate();
			}else {
				return new RgxGen(PHONE_REGU).generate();
			}
		}
		return (String) getMagicValue(defaultValue, STRINGS);
		 
	}

}
