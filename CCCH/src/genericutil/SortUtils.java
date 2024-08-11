package genericutil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class SortUtils {

	private static Map<String, String> fieldMapping;
	private static List<SortWrapper> sortList;
	
	/**
	 * Sorts the specified list according to the order of sorts 
	 * @param dataList the list to be sorted, must not be null or empty
	 * @param sorts the sort fields determine the order of the array, 
	 * 		  ascending order as default, to sort in descending order the 
	 * 		  sort field should start with hyphen, must not be null or empty
	 */
	@SuppressWarnings("unchecked")
	public static <T> void sort(final List<T> dataList, String... sorts) {
		if (dataList == null || dataList.isEmpty()) {
			throw new IllegalArgumentException("Data list must not be null or empty.");
		}
		if (sorts == null || sorts.length <= 0) {
			throw new IllegalArgumentException("Sorts must not be null or empty");
		}
		init(dataList.get(0).getClass(), sorts);
		Collections.sort(dataList, new InternalComparator<T>());
	}
	
	/**
	 * Sorts the specified array of objects according to the order of sorts 
	 * @param dataArray the array to be sorted, must not be null or empty
	 * @param sorts the sort fields determine the order of the array, 
	 * 		  ascending order as default, to sort in descending order the 
	 * 		  sort field should start with hyphen, must not be null or empty
	 */
	@SuppressWarnings("unchecked")
	public static <T> void sort(final T[] dataArray, String... sorts) {
		if (dataArray == null || dataArray.length <= 0) {
			throw new IllegalArgumentException("Data array must not be null or empty.");
		}
		if (sorts == null || sorts.length <= 0) {
			throw new IllegalArgumentException("Sorts must not be null or empty");
		}
		init(dataArray[0].getClass(), sorts);
		Arrays.sort(dataArray, new InternalComparator<T>());
	}
	
	private static void init(Class<?> clazz, String[] sorts) {
		initFieldMapping(clazz);
		initSorts(sorts);
	}
	
	private static void initSorts(String[] sorts) {
		sortList = new CopyOnWriteArrayList<>();
		sortList.addAll(Arrays.stream(sorts).filter(s -> {
			String name = s;
			if (name.startsWith("-")) {
				name = name.substring(1);
			}
			return fieldMapping.containsKey(name);
		}).map(s -> parseSort(s)).collect(Collectors.toList()));
	}
	
	private static SortWrapper parseSort(String sort) {
		String name = sort;
		boolean asc = true;
		if (name.startsWith("-")) {
			name = name.substring(1);
			asc = false;
		}
		return new SortWrapper(asc, name);
	}

	private static void initFieldMapping(Class<?> clazz) {
		fieldMapping = new HashMap<String, String>();
		Class<?> searchClass = clazz;
		while (Object.class != searchClass && searchClass != null) {
			Field[] fields = searchClass.getDeclaredFields();
			if (fields != null) {
				Arrays.stream(fields).forEach(field -> {
					String queryName = field.getName();
					SortString queryString = field.getDeclaredAnnotation(SortString.class);
					if (queryString != null) {
						queryName = queryString.value();
					}
					fieldMapping.put(queryName, field.getName());
				});
			}
			searchClass = searchClass.getSuperclass();
		}
	}

	private static class SortWrapper {
		private boolean asc;
		private String name;
		public SortWrapper(boolean asc, String name) {
			super();
			this.asc = asc;
			this.name = name;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static class InternalComparator<T> implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			Class<?> clazz = o1.getClass();
			for (SortWrapper sort : sortList) {
				String fieldName = fieldMapping.get(sort.name);
				try {
					Field field = clazz.getDeclaredField(fieldName);
					field.setAccessible(true);
					Object obj = field.get(o1);
					if (!(obj instanceof Comparable)) {
						continue;
					}
					int result = ((Comparable)obj).compareTo(field.get(o2));
					if (result != 0) {
						return sort.asc ? result : 0-result;
					}
				} catch (Exception e) {
					return 0;
				}
			}
			return 0;
		}
	}

}