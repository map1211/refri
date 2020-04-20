package kr.kis.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SharedVars {

	public HashMap<String, String> mvar = new HashMap<String, String>();
	
	public synchronized String getMvar(String var) {
		String r = "";
		
		Set set = mvar.keySet();
		
		Iterator iter = set.iterator();
		
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if(var.equals(key)) {
				r = (String)mvar.get(key);
			}
		}
		
		return r ;
	}
	
	public synchronized void setMvar(String key, String value) {
		mvar.put(key, value);
	}
	
	public synchronized void delMvar(String key) {
		mvar.remove(key);
	}
}
