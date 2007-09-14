package org.apache.tuscany.sca.itest;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface MyListServiceByYear {
	String[] getHolidays(int year);
}
