# MaxSysLibs

calendarlib - a simple Date Picker. Usage sample:

	CalendarDialog dlg = new CalendarDialog(this, "en");
	Calendar ca = dlg.getCalendar();
	if (ca != null) {
		System.out.println("ca = " + ca.getTime());
	} else {
		System.out.println("null!");
	}
