# MaxSysLibs

Usage of calendarlib:

	CalendarDialog dlg = new CalendarDialog(this, "en");
        Calendar ca = dlg.getCalendar();
        if (ca != null) {
            System.out.println("ca = " + ca.getTime());
        } else {
            System.out.println("null!");
        }
