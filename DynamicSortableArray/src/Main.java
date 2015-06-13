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
		
		/////////////////////////////////////////////////////////////////////////////////////////////
		
		start = System.currentTimeMillis();
		
		DynamicSortableArray<DataElem> arr = new DynamicSortableArray<DataElem>(20, DataElem.class);
		
		for (int i=0; i<SIZE; i++) {
			arr.get().init();
		}
		
		end = System.currentTimeMillis();
		
		long dynamicAdditionScore = end - start;
		
		System.out.println("Dynamic addition: " + dynamicAdditionScore);
		
		start = System.currentTimeMillis();
		
		DataElem []sorted = arr.sort();
		
		end = System.currentTimeMillis();
		
		long dynamicSortScore = end - start;
		
		System.out.println("Dynamic sort: " + dynamicSortScore);
		
		System.out.println("Dynamic Total: " + (dynamicAdditionScore + dynamicSortScore));
		
//		String prev = "";
//		for (int i=0; i<SIZE; i++) {
//			if (prev.equals(list.get(i).toString()) == false) {
//				System.out.println(i + ", " + list.get(i).toString());
//			}
//			prev = list.get(i).toString();
//		}
//		
//		String prev = "";
//		for (int i=0; i<SIZE+10; i++) {
//			if (prev.equals(sorted[i].toString()) == false) {
//				System.out.println(i + ", " + sorted[i].toString());
//			}
//			prev = sorted[i].toString();
////			System.out.println(i + ", " + sorted[i].toString());
//		}
	}
}
