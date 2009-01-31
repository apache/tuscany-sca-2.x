package org.apache.tuscany.sca.itest.admin;

import java.util.Date;

import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface MyServiceByDate {
	Date nextHoliday(Date date);
}
