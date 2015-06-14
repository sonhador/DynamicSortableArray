/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 MongJu Jung <mongju.jung@emc.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
import java.util.ArrayList;
import java.util.List;

public class Main {
	private static int SIZE = 100000;
	
	public static void main(String []args) throws InstantiationException, IllegalAccessException {
		long start = System.currentTimeMillis();
		
		List<DataElem> list = new ArrayList<DataElem>();
		
		for (int i=0; i<SIZE; i++) {
			DataElem elem = new DataElem();
			elem.init();
			
			list.add(elem);
		}
		
		long end = System.currentTimeMillis();
		long rawAdditionScore = end - start;
		System.out.println("Raw addition: " + rawAdditionScore);
		
		start = System.currentTimeMillis();
		list.sort(new DataElemComparator<DataElem>());
		end = System.currentTimeMillis();
		long rawSortScore = end - start;

		System.out.println("Raw sort: " + rawSortScore);
		System.out.println("Raw Total: " + (rawAdditionScore + rawSortScore));
		System.out.println();
		
		/////////////////////////////////////////////////////////////////////////////////////////////
		
		start = System.currentTimeMillis();
		
		DynamicSortableArray<DataElem> arr = 
				new DynamicSortableArray<DataElem>(20, DataElem.class, new DataElemComparator<DataElem>());
		
		for (int i=0; i<SIZE; i++) {
			arr.get().init();
		}
		
		end = System.currentTimeMillis();
		long dynamicAdditionScore = end - start;
		System.out.println("Dynamic addition(1st round): " + dynamicAdditionScore);
		
		start = System.currentTimeMillis();
		DataElem []sorted = arr.sort(true);
		end = System.currentTimeMillis();
		long dynamicSortScore = end - start;
		
		System.out.println("Dynamic sort(1st round): " + dynamicSortScore);
		System.out.println("Dynamic Total(1st round): " + (dynamicAdditionScore + dynamicSortScore));
		System.out.println();
		
		start = System.currentTimeMillis();
		
		for (int i=0; i<SIZE; i++) {
			arr.get().init();
		}
		
		end = System.currentTimeMillis();
		dynamicAdditionScore = end - start;
		System.out.println("Dynamic addition(2nd round): " + dynamicAdditionScore);
		
		start = System.currentTimeMillis();
		sorted = arr.sort(true);
		end = System.currentTimeMillis();
		dynamicSortScore = end - start;
		
		System.out.println("Dynamic sort(2nd round): " + dynamicSortScore);
		System.out.println("Dynamic Total(2nd round): " + (dynamicAdditionScore + dynamicSortScore));
		System.out.println();
		
		System.out.println("Raw, Sort Output Sampling:");
		for (int i=0; i<SIZE; i++) {
			if (i % (SIZE / 20) == 0) {
				System.out.println(i + ", " + sorted[i].toString());
			}
		}

		System.out.println();
		System.out.println("Dynamic, Sort Output Sampling:");
		for (int i=0; i<SIZE+10; i++) {
			if (i % (SIZE / 20) == 0) {
				System.out.println(i + ", " + sorted[i].toString());
			}
		}
	}
}
