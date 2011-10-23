package com.kelsonprime.huffman;

import java.util.LinkedList;
import java.util.List;


public class Encoding implements Comparable<Object>{
	private List<Boolean> data;
	public Encoding(List<Boolean> path){
		data = new LinkedList<Boolean>();
		for(Boolean b : path){
			data.add(b);
		}
	}
	public Encoding(String path){
		data = new LinkedList<Boolean>();
		for(char c : path.toCharArray()){
			if(c == '0')
				data.add(false);
			else if(c == '1')
				data.add(true);
		}
	}
	
	public String toString(){
		String s = "";
		for(boolean b : data){
			if(b)
				s = s + '1';
			else
				s = s + '0';
		}
		return s;
	}
	
	public static String toString(Encoding[] enc){
		String ret = "";
		for(Encoding e : enc){
			ret += e;
		}
		return ret;
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public int compareTo(Object o) {
		if(this.equals(o))
			return 0;
		if(o instanceof Encoding){
			return this.hashCode() - o.hashCode();
		}
		return -1;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Encoding){
			Encoding other = (Encoding) o;
			if(other.hashCode() == this.hashCode())
				return true;
		}
		return false;
	}
}
