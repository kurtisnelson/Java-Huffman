package com.kelsonprime.huffman;

import java.util.LinkedList;
import java.util.List;


public class Encoding implements Comparable<Object>{
	private List<Boolean> data;
	public Encoding(List<Boolean> path){
		data = new LinkedList<Boolean>(path);
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
		StringBuilder sb = new StringBuilder();
		for(boolean b : data){
			if(b)
				sb.append('1');
			else
				sb.append('0');
		}
		return sb.toString();
	}
	
	public static String toString(Encoding[] enc){
		StringBuilder sb = new StringBuilder();
		for(Encoding e : enc){
			sb.append(e);
		}
		return sb.toString();
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
