import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

public class SleepPatternMonitor 
{

	public static void main(String[] args) 
	{
		SleepMonitorController controller = new SleepMonitorController();
		controller.init();
		controller.runMonitor();
	}
}

class SleepMonitorController
{
	TimeMonitor monitor;
	GregorianCalendar sleepTime;
	GregorianCalendar alarmTime;
	ClockFrame fr;
	ClockTask task;
	Thread clock;
	
	public void init()
	{
		monitor = new TimeMonitor();
		IObserver ob1 = new YellowAlert();
		IObserver ob2 = new RedAlert();
		IObserver ob3 = new Alarm();
		monitor.addEventListener(ob1);
		monitor.addEventListener(ob2);
		monitor.addEventListener(ob3);
		sleepTime = new GregorianCalendar();
		sleepTime.add(GregorianCalendar.HOUR, 2);
		alarmTime = new GregorianCalendar();
		alarmTime.add(GregorianCalendar.HOUR, 10);
		fr = new ClockFrame(sleepTime, alarmTime);
		task = new ClockTask();
		clock = new Thread(task);
		fr.setThread(clock);
	}
	
	public void runMonitor()
	{
		clock.start();
	}
	
	class ClockTask implements Runnable
	{

		@Override
		public void run() 
		{
			while(true)
			{
				GregorianCalendar currentTime = new GregorianCalendar();
				TimeEvent now = new TimeEvent(alarmTime, currentTime, sleepTime, fr);
				monitor.setEvent(now);
				try{
					Thread.sleep(300000);
				}
				catch(InterruptedException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
	}
}

class ClockFrame extends JFrame
{
	Thread clock;
	ClockPanel pnl;
	GregorianCalendar alarmTime;
	GregorianCalendar sleepTime;
	
	public ClockFrame(GregorianCalendar alarmTime, GregorianCalendar sleepTime)
	{
		this.alarmTime = alarmTime;
		this.sleepTime = sleepTime;
		this.setSize(600, 600);
		this.setLocation(300, 100);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				clock.stop();
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		pnl = new ClockPanel();
		this.add(pnl);
		this.setVisible(true);
	}
	
	public void updateText(String msg)
	{
		pnl.updateWarning(msg);
	}
	
	public void setThread(Thread t)
	{
		clock = t;
	}
	
	class ClockPanel extends JPanel
	{
		JTextField timeEntry;
		JTextField warning;
		JPanel btnPanel;
		
		public ClockPanel()
		{
			super();
			this.setBackground(Color.white);
			this.setLayout(new BorderLayout(2, 2));
			timeEntry = new JTextField();
			warning = new JTextField();
			warning.setVisible(false);
			this.add(timeEntry, BorderLayout.CENTER);
			this.add(warning, BorderLayout.NORTH);
			btnPanel = new JPanel();
			JButton btnUpdateWarning = new JButton("Update Sleep Time");
			btnUpdateWarning.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					DateFormat df = new SimpleDateFormat("hh:mm dd/MM/yy");
					try
					{
					sleepTime.setTime(df.parse(timeEntry.getText()));
					}
					catch(ParseException pe)
					{
						GregorianCalendar now = new GregorianCalendar();
						now.add(GregorianCalendar.HOUR, 2);
						sleepTime = now;
					}
				}
			});
			JButton btnAlarmSet = new JButton("Update Alarm Time");
			btnUpdateWarning.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					DateFormat df = new SimpleDateFormat("hh:mm dd/MM/yy");
					try
					{
					alarmTime.setTime(df.parse(timeEntry.getText()));
					}
					catch(ParseException pe)
					{
						GregorianCalendar now = new GregorianCalendar();
						now.add(GregorianCalendar.HOUR, 2);
						alarmTime = now;
					}
				}
			});
			btnPanel.add(btnAlarmSet);
			btnPanel.add(btnUpdateWarning);
			this.add(btnPanel, BorderLayout.SOUTH);
			this.setVisible(true);
		}
		
		public void updateWarning(String msg)
		{
			warning.setText(msg);
			if(msg.equals("You should go to bed soon."))
				warning.setForeground(Color.yellow);
			else
				warning.setForeground(Color.red);
			warning.setVisible(true);
		}
	}
}