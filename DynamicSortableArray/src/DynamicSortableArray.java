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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;

public class DynamicSortableArray<T extends Comparable<T>> {
	private int size;
	private Class<T> objClass;
	private Comparator<T> comparator;
	private PartitionManager<T> partitionManager;
	
	public DynamicSortableArray(int size, Class<T> objClass, Comparator<T> comparator) throws InstantiationException, IllegalAccessException {
		this.size = size;
		this.objClass = objClass;
		this.comparator = comparator;
		partitionManager = new PartitionManager<T>(size, objClass);
	}
	
	public T get() throws InstantiationException, IllegalAccessException {
		return partitionManager.get();
	}
	
	public T get(long idx) throws InstantiationException, IllegalAccessException {
		return partitionManager.get(idx);
	}
	
	public T[] sort(boolean withClear) {
		int partitionCnt = partitionManager.getPartitions().size();
		T []totalArr = (T[]) Array.newInstance(objClass, partitionCnt*size);
		
		Iterator<Partition<T>> itr = partitionManager.getPartitions().iterator();
		int idx=0;
		while (itr.hasNext()) {
			System.arraycopy(itr.next().getArr(), 0, totalArr, idx*size, size);

			if (withClear) {
				itr.remove();
			}
			
			idx++;
		}
		
		if (withClear) {
			partitionManager.initIdx();
		}
		
		Arrays.parallelSort(totalArr, comparator);
		
		return totalArr;
	}
	
	private class PartitionManager<T extends Comparable<T>> {
		private int size;
		private long idx = 0;
		private List<Partition<T>> partitions;
		private Class<T> objClass;
		
		public PartitionManager(int size, Class<T> objClass) throws InstantiationException, IllegalAccessException {
			this.size = size;
			this.objClass = objClass;
			
			partitions = new ArrayList<Partition<T>>();
			
			init(size, objClass);
		}
		
		private void init(int size, Class<T> objClass) throws InstantiationException, IllegalAccessException {
			partitions.add(new Partition<T>(size, objClass));
		}
		
		private int getPartition(long idx) {
			return (int)idx / size;
		}
		
		private int getIdx(long idx) {
			return (int)idx % size;
		}
		
		private void increasePartitionAsNecessary(long idx) throws InstantiationException, IllegalAccessException {
			int partitionNum = getPartition(idx);
			int partitionCnt = partitions.size();

			if (partitionCnt == 0 || partitionCnt <= partitionNum) {
				if (partitionCnt == 0) {
					partitionCnt = 1;
				}
				
				int partitionIncrAmnt = partitionCnt*3/2;
				
				for (int i=0; i<partitionIncrAmnt; i++) {
					partitions.add(new Partition<T>(size, objClass));
				}
			}
		}
		
		public T get(long idx) throws InstantiationException, IllegalAccessException {
			increasePartitionAsNecessary(idx);
			
			return partitions.get(getPartition(idx)).get(getIdx(idx));
		}
		
		public T get() throws InstantiationException, IllegalAccessException {
			increasePartitionAsNecessary(this.idx);

			return partitions.get(getPartition(this.idx++)).get();
		}
		
		public List<Partition<T>> getPartitions() {
			return partitions;
		}
		
		public void initIdx() {
			idx = 0;
		}
	}

	private class Partition<T extends Comparable<T>> {
		private int idx = 0;
		private T []arr;
		
		public Partition(int size, Class<T> objClass) throws InstantiationException, IllegalAccessException {
			arr = (T[]) Array.newInstance(objClass, size);
			init(arr, size, objClass);
		}
		
		private void init(T []arr, int size, Class<T> objClass) throws InstantiationException, IllegalAccessException {
			Arrays.setAll(arr, new IntFunction<T>() {
				@Override
				public T apply(int value) {
					try {
						return objClass.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					
					return null;
				}
			});
		}
		
		public T get(int idx) {
			if (idx < 0) {
				return null;
			}
			
			return arr[idx];
		}
		
		public T get() {
			return arr[idx++];
		}
		
		public T[] getArr() {
			return arr;
		}
	}
}
