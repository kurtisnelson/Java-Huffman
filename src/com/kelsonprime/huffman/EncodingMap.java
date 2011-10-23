package com.kelsonprime.huffman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * Generic a Huffman encoding tree and map from generics
 * @author Kurt Nelson
 * @version 1.0
 * @param <K> Type to encode
 */
public class EncodingMap<K extends Comparable<?>> {
	private final HashMap<K, Encoding> map;
	private final HashMap<Encoding, K> invertedMap;
	private ArrayList<K> seed;

	/**
	 * Create a map using the specified <code>List</code> to seed frequencies and available Keys
	 * @param seed List of keys to generate a Huffman map for
	 */
	public EncodingMap(List<K> seed) {
		this.seed = new ArrayList<K>(seed.size());
		this.seed.addAll(seed);
		Collections.sort(this.seed, new KeyCompare());
		map = makeTreeMap(this.seed);
		invertedMap = invertMap(map);
	}
	
	public EncodingMap(HashMap<K, Encoding> map){
		this.map = map;
		invertedMap = invertMap(map);
	}
	
	/**
	 * Decodes an <code>Encoding</code> into the generic
	 * @param enc Encoding to decode
	 * @return Generic if decoded properly, null on error
	 */
	public K getDecoded(Encoding enc){
		if(isEncoding(enc))
			return invertedMap.get(enc);
		return null;
	}
	
	/**
	 * Encodes a generic into an <code>Encoding</code>
	 * @param key Generic to encode
	 * @return Encoding if encoding supported, null on error
	 */
	public Encoding getEncoding(K key){
		if(isValid(key))
			return map.get(key);
		return null;
	}
	
	/**
	 * Checks if this <code>EncodingMap</code> can encode a key
	 * @param key Generic to check
	 * @return True if encoding supported
	 */
	public boolean isValid(K key){
		return map.containsKey(key);
	}
	
	/**
	 * Checks if this <code>EncodingMap</code> can decode an <code>Encoding</code>
	 * @param enc Encoding to check
	 * @return True if decoding is available
	 */
	public boolean isEncoding(Encoding enc){
		return invertedMap.containsKey(enc);
	}

	/**
	 * Generates map to support efficient decoding
	 * @param map Map for encoding
	 * @return Map for decoding
	 */
	private HashMap<Encoding, K> invertMap(HashMap<K, Encoding> map){
		HashMap<Encoding, K> inverted = new HashMap<Encoding, K>(map.size());
		for(Entry<K, Encoding> e : map.entrySet()){
			inverted.put(e.getValue(), e.getKey());
		}
		return inverted;
	}
	
	/**
	 * Actually does the Huffman algorithm to generate a tree, then the map.
	 * @param seed Supported keys
	 * @return A map for encoding
	 */
	private HashMap<K, Encoding> makeTreeMap(ArrayList<K> seed) {
		LinkedHashSet<K> set = new LinkedHashSet<K>();
		set.addAll(seed);
		ArrayList<HuffNode<K>> nodes = new ArrayList<HuffNode<K>>(set.size());
		for (K c : set) {
			nodes.add(new HuffNode<K>(c, Collections.frequency(seed, c)));
		}
		Collections.sort(nodes);
		while (nodes.size() > 1) {
			HuffNode<K> c1 = nodes.remove(0);
			HuffNode<K> c2 = nodes.remove(0);
			HuffNode<K> parent = new HuffNode<K>(null, c1.getScore() + c2.getScore());
			parent.left = c1;
			parent.right = c2;
			nodes.add(parent);
			Collections.sort(nodes);
		}
		HuffNode<K> root = nodes.remove(0);
		nodes = null;
		System.gc(); //We've just made a massive mess, so recommend cleaning things up.
		
		HashMap<K, Encoding> treeMap = new HashMap<K, Encoding>();
		mapTree(root, treeMap, new Stack<Boolean>());
		return treeMap;
	}
	
	/**
	 * Maps a huffman tree onto a <code>HashMap</code>
	 * @param parent HuffNode to be processed
	 * @param map Pointer to map being built
	 * @param path Current path in the tree
	 */
	private void mapTree(HuffNode<K> parent, HashMap<K, Encoding> map, Stack<Boolean> path){
		//We've arrived at a leaf, add it to the map.
		if(parent.left == null && parent.right == null){
			map.put(parent.getValue(), new Encoding(path));
		}
		if(parent.left != null){
			path.push(false);
			mapTree(parent.left, map, path);
			path.pop();
		}
		if(parent.right != null){
			path.push(true);
			mapTree(parent.right, map, path);
			path.pop();
		}
	}
	
	@Override
	public String toString(){
		return mapToString();
	}

	/**
	 * Returns a user readable key for encoding
	 * @return ASCII table
	 */
	public String mapToString(){
		String s = "Key -> Encoding\n";
		for(Entry<K, Encoding> e : map.entrySet()){
			s += e.getKey() + " <-> " + e.getValue() + "\n";
		}
		return s;
	}
	
	/**
	 * Returns a user readable key for decoding
	 * @return ASCII table
	 */
	public String invertedToString(){
		String s = "Encoding -> Key\n";
		for(Entry<Encoding, K> e : invertedMap.entrySet()){
			s += e.getKey() + " <-> " + e.getValue() + "\n";
		}
		return s;
	}
	
	/**
	 * Scores a key for priority on the tree, uses ASCII value to break ties for <code>Character</code>, hash code otherwise.
	 * @param c Item being scored
	 * @return Unique score
	 */
	private int score(K c) {
		if(c instanceof Character){
			return Collections.frequency(seed, c) * 100 + ((Character)c);
		}
		return Collections.frequency(seed, c) * 100 + c.hashCode();
	}

	/**
	 * Makes sorts use score to compare.
	 * @author Kurt Nelson
	 * @version 1.0
	 */
	private class KeyCompare implements Comparator<Object> {
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			K a = (K) o1;
			K b = (K) o2;
			if (a.equals(b))
				return 0;
			return score(b) - score(a);
		}

	}

	/**
	 * Wraps a generic so it can be put in a Huffman tree.
	 * @author Kurt Nelson
	 * @version 1.0
	 * @param <T>
	 */
	private class HuffNode<T> implements Comparable<Object>{
		private T value;
		private int score;
		public HuffNode<T> left;
		public HuffNode<T> right;
		
		/**
		 * Sets up a node
		 * @param value
		 * @param score Score assigned by <code>EncodingMap</code>
		 */
		public HuffNode(T value, int score){
			this.value = value;
			this.score = score;
		}
		
		public T getValue(){
			return value;
		}
		
		public int getScore(){
			return score;
		}
		
		@SuppressWarnings("unchecked")
		public int compareTo(Object o){
			HuffNode<T> other = (HuffNode<T>) o;
			return this.getScore() - other.getScore();
		}
	}
}
