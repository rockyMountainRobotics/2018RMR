package org.usfirst.frc.team662.robot;

import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Imgproc;

import com.armabot.SweepJNI;
import com.armabot.SweepSample;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class Robot extends IterativeRobot 
{   
    //Makes an array of scan data.
    SweepSample[] scanData;
    
    //The port that will be our scanner
	long ourScanner = 0;
	
	//A string that will hold the data given to us by the FMS at the beginning of the game
	String gameData;
	
	UsbCamera jevois;
    
    //Creates a variable for states A, B, C, and D in Autonomous. These are used in the following
    //classes: Drive and BoxManip
    public static char autoState = '0';
    public static boolean state0Finished = false;
    public static String  driftSpeed = "";

    //Creates the gyro object. Initialization happens in robotInit
    public ADXRS450_Gyro gyro;
    
    //Doubles for the math that we calculated
    public final double GYRO_RIGHT_ANGLE = 16.24; /* TODO  : confirm angles - try 26.36*/
    public final double GYRO_LEFT_ANGLE = -20.13;  /* TODO confirm angles  - try -30.56*/
    public final double LIDAR_LEFT_DISTANCE = 358.895;
    public final double LIDAR_RIGHT_DISTANCE = 270; // This should be 339.35!!!! we changed it to the distance straight backfor testing
    
    public final static double DEADZONE = .15;
    
    //an array of components so we can easily call update() on different components
    Component[] parts = new Component[10];
    
    //Creates the controller for drive.
    public static XboxController driveController = new XboxController(0);
    public static XboxController manipController = new XboxController(1);

    public static DigitalInput autoSwitchA = new DigitalInput(RobotMap.AUTO_ENABLE_A);
    public static DigitalInput autoSwitchB = new DigitalInput(RobotMap.AUTO_ENABLE_B);
    
    public static DigitalInput chainTopLimit = new DigitalInput(RobotMap.LIMIT_TOP);
	public static DigitalInput chainMiddleLimit = new DigitalInput(RobotMap.LIMIT_MIDDLE);
	public static DigitalInput chainBottomLimit = new DigitalInput(RobotMap.LIMIT_BOTTOM);
	
    public static boolean ifScannerConnected = true;
    
  //Double variables that store the gyro's angle, the distance given by the lidar, the angle that
  	//the lidar is at, the previous distance (used for error checking), and the change in distance
  	double gyroAngle;
  	
  	double distance;
  	double lidarAngle;
  	
  	double previousDistance=3;
  	double distanceChange;
  	
  	double bigJump = 20;
  	int scanCount = 0;
  	
  	int chooseAuto = 1;
  	
  	public char firstCharOfData;
  	boolean gameDataAvailable = true;
  	
  	double turnA;
  	double distanceB;
  	
  	//Positions of various components in the array
  	int arrayPosCompressor = 0;
  	int arrayPosDrive = 1;
  	int arrayPosBoxManip = 2;
  	int arrayPosWinchManip = 3;
  	int arrayPosShifter = 4;
  	
  	CvSink cvSink;
  	CvSource outputStream;
  	Mat source;
  	Mat output;
  	
	@Override
	public void robotInit() 
	{
		
		//Add components to the array - currently we don't have the manipulator classes or the shifter
		parts[arrayPosCompressor] = new CompressorSwitch();
		parts[arrayPosDrive] = new Drive();
		parts[arrayPosBoxManip] = new BoxManip();
		parts[arrayPosWinchManip] = new WinchManip();
		parts[arrayPosShifter] = new Shifter();
		
		//initialize the gyro
		gyro = new ADXRS450_Gyro();
		
		/*jevois = CameraServer.getInstance().startAutomaticCapture();
		cvSink = CameraServer.getInstance().getVideo();
		outputStream = CameraServer.getInstance().putVideo("Jevois", 640, 480);
		source = new Mat();
		output = new Mat();*/
		
		//TODO: try-catch blocks!!!!!
		//Construct scanner.
		try
		{
			ourScanner = SweepJNI.construct("/dev/ttyUSB0", 115200);
			//Try to scan.
			SweepJNI.setMotorSpeed(ourScanner, 0);
			
			
		}
		catch(Exception e)
		{
			System.out.println(e + "LiDAR not created");
			ifScannerConnected = false;
		
		}
		((BoxManip)parts[arrayPosBoxManip]).init();
		
 	}
	
	public void teleopInit() 
	{
		//Output for debugging
		System.out.println("Enter teleopInit");
	}
	
	@Override
	public void teleopPeriodic() 
	{

		//		CameraServer.getInstance().getVideo();
		/*if (isOperatorControl() && isEnabled()) 
		{
			cvSink.grabFrame(source);
			//Imgproc.cvtColor(source, output,Imgproc.COLOR_BGR2GRAY);
			outputStream.putFrame(source);
			return;
		}*/
		
		//If we are in operator control and the robot is enabled
		if (isOperatorControl() && isEnabled()) 
		{
			
			for(int i = 0; parts[i] != null; i++) {
				parts[i].update();
			}
			/*parts[0].update(); //updates compressor switch
			parts[1].update();
			parts[2].update();*/
		}
		
		
	}
	
	public void autonomousInit() 
	{
		//Output for debugging
		//System.out.println("Enter autoInit");
		
		//Set the state of autonomous to 'a' to start
		//autoState = '0';
		
		//Output for debugging
		//System.out.println("Calibrate gyro");
		
		//Calibrate the gyro again
		gyro.reset();
		System.out.println("Gyro angle after Calibration: " + gyro.getAngle());
		if(ifScannerConnected)
		{
			SweepJNI.setMotorSpeed(ourScanner, 5);
			SweepJNI.startScanning(ourScanner);
		}
		//15ft per second
		//Instantiate gameData
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		//Try to get the gameData
		try {
			firstCharOfData = gameData.charAt(0);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			gameDataAvailable = false;
		}
		
		
		//Choose which autonomous to run:
		//Check 3 things: the physical switch is correct, there is gameData, and the lidar is connected
		if(!autoSwitchA.get() && gameDataAvailable && ifScannerConnected)
		{
			//1 means that we run fancy auto with lidar and gyro
			chooseAuto = 1;
		}
		else
		{
			//0 means we run the basic auto that drives forward
			chooseAuto = 0;
		}
	}
	
	//Runs periodically during autonomous
	public void autonomousPeriodic() 
	{
		//System.out.println("auto or not? " + autoSwitchA.get());
		//System.out.println("autoState" + autoState); 
		/* (mostly fixed i think but double check) TODO remember we want to move forward even if lidar doesnt work */
		
		/*
		 State 0: Robot picks up the cube from inside the robot
		 State a: Robot is on the back wall and rotating
		 State b: Robot is moving forwards
		 State c: Robot is turning to face switch
		 State d: Robot is dropping the cube
		 */
		
		//Output what state we are in for debugging.
		//System.out.println("State: " + autoState);
		//if(chooseAuto == 1)
		
		if(chooseAuto == 1)
		{
			//gets the angle from the gyro
			gyroAngle = gyro.getAngle();
			
			//Output for debugging
			//System.out.println("Gyro Angle: " + gyroAngle);
			
			//If the switch is on the left.
			if (gameData.charAt(0) == 'L')
			{
				turnA = GYRO_LEFT_ANGLE;
				distanceB = LIDAR_LEFT_DISTANCE;
			}
			else if(gameData.charAt(0) == 'R')
			{
				turnA = GYRO_RIGHT_ANGLE;
				distanceB = LIDAR_RIGHT_DISTANCE;
			}
			
			//if the manipulator has moved, move to state a
			if(state0Finished)
			{
				autoState = 'a';
			}
			
			//If we are in state 0, update the box manipulator part
			if(autoState == '0')
			{
				parts[arrayPosBoxManip].autoUpdate();
			}
			
			//Sets autoState to the second stage if we have rotated the correct amount.
			if(gyroAngle >= turnA-1 && gyroAngle <= turnA+1 && autoState == 'a')
		    {
				//Set the state to b
		    	autoState = 'b';
		    }
				
			//if the distance is within a margin of 1.5 from what we want and we are currently in state b, go to state c
			else if(distance >= distanceB -1.5 && distance <= distanceB + 100 && autoState == 'b')
		    {
		    	autoState = 'c';
		    }
				
			//If we are currently in state b, this runs both to get lidar values and (later) to correct drift or lidar malfunctions
			if(autoState == 'b') {
				//get a scan from the lidar -> TODO: only run this sometimes, otherwise wheels glitch
					
				if(scanCount > 8) {
					scanData = SweepJNI.getScan(ourScanner);
					
					//get the angle from the lidar
					lidarAngle = scanData[0].angle;
					//System.out.println(" lidarAngle: " + lidarAngle);
						
					//If the angle we're getting is in the right range, then we use it.
					if(lidarAngle <= 5000)
					{
						//get the distance, and output the distance/angle values for debugging
						distance = scanData[0].distance;
						//System.out.println("Distance: " + distance + " Angle: " + lidarAngle);
					}
					scanCount = 0;
				}
				scanCount++;
				//set the value of the change in the distance -- used for error management
				distanceChange = distance - previousDistance;
				/* TODO: Get rid of these magic values, things like 19.13 */
				//If the angle given by the gyro is less than what we want, go a little more left
				/*if(gyroAngle <= GYRO_RIGHT_ANGLE - 2)
				{	
					driftSpeed = "left";
				}
				
				//If the angle given by the gyro is more than what we want, go a little more right
				if(gyroAngle >=  GYRO_RIGHT_ANGLE + 2)
				{	
					driftSpeed = "right";
				}
					
				//checks for if there is a big jump in the lidar's distance readings
				if(distanceChange >= bigJump || distanceChange <= -1 * bigJump)
				{
					//set the speed to slow
					driftSpeed = "slow"; 
					//TODO: Some way to handle this error
				}
					
				else
				{
					//sets the current distance to the previous distance
					previousDistance = distance;
				}*/
			}
			
			
			//changes autoState to the dropping of the cube stage (d) if we have rotated the correct amount 
			/* TODO More magic values -2, 2 */ 
			if(gyroAngle >= -2 && gyroAngle <= 2 && (autoState == 'c' || autoState == 'b')) 
			{
		    	autoState = 'd';
		    }
			
			if(autoState == 'd')
			{
				parts[arrayPosBoxManip].autoUpdate();
			}
			
			//Call the autoupdate function on the Drive object
			parts[arrayPosDrive].autoUpdate(); 
		}//(ends if(chooseAuto == 1)
			
			
		else if(chooseAuto == 0)	
		{
			((Drive)parts[arrayPosDrive]).driveAutoUpdate();
		}
		
		
		//make sure the compressor turns on because we need it in auto
		parts[arrayPosCompressor].update();
	}//(ends the autonomousPeriodic() method)
	
	public void testInit()
	{
		CameraServer.getInstance().startAutomaticCapture();
	}
	public void testPeriodic()
	{
			//Call the testUpdate method for the winchManip object
// FORNOW			((WinchManip)parts[arrayPosWinchManip]).testUpdate();

		/*new Thread(() -> {*/

	        
//	        Mat source = new Mat();
	        //Mat output = new Mat();
	        
//	        while(!Thread.interrupted()) {
//	            cvSink.grabFrame(source);
	            //Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
//	            outputStream.putFrame(source);
//	        }
//	    }).start();
    }
	
	public void disable()
	{
		//TODO make in a loop and add disable method for other classes
		parts[arrayPosBoxManip].disable();
	}
}