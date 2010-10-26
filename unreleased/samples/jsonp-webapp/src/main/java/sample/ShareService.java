package sample;

import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface ShareService {

	String shareName(String firstName, String lastName);
	
	int shareAge(int age);
	
	Location shareLocation(Location location);
	
}
