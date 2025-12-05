package com.api.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionUtility {

	 private static final Map<String, String> SHARED_SESSION = new ConcurrentHashMap<>();


	    private SessionUtility() {
	    	
	    }

	    public static void put(String key, String value) {
	    	SHARED_SESSION.put(key, value);
	    }

	    public static String get(String key) 
	    {   System.out.println("SessionUtility's session"+SHARED_SESSION.get(key));
	        return SHARED_SESSION.get(key);
	    }

	    public static void clear() {
	    	SHARED_SESSION.clear();
	    }
	}
