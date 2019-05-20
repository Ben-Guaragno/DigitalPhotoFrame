package com.voidStudios.photoDisplay;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class View extends JFrame {
	
	//No clue what this is
	private static final long serialVersionUID=7285388711508726488L;
	
	GraphicsDevice device;
	DataController data;
	private HashMap<String,File> icons;
	
	public View(DataController data) {
		this.data=data;
		icons=data.getIcons();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(Color.black);
        
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setCursor(null);
        contentPane.setOpaque(false);

        JPanel panel = new JPanel() {
			//Again, confused
			private static final long serialVersionUID=-8590455995919752197L;
			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawScreen((Graphics2D) g);
            }
        };
        panel.setBackground(Color.black);
        panel.setOpaque(true);
        contentPane.add(panel);
        
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        device.setFullScreenWindow(this);

        //Cursor Blanking
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        getContentPane().setCursor(blankCursor);
	}
	
	private synchronized void drawScreen(Graphics2D g) {
		//draw all that shit
		Rectangle rect=getBounds();
		
		//Some rendering stuff
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 100);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE); 
		
		//Draws the picture
		try {
			BufferedImage pic=null;
			if(data.getFile()!=null) {
				pic=ImageIO.read(data.getFile());
				drawCentered(g, pic);
//				System.out.println(pic+", "+data.getFile());
			}else {
				g.setFont(new Font("Comic Sans MS", Font.PLAIN, 60));
				g.setColor(Color.white);
				FontMetrics m=g.getFontMetrics();
				g.drawString("No valid photos!", rect.width/2-m.stringWidth("No valid photos!")/2, rect.height/2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Retrieve offset stats for date
		g.setFont(new Font("Times New Roman", Font.PLAIN, 60));
		int[] offset=rightAligned(data.getDate(), g);
		//Transparent date border
		g.setColor(new Color(0f, 0f, 0f, .4f));
		g.fillRect(rect.width-offset[0]-20, 0, rect.width, offset[2]);
		//Transparent weather background
		
		//Need to get longest weather string
		int hourOffset=0;
		if(data.getWeatherDay()!=null) {
			hourOffset=getHourOffset(g);

			g.fillRect(0, 0, 335, 95);							//Daily
			g.fillRect(0, 95, 120+hourOffset, 385-95-57);		//Hourly
		}
		
		//Draw date
		g.setColor(Color.white);
		g.drawString(data.getDate(), rect.width-offset[0]-10, offset[1]);
		
		//Draw weather
		if(data.getWeatherDay()!=null)
			drawWeather(g);
	}
	
	public void dispose() {
		device.setFullScreenWindow(null);
        super.dispose();
    }
	//centered and scaled
	private void drawCentered(Graphics2D g, BufferedImage pic) {
		Rectangle rect=getBounds();
		
	    int height=rect.height, width=rect.width;
		int w=pic.getWidth();
		int h=pic.getHeight();
		width=(int)(w/(h/(double)height));
		if(width>1920){
			width=1920;
			height=(int)(h/(w/(double)width));
		}
		BufferedImage scalePic=progressiveResize(pic, width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    
		int drawW=(int)(rect.getWidth()/2-width/2);
		int drawH=(int)(rect.getHeight()/2-height/2);
		
		g.drawImage(scalePic, drawW, drawH, null);
	}
	private static BufferedImage progressiveResize(BufferedImage source,
            int width, int height, Object hint) {
        int w = Math.max(source.getWidth()/2, width);
        int h = Math.max(source.getHeight()/2, height);
        BufferedImage img = commonResize(source, w, h, hint);
        while (w != width || h != height) {
            BufferedImage prev = img;
            w = Math.max(w/2, width);
            h = Math.max(h/2, height);
            img = commonResize(prev, w, h, hint);
            prev.flush();
        }
        return img;
    }
	private static BufferedImage commonResize(BufferedImage source,
            int width, int height, Object hint) {
        BufferedImage img = new BufferedImage(width, height,
                source.getType());
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g.drawImage(source, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return img;
    }
	
	
	private int[] rightAligned(String s,Graphics g) {
		int[] stringLength=new int[3];
		FontMetrics metrics=g.getFontMetrics();
		stringLength[0]=metrics.stringWidth(s);
		stringLength[1]=metrics.getAscent();
		stringLength[2]=metrics.getHeight();
		
		return stringLength;
	}
	
	//Begin Weather drawing
	private void drawWeather(Graphics2D g){
//		Hashtable<String, String> weather=data.getWeatherDay().get(0);
		g.setColor(Color.white);
		
		//Test call for icons
//		try {
//			for(int i=0;i<10;i++)
//				drawWeatherDayTest(g, i);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		try {
			for(int i=0;i<3;i++)
				drawWeatherDay(g, i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		g.setFont(new Font("Calibri", Font.PLAIN, 30));
		g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		
//		String sumString=weather.get("summary").substring(1);
//		sumString=sumString.substring(0, sumString.length()-2);
		
		String sumString=data.getWeatherSummary();
		
		if(!sumString.equals("")) {
			Rectangle rect=getBounds();

			g.setColor(new Color(0f, 0f, 0f, .4f));
			FontMetrics m=g.getFontMetrics();
			g.fillRect(0, rect.height-(m.getHeight()+5), m.stringWidth(sumString)+10, m.getHeight()+5);

			g.setColor(Color.white);
			g.drawString(sumString, 5, rect.height-11);
		}
		try {
			for(int i=0;i<6;i++)
				drawWeatherHour(g, i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Hourly forecast
	private void drawWeatherHour(Graphics2D g, int hourNum) throws IOException {
		Hashtable<String, String> weather=data.getWeatherHour().get(hourNum);
		//Fix the time
		String time=null;
		try {
			String timeGMT=weather.get("time")+" "+"GMT";
			SimpleDateFormat sdf =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
			TimeZone tz = TimeZone.getDefault();
			sdf.setTimeZone(tz);
			Date date=null;
			date=sdf.parse(timeGMT);
			sdf = new SimpleDateFormat("h a");
			time = sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		int hourOffset=40*hourNum+120;
		int indent=85;
		
		int[] alignInf=rightAligned(time, g);
		g.drawString(time, indent-5-alignInf[0], hourOffset);
		
		
//		g.setColor(Color.BLACK);
//		g.fillRect(indent, 150+hourOffset, 25, 25);
		
		File hourIconF=icons.get(weather.get("icon"));
		BufferedImage hourIcon=ImageIO.read(hourIconF);
		hourIcon=resize(hourIcon, 50, 50);
		g.drawImage(hourIcon, indent-12, -35+hourOffset+2, null);
		
		g.setColor(Color.white);
		
		
		
		g.setFont(new Font("Calibri", Font.PLAIN, 25));
		String s=weather.get("apparentTemperature")+" "+weather.get("summary");
		g.drawString(s, indent+30, hourOffset);
	}
	private int getHourOffset(Graphics2D g) {
		int length=0;
		for (int i=0; i<6; i++) {
			Hashtable<String, String> weather=data.getWeatherHour().get(i);
			String s=weather.get("apparentTemperature")+" "+weather.get("summary");
			FontMetrics m=g.getFontMetrics(new Font("Calibri", Font.PLAIN, 25));
			int x=m.stringWidth(s);
			if(x>length)
				length=x;
		}
		return length;
	}
	//Daily forecast
	private void drawWeatherDay(Graphics2D g, int dayNum) throws IOException {
		Hashtable<String, String> weather=data.getWeatherDay().get(dayNum);
		int dayOffset=110*dayNum;
		g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		
		//One day
		
		
//		g.setColor(Color.black);
//		g.fillRect(10+dayOffset, 10, 50, 50);
		g.setColor(Color.white);
		
		File dayIconF=icons.get(weather.get("icon"));
		//BufferedImage dayIcon=resize(ImageIO.read(dayIconF),50,50);
		BufferedImage dayIcon=ImageIO.read(dayIconF);
		g.drawImage(dayIcon, -15+dayOffset, -15, null);
		
		FontMetrics metrics=g.getFontMetrics();
		int tempH=metrics.getAscent();
		
		g.drawString(weather.get("apparentTemperatureHigh"), 67+dayOffset, tempH+3);
		g.drawString(weather.get("apparentTemperatureLow"), 67+dayOffset, (tempH)*2+3);
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		g.drawString(data.getDayOfWeek(Integer.parseInt(data.getDay())-1+dayNum), 10+dayOffset, 85);
	}
	
	//Test method for icons
//	private void drawWeatherDayTest(Graphics2D g, int dayNum) throws IOException {
//		Hashtable<String, String> weather=data.getWeatherDay().get(0);
//		int dayOffset=110*dayNum;
//		g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
//		
//		g.setColor(Color.black);
//		g.fillRect(10+dayOffset, 10, 50, 50);
//		g.setColor(Color.white);
//		
////		File dayIconF=icons.get(weather.get("icon"));
//		Object[] icons=this.icons.keySet().toArray();
//		
//		File dayIconF=this.icons.get(icons[dayNum]);
//		BufferedImage dayIcon=ImageIO.read(dayIconF);
//		g.drawImage(dayIcon, -15+dayOffset, -20+5, null);
//		
//		FontMetrics metrics=g.getFontMetrics();
//		int tempH=metrics.getAscent();
//		
//		g.drawString(weather.get("apparentTemperatureHigh"), 67+dayOffset, tempH+3);
//		g.drawString(weather.get("apparentTemperatureLow"), 67+dayOffset, (tempH)*2+3);
//		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
//		g.drawString(data.getDayOfWeek(dayNum), 10+dayOffset, 85);
//	}
	
	
	
	private static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
//	private static BufferedImage colorImage(BufferedImage loadImg, int red, int green, int blue) {
//	    BufferedImage img = new BufferedImage(loadImg.getWidth(), loadImg.getHeight(),
//	        BufferedImage.TRANSLUCENT);
//	    Graphics2D graphics = img.createGraphics(); 
//	    Color newColor = new Color(red, green, blue, 0 /* alpha needs to be zero */);
//	    graphics.setXORMode(newColor);
//	    graphics.drawImage(loadImg, null, 0, 0);
//	    graphics.dispose();
//	    return img;
//	}
}
