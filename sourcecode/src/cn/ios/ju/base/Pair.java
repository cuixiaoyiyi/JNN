package cn.ios.ju.base;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V>, Comparable<Pair<K, V>> {
	private K k;

	private V v;

	public Pair(K k, V v) {
		this.k = k;
		this.v = v;
	}
	
	
	public void setKey(K k) {
		this.k = k;
	}

	@Override
	public K getKey() {
		return k;
	}

	@Override
	public V getValue() {
		return v;
	}

	@Override
	public V setValue(V value) {
		this.v = value;
		return v;
	}

	@Override
	public String toString() {
		return String.valueOf(k) + ":" + String.valueOf(v);
	}

	public boolean isNumberType() {
		return getValue() instanceof Number;
	}

	public boolean isBooleanType() {
		return (getValue().equals("false") || getValue().equals("true"));
	}


	@Override
	public int compareTo(Pair<K, V> anotherPair) {
		V v0 = anotherPair.getValue();
		if (anotherPair.isNumberType() && isNumberType()) {
			Number p = (Number) v;
			Number q = (Number) v0;
			long x = p.longValue();
			long y = q.longValue();
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		} else if (anotherPair.isNumberType() && !isNumberType()) {
			return 1;
		} else if (!anotherPair.isNumberType() && isNumberType()) {
			return -1;
		}
		return 0;
	}

}
