package org.apache.tuscany.sca.itest;

import java.util.Date;

public interface MyService  extends SCAComponentService{
    Date nextHoliday();
    String getLocation();
    String getYear();
}
