package com.workspace.comparators;
public class MyComparator implements java.util.Comparator<String> {

    public MyComparator() {
        super();
    }

    public int compare(String s1, String s2) {
        int dist1 = s1.split(" ").length;
        int dist2 = s2.split(" ").length;

        if(dist2<dist1)
        	return -1;
        else if(dist2>dist1)
        	return 1;
        return 0;
    }
}