package org.apache.tuscany.sca.itest.admin;

import java.util.Date;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface MyServiceByDate {
	Date nextHoliday(Date date);
}
