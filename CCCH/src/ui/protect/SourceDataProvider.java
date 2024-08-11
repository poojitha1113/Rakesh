package ui.protect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;

import InvokerServer.SPOGServer;
import ui.filter.constants.ConnectionStatus;
import ui.filter.constants.JobStatus;
import ui.filter.constants.OS;
import ui.filter.constants.ProtectionStatus;

public class SourceDataProvider{

	public static int getRandom(int length) {

		return new Random().nextInt(length);
	}

	public static String getFilter(String filterName, Class<?> className)  {

		String filter = filterName;
		Field[] allFields = className.getFields();
		String[] filterValues = new String[allFields.length];

		for (int i = 0; i < allFields.length; i++) {
			Object filterVar = allFields[i].getName();

			try {

				filterValues[i] = allFields[i].get(filterVar).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}	

		}

		int fc = new Random().nextInt(filterValues.length);

		ArrayList<String> filters = new ArrayList<>();
		for (int i = 0; i <= fc; i++) {
			filters.add(filterValues[getRandom(filterValues.length)]);
		}

		filters = removeDuplicates(filters);
		
		for (int i = 0; i < filters.size(); i++) {
			if (!filter.contains(";")) {
				filter += ";"+filters.get(i);
			}else {
				filter += "|"+filters.get(i);
			}
		}

		return filter;		
	}

	public static String getUpdatedFitler(String oldFilter, String newFilter) {

		String[] oldIndividualFilters = oldFilter.split(";")[1].split("\\|");
		String[] newIndividualFilters = newFilter.split(";")[1].split("\\|");

		for (int i = 0; i < oldIndividualFilters.length; i++) {
			boolean flag = false;
			for (int j = 0; j < newIndividualFilters.length; j++) {

				if (newIndividualFilters[j].equalsIgnoreCase(oldIndividualFilters[i])) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				newFilter += "|"+oldIndividualFilters[i];
			}

		}

		return newFilter;
	}
	
	private static ArrayList<String> removeDuplicates(ArrayList<String> list){
		
		Set<String> withoutDuplicates = new LinkedHashSet<>(list);
		list.clear();
		list.addAll(withoutDuplicates);
		
		return list;
	}
	
	@DataProvider(name="manageSaveSearchInfo")
	public static Object[][] data() {
		
		int testCasesCount = 10;
		
		Class<?> classType = null;
		String[] allFilters = new String[]{"Protection Status", "Connection Status", "Job Status", "OS"};
		ArrayList<String> selectedFilters = new ArrayList<>();
			
		Object[][] data1 = new Object[testCasesCount][];
		
		for (int i = 0; i < testCasesCount; i++) {
			
			String uiFilter = null;
			String apiFilter = null;
			int filterCount = getRandom(allFilters.length)+1;
			for (int k = 0; k < filterCount; k++) {
				selectedFilters.add(allFilters[new Random().nextInt(allFilters.length)]);
			}
			selectedFilters = removeDuplicates(selectedFilters);
			
			for (int j = 0; j < selectedFilters.size(); j++) {
					
				switch (selectedFilters.get(j)) {
					
					case "Protection Status": classType = ProtectionStatus.class;
						break;
					
					case "Connection Status": classType = ConnectionStatus.class;
						break;
					
					case "Job Status": classType = JobStatus.class;
						break;
					
					case "OS": classType = OS.class;
						break;

					default: System.out.println("Class type not matched.");
						break;
				}
					
				if (uiFilter == null) {
					uiFilter= getFilter(selectedFilters.get(j), classType);	
				}else {
					uiFilter+= ","+getFilter(selectedFilters.get(j), classType);	
				}			
			}
		
			apiFilter = uiFilter.replace(";", ";=;").replace(" ", "_").toLowerCase();
			
			//Update filter
			String uiNewFilter = null;
			String apiNewFilter = null;
			filterCount = getRandom(allFilters.length)+1;
			
			selectedFilters.clear();
			
			for (int k = 0; k < filterCount; k++) {
				selectedFilters.add(allFilters[new Random().nextInt(allFilters.length)]);
			}
			selectedFilters = removeDuplicates(selectedFilters);
			
			for (int j = 0; j < selectedFilters.size(); j++) {
					
				switch (selectedFilters.get(j)) {
					
					case "Protection Status": classType = ProtectionStatus.class;
						break;
					
					case "Connection Status": classType = ConnectionStatus.class;
						break;
					
					case "Job Status": classType = JobStatus.class;
						break;
					
					case "OS": classType = OS.class;
						break;

					default: System.out.println("Class type not matched.");
						break;
				}
					
				if (uiNewFilter == null) {
					uiNewFilter= getFilter(selectedFilters.get(j), classType);	
				}else {
					uiNewFilter+= ","+getFilter(selectedFilters.get(j), classType);	
				}			
			}
		
			apiNewFilter = uiNewFilter.replace(";", ";=;").replace(" ", "_").toLowerCase();
			
			data1[i] = new Object[] {"Create save search with filters: "+uiFilter+" and update the filters values to: "+uiNewFilter,
						ReturnRandom("search"), ReturnRandom("search"), null, false, 
						uiFilter, apiFilter, uiNewFilter, apiNewFilter};
				
		}
		
		System.out.println(data1.toString());
		
		return data1;
	}
	
	public static String ReturnRandom(String baseString) {

	    if (baseString == "") {
	      return baseString;
	    } else {
	      return baseString + RandomStringUtils.randomAlphanumeric(4);
	    }
	}
}
