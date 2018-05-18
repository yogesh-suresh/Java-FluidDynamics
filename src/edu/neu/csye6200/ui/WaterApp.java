package edu.neu.csye6200.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.neu.csye6200.fd.FluidSimulator;

/**
 * A Test application for the Wolfram Biological Growth application
 * @author MMUNSON
 */
public class WaterApp extends WApp implements ActionListener,WindowListener,Observer{

	private static Logger log = Logger.getLogger(WaterApp.class.getName());

	protected JPanel mainPanel = null;
	protected JPanel northPanel =null;
	protected static JButton startBtn;
	protected static JButton stopBtn;
	protected static JButton pauseBtn;
	protected JLabel  getCnt;
	protected static JTextField genCnt;
	protected static  JComboBox<String> rulebox;
	protected JLabel plateDisp;
	protected static JCheckBox plateBtn;
    public static BGCanvas bgPanel = null;
    private boolean pauseSet = true;
    private FluidSimulator ffSim = null;
    private Thread threadA = null;
    static int count=5;
    public static boolean isPlateON =false;
    public static boolean collisionRule =true; 
    
	
    /**
     * Sample app constructor
     */
    public WaterApp() {
    	System.out.println("Water CONS");
    	frame.setSize(600, 400); // initial Frame size
		frame.setTitle("WaterApp");
		
		menuMgr.createDefaultActions(); // Set up default menu items
		//ffSim = new FluidSimulator(count);
		//ffSim.addObserver(bgPanel);
    	showUI(); // Cause the Swing Dispatch thread to display the JFrame
    }
   
    /**
     * Create a main panel that will hold the bulk of our application display
     */
	@Override
	public JPanel getMainPanel() {
	
		mainPanel = new JPanel();
    	mainPanel.setLayout(new BorderLayout());
    	mainPanel.add(BorderLayout.NORTH, getNorthPanel());
    	pauseBtn.setEnabled(false);
    	stopBtn.setEnabled(false);
    	bgPanel = new BGCanvas();
    	mainPanel.add(BorderLayout.CENTER, bgPanel);
    	
    	return mainPanel;
	}
    
	/**
	 * Create a top panel that will hold control buttons
	 * @return
	 */
    public JPanel getNorthPanel() {
    	northPanel = new JPanel();
    	northPanel.setLayout(new FlowLayout());
    	
    	startBtn = new JButton("Start");
    	startBtn.addActionListener(this); // Allow the app to hear about button pushes
    	northPanel.add(startBtn);
    	
    	pauseBtn = new JButton("Pause/Resume"); // Allow the app to hear about button pushes
    	pauseBtn.addActionListener(this);
    	northPanel.add(pauseBtn);
    	
    	stopBtn = new JButton("Stop"); // Allow the app to hear about button pushes
    	stopBtn.addActionListener(this);
    	northPanel.add(stopBtn);
    	
    	getCnt=new JLabel("Generation count:"); 
        northPanel.add(getCnt); 
    	genCnt =new JTextField("10");
        northPanel.add(genCnt);
        plateDisp=new JLabel("Plate:"); 
        northPanel.add(plateDisp);
        
        //Plate CheckBox
        plateBtn = new JCheckBox("");
        northPanel.add(plateBtn);
        plateBtn.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				
				JCheckBox cb = (JCheckBox) e.getSource();
				if(cb.isSelected())
				{
					log.info("Plate tick");
					isPlateON=true;
				}	else
				{
					log.info("Plate no-tick");
					isPlateON=false;
				}
			}
		});
        //ComboBox
        rulebox = new JComboBox<String>();
        rulebox.addItem("Collision");
        rulebox.addItem("No Collision");
        rulebox.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				log.info("RuleBox selected");
				String ruleSelect = (String) rulebox.getSelectedItem();
				 if (ruleSelect.equals("Collision")){
			            System.out.println("Collision selected");
			            collisionRule=true;
				 } else if (ruleSelect.equals("No Collision")) {
			            System.out.println("No Collision selected");
			            collisionRule=false;
			}
			}
		});
        
        northPanel.add(rulebox);
        

    	return northPanel;
    }
    
    
	@Override
	synchronized public void actionPerformed(ActionEvent ae) {
		log.info("We received an ActionEvent " + ae);
		
		 count=Integer.parseInt(genCnt.getText());
		 if(count<=0) count = 20;

		if (ae.getSource() == startBtn) {
			System.out.println("Start pressed");
			ffSim = new FluidSimulator(count);
	    	threadA = new Thread(ffSim);
			threadA.start();
			notify();
			startBtn.setEnabled(false);
			genCnt.setEnabled(false);
			rulebox.setEnabled(false);
			plateBtn.setEnabled(false);
			pauseBtn.setEnabled(true);
			stopBtn.setEnabled(true);
			
			}
		else if (ae.getSource() == pauseBtn){
			if(!pauseSet) {
				System.out.println("resume pressed");
				pauseSet = true;
				FluidSimulator.resumeRun();
				//threadA.notify();
			}
			else {
				System.out.println("pause pressed");
				//threadA.suspend();
				FluidSimulator.pauseRun();
				pauseSet = false;
				}
			}
		else if (ae.getSource() == stopBtn) {
			
			System.out.println("Stop pressed");
			threadA.stop();
			startBtn.setEnabled(true);
			genCnt.setEnabled(true);
			rulebox.setEnabled(true);
			plateBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			FluidSimulator.xStart= 100 ;
			FluidSimulator.xEnd = 200;
			BGCanvas.offset= 0;
			FluidSimulator.resumeRun();
		}
			
	}

	@Override
	public void windowOpened(WindowEvent e) {
		log.info("Window opened");
	}

	@Override
	public void windowClosing(WindowEvent e) {	
		log.info("Window closing");
	}



	@Override
	public void windowClosed(WindowEvent e) {
		log.info("Window closed");
	}



	@Override
	public void windowIconified(WindowEvent e) {
		log.info("Window iconified");
	}



	@Override
	public void windowDeiconified(WindowEvent e) {	
		log.info("Window deiconified");
	}



	@Override
	public void windowActivated(WindowEvent e) {
		log.info("Window activated");
	}



	@Override
	public void windowDeactivated(WindowEvent e) {	
		log.info("Window deactivated");
	}
	
	/**
	 * Sample Wolf application starting point
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				log.info("Fliud SimulatorApp started");
				WaterApp wapp = new WaterApp();
				
			}
				
	});
	}

	 public static JFrame getFrame(){
	    	return frame;
	    }
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		bgPanel.repaint();
		//notify();
	}
	
	public static void reset()
	{
		startBtn.setEnabled(true);
		genCnt.setEnabled(true);
		rulebox.setEnabled(true);
		plateBtn.setEnabled(true);
		pauseBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		FluidSimulator.resumeRun();	
	}

}
