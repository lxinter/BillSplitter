/**
 * 
 */
package com.example.billsplitter;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author LxInter
 *
 */
public class ArrayComparator implements Comparator<Integer> {

	/**
	 * 
	 */
	
	private Float[] array;
	
	public ArrayComparator(ArrayList<Float> arraylist) {
		// TODO Auto-generated constructor stub
		array = new Float[arraylist.size()];
		array = arraylist.toArray(array);
	}
	
	public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; 
        }
        return indexes;
    }
	@Override
	public int compare(Integer lhs, Integer rhs) {
		// TODO Auto-generated method stub
		return array[lhs].compareTo(array[rhs]);
	}
	
	

}
