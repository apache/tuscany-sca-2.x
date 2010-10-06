package sample;

import org.oasisopen.sca.annotation.Service;

@Service(ShareService.class)
public class ShareServiceImpl implements ShareService {

	@Override
	public String shareName(String firstName, String lastName) {
		return firstName + " " + lastName;
	}

	@Override
	public int shareAge(int age) {
		return age;
	}

	@Override
	public Location shareLocation(Location location) {
		return location;
	}

}
