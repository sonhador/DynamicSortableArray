import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public class DynamicSortableArray<T extends Comparable<T>> {
	private int size;
	private Class<T> objClass;
	private PartitionManager<T> partitionManager;
	
	public DynamicSortableArray(int size, Class<T> objClass) throws InstantiationException, IllegalAccessException {
		this.size = size;
		this.objClass = objClass;
		partitionManager = new PartitionManager<T>(size, objClass);
	}
	
	public T get() throws InstantiationException, IllegalAccessException {
		return partitionManager.get();
	}
	
	public T get(long idx) {
		return partitionManager.get(idx);
	}
	
	public T[] sort() {
		T []totalArr = (T[]) Array.newInstance(objClass, partitionManager.getPartitions().size()*size);
		
		for  (int i=0; i<partitionManager.getPartitions().size(); i++) {
			System.arraycopy(partitionManager.getPartitions().get(i).getArr(), 0, totalArr, i*size, size);
		}
		
		Arrays.parallelSort(totalArr, new DataElemComparator<T>());
		
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
		
		public T get(long idx) {
			int partitionNum = getPartition(idx);
			
			if (partitions.size() > partitionNum) {
				return partitions.get(partitionNum).get(getIdx(idx));
			} else {
				throw new RuntimeException("Range out of bound!!");
			}
		}
		
		public T get() throws InstantiationException, IllegalAccessException {
			
			int partitionNum = getPartition(idx);

			if (partitions.size() <= partitionNum) {
				int partitionIncrAmnt = partitions.size()*3/2;
				
				for (int i=0; i<partitionIncrAmnt; i++) {
					partitions.add(new Partition<T>(size, objClass));
				}
			}

			idx++;
			
			return partitions.get(partitionNum).get();
		}
		
		public List<Partition<T>> getPartitions() {
			return partitions;
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
