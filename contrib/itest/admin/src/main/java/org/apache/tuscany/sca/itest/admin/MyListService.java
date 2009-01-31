package org.apache.tuscany.sca.itest.admin;

import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface MyListService {
	String[] getHolidays();
    String getYear();
}
