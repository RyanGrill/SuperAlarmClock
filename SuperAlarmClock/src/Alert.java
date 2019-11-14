import java.awt.Toolkit;
import java.text.DateFormat;
import java.util.*;

interface IObserver
{
	void alert(TimeEvent e);
}

class TimeMonitor
{
	private ArrayList<IObserver> observerList;
	TimeEvent event;
	
	public TimeMonitor()
	{
		observerList = new ArrayList<IObserver>();
	}
	
	public void addEventListener(IObserver observer)
	{
		observerList.add(observer);
	}
	
	public void removeEventListener(IObserver observer)
	{
		observerList.remove(observer);
	}
	
	public void setEvent(TimeEvent e)
	{
		this.event = e;
		informListeners();
	}
	
	public TimeEvent getEvent()
	{
		return event;
	}

	private void informListeners() {
		for (IObserver item: observerList)
		{
			item.alert(getEvent());
		}
	}
}

class TimeEvent 
{
	GregorianCalendar alarmTime;
	GregorianCalendar sleepTime;
	GregorianCalendar time;
	ClockFrame fr;
	
	public TimeEvent(GregorianCalendar alarmTime, GregorianCalendar time, GregorianCalendar sleepTime, ClockFrame fr)
	{
		this.alarmTime = alarmTime;
		this.time = time;
		this.sleepTime = sleepTime;
		this.fr = fr;
	}
	
	public void setAlarmTime(GregorianCalendar alarmTime)
	{
		this.alarmTime = alarmTime;
	}
	
	public void setTime(GregorianCalendar time)
	{
		this.time = time;
	}
	
	public void setSleepTime(GregorianCalendar sleepTime)
	{
		this.sleepTime = sleepTime;
	}
}


class YellowAlert implements IObserver
{

	@Override
	public void alert(TimeEvent e) 
	{
		String msg = "";
		GregorianCalendar warningTime = e.sleepTime;
		warningTime.add(GregorianCalendar.HOUR, -1);
		GregorianCalendar redAlertTime = e.sleepTime;
		if(e.time.compareTo(warningTime) >= 0 && e.time.compareTo(redAlertTime) < 0)
			msg += "You should go to bed soon.";
		e.fr.updateText(msg);
	}
	
}

class RedAlert implements IObserver
{

	@Override
	public void alert(TimeEvent e) 
	{
		String msg = "";
		GregorianCalendar redAlertTime = e.sleepTime;
		if(e.time.compareTo(redAlertTime) >= 0)
			msg += "You need to go to bed now!";
		e.fr.updateText(msg);
	}
	
}

class Alarm implements IObserver
{

	@Override
	public void alert(TimeEvent e) 
	{
		String msg = "";
		if(e.time.compareTo(e.alarmTime) >= 0)
			msg += "Time to wake up.";
		e.fr.updateText(msg);
	}
	
}
