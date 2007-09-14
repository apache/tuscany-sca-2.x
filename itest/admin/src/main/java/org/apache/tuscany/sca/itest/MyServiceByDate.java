package org.apache.tuscany.sca.itest;

import java.util.Date;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface MyServiceByDate {
	Date nextHoliday(Date date);
}
