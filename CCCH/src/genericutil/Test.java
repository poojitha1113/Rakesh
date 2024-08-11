package genericutil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class Test {
	public static void main(String[] args) {
		List<User> userList = new ArrayList<User>();
		IntStream.range(0, 10).forEach(i -> {
			User u = new User();
			u.setAge(new Random().nextInt(5));
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, i);
			u.setCreateTime(c.getTime());
			u.setUsername(UUID.randomUUID().toString());
			userList.add(u);
		});
		userList.forEach(System.out::println);
		SortUtils.sort(userList, "age", "-create_time");
		System.out.println("**************************");
		userList.forEach(System.out::println);
	}
}
