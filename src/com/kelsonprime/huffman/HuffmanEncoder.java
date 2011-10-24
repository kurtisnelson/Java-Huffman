package com.kelsonprime.huffman;

import java.util.ArrayList;
import java.util.List;

/**
 * Encode a generic with huffman encoding.
 * @author Kurt Nelson
 * @version 1.0
 */
public class HuffmanEncoder<K extends Comparable<?>> {
	EncodingMap<K> map;

	/**
	 * Initialize the engine
	 * @param map <code>Map</code> to use for encoding and decoding
	 */
	public HuffmanEncoder(EncodingMap<K> map) {
		this.map = map;
	}

	/**
	 * Encode a List of generics
	 * @param text List of generics to be encoded
	 * @return Array of <code>Encoding</code> to be saved efficiently and concatenated by the user
	 */
	public Encoding[] encode(List<K> text) {
		Encoding[] out = new Encoding[text.size()];
		int i = 0;
		for (K c : text) {
			if (map.isValid(c)) {
				out[i] = map.getEncoding(c);
			}
			i++;
		}
		return out;
	}

	/**
	 * Decode an array into a List of generics
	 * @param enc Encoded bits
	 * @return Array of generics decoded
	 */
	public List<K> decode(Encoding[] enc) {
		List<K> ret = new ArrayList<K>();
		for (Encoding e : enc) {
			ret.add(map.getDecoded(e));
		}
		return ret;
	}

	/**
	 * Treat a String of 1s and 0s as block to decode.
	 * @param enc 1s and 0s to be decoded
	 * @return List of decoded generics
	 */
	public List<K> decode(String enc) {
		List<K> ret = new ArrayList<K>();
		StringBuilder sb = new StringBuilder();
		int pointer = 0;
		char[] charr = enc.toCharArray();
		while (pointer < charr.length) {
			sb.append(charr[pointer]);
			Encoding test = new Encoding(sb.toString());
			if (map.isEncoding(test)) {
				ret.add(map.getDecoded(test));
				sb = new StringBuilder();
			}
			pointer++;
		}
		if (sb.length() > 0)
			throw new RuntimeException("Invalid input");
		return ret;
	}
}
