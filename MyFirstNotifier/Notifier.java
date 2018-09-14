import java.awt.SystemTray;
import java.awt.*;
import javax.swing.ImageIcon;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Random;
/*
import com.teamdev.jxbrowser.chromium.BrowserFactory;
import com.teamdev.jxbrowser.chromium.Browser;
*/
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


class Notifier{
	// because non static cant be referenced from static context or whatever
	public static TrayIcon ti;
	public static int waterUptilNow = 0;
	public static int target = NotifierConstants.TARGET_WATER_IN_ML;

	class DesktopNotifier extends TimerTask{
		Timer timer;
		//Browser browser;
		/*DesktopNotifier(Timer pTimer, Browser pBrowser){
				this.timer = pTimer;
				this.browser = pBrowser;
		}*/

		DesktopNotifier(Timer pTimer){
				this.timer = pTimer;
		}

		@Override
		public void run(){
			//only need the display message part to be repititive, dont need to create 
			//and initialize variables again and again 
			ImageIcon ws = null;

			JFrame frame = new JFrame();		
			JTextArea jt = new JTextArea("");
			String toBeDisplayed="";
			File dir = new File(".");
			File[] motivationalFiles  = dir.listFiles((d,fname)->(fname.endsWith(".txt") && fname.startsWith("Mot")));
			int rnd = new Random().nextInt(motivationalFiles.length);
			String fName = motivationalFiles[rnd].getName();
			try{
				BufferedReader bwr;
				FileReader fr = new FileReader(fName);
				bwr = new BufferedReader(fr);
				String line="";
				while((line = bwr.readLine())!=null){
					toBeDisplayed+=line;
					toBeDisplayed+="\n";
				}
			}catch(IOException e){}
			jt.append(toBeDisplayed);
			jt.setVisible(true);
			//frame.add(this.browser.getView().getComponent());
			try{
				ws = new ImageIcon(ImageIO.read(new File("Mario.jpg")));
			}
			catch(IOException e){}
			JOptionPane.showMessageDialog(frame,jt);
			String waterDrank = (String)JOptionPane.showInputDialog(frame, "Input water drank (ml.)","This is the water mario GRRRRR!!",JOptionPane.PLAIN_MESSAGE, ws, null, null);
			if(waterDrank == null || !waterDrank.matches("[-+]?\\d*\\.?\\d+")) return;
			waterUptilNow += Integer.parseInt(waterDrank);
			if (waterUptilNow >= target) {
				try{
				BufferedWriter bw;
				FileWriter fw = new FileWriter("Water.txt");
				bw = new BufferedWriter(fw);
				bw.newLine();
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();
				bw.write(df.format(date) + " -- " + "Target achieved");
				bw.close();
				}
				catch(Exception e){}
				ti.displayMessage("BRAVO!!", "YOU DID IT !!", TrayIcon.MessageType.INFO);
				this.timer.cancel(); 
				System.exit(0);
			}
			ti.displayMessage( String.format("%d",target - waterUptilNow) + " ml left to be drunk...", NotifierConstants.TEXT, TrayIcon.MessageType.WARNING);
		}
	}

	public static void main(String args[]){
		if(SystemTray.isSupported()){
			SystemTray dummy = SystemTray.getSystemTray();
			//SUGG: can move this to a constants file
			ImageIcon ws = new ImageIcon("Mario.jpg");
			Image img = ws.getImage();
			ti = new TrayIcon(img);
			try{
			dummy.add(ti);
			}
			catch(AWTException e){
				System.out.println(e);
			}
			//Browser browser = BrowserFactory.create();

			Timer timer = new Timer();
			timer.schedule(new Notifier().new DesktopNotifier(timer), NotifierConstants.INITIAL_DELAY, NotifierConstants.REPITITION_PERIOD);

		}
	}
}