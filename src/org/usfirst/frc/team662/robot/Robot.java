package org.usfirst.frc.team662.robot;

import org.opencv.core.Mat;
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
    //classes: Drive, LiDAR(if we keep this as its own class), BoxManip
    public static char autoState = 'a';
    public static String  driftSpeed = "";

    
    //Creates the gyro object. Initialization happens in robotInit
    public ADXRS450_Gyro gyro;
    
    //Doubles for the math that we calculated
    public final double GYRO_RIGHT_ANGLE = 16.24; /* TODO  : confirm angles */
    public final double GYRO_LEFT_ANGLE = -20.13;  /* TODO confirm angles */
    public final double LIDAR_LEFT_DISTANCE = 358.895;
    public final double LIDAR_RIGHT_DISTANCE = 339.35;
    
    public final static double DEADZONE = .15;
    
    //an array of components so we can easily call update() on different components
    Component[] parts = new Component[10];
    //Creates the controller for drive.
    public static XboxController driveController = new XboxController(0);
    public static XboxController manipController = new XboxController(1);

    
    DigitalInput autoSwitchA = new DigitalInput(RobotMap.AUTO_ENABLE_A);
    DigitalInput autoSwitchB = new DigitalInput(RobotMap.AUTO_ENABLE_B);
    
    public static boolean ifScannerConnected = true;
    
	@Override
	public void robotInit() 
	{
		//jevois = CameraServer.getInstance().startAutomaticCapture();
		/*new Thread(() -> {
			jevois = CameraServer.getInstance().startAutomaticCapture();
			jevois.setResolution(640, 480);
		
			CvSink cvSink = CameraServer.getInstance().getVideo();
	        CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
	        
	        Mat source = new Mat();
	        //Mat output = new Mat();
	        
	        while(!Thread.interrupted()) {
	            cvSink.grabFrame(source);
	            //Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
	            outputStream.putFrame(source);
	        }
	    }).start();
    */
		
		//Add components to the array - currently we don't have the manipulator classes or the shifter
		parts[0] = new CompressorSwitch();
		parts[1] = new Drive();
		parts[2] = new BoxManip();
		parts[3] = new WinchManip();
		parts[4] = new Shifter();
		
		//initialize the gyro
		gyro = new ADXRS450_Gyro();
		
		
		//TODO: try-catch blocks!!!!!
		//Construct scanner.
		try
		{
			ourScanner = SweepJNI.construct("/dev/ttyUSB0", 115200);
			//Try to scan.
			SweepJNI.startScanning(ourScanner);
		}
		catch(Exception e)
		{
			System.out.println(e + "LiDAR not created");
			ifScannerConnected = false;
		
		}
 	}
	
	public void teleopInit() 
	{
		//Output for debugging
		System.out.println("Enter teleopInit");
	}
	
	@Override
	public void teleopPeriodic() 
	{
		//If we are in operator control and the robot is enabled
		if (isOperatorControl() && isEnabled()) 
		{
			//TODO Put these in a loop and make sure we have the rest of them
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
		System.out.println("Enter autoInit");
		
		//Set the state of autonomous to 'a' to start
		autoState = 'a';
		
		//Output for debugging
		System.out.println("Calibrate gyro");
		
		//Calibrate the gyro again idk why but its here
		/* TODO : Figure out why its here and if it should be somewhere else */
		gyro.calibrate();
		System.out.println("Gyro angle after Calibration: " + gyro.getAngle());
		
		//15ft per second
		//Instantiate gameData
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}
	
	/* TODO : Move these variables to the top of class, put variables together */
	//Double variables that store the gyro's angle, the distance given by the lidar, the angle that
	//the lidar is at, the previous distance (used for error checking), and the change in distance
	double gyroAngle;
	double distance;
	double lidarAngle;
	double previousDistance=3;
	double distanceChange;
	double bigJump = 20;
	
	
	//Runs periodically during autonomous
	public void autonomousPeriodic() 
	{
		/* (mostly fixed i think but double check) TODO remember we want to move forward even if lidar doesnt work */
		
			/*
			 State a: Robot is on the back wall and rotating
			 State b: Robot is moving forwards
			 State c: Robot is turning to face switch
			 State d: Robot is dropping the cube
			 */
			
			//Output what state we are in for debugging.
			//System.out.println("State: " + autoState);
			
			//gets the angle from the gyro
			gyroAngle = gyro.getAngle();
			
			//Output for debugging
			System.out.println("Gyro Angle: " + gyroAngle);
			
			//If the switch is on the left.
			if (gameData.charAt(0) == 'L')
			{
				//Sets autoState to the second stage if we have rotated the correct amount.
				if(gyroAngle >= GYRO_LEFT_ANGLE-1 && gyroAngle <= GYRO_LEFT_ANGLE+1 && autoState == 'a')
			    {
					//Set the state to b
			    	autoState = 'b';
				    	
				    	//get a scan from the lidar -> TODO: only run this sometimes, otherwise wheels glitch
						scanData = SweepJNI.getScan(ourScanner);
						
						//get the angle from the lidar
						lidarAngle = scanData[0].angle;
						
						//If the angle we're getting is in the right range, then we use it.
						if(lidarAngle <= 5000)
						{
							//get the distance, and output the distance/angle values for debugging
							distance = scanData[0].distance;
							System.out.println("Distance: " + distance + " Angle: " + lidarAngle);
						}
						
						//set the value of the change in the distance -- used for error management
						distanceChange = distance - previousDistance;
						
			    }
				
				//if the distance is within a margin of 1.5 from what we want and we are currently in state b, go to state c
				else if(distance >= LIDAR_LEFT_DISTANCE -1.5 && distance <= LIDAR_LEFT_DISTANCE +1.5 && autoState == 'b')
			    {
			    	autoState = 'c';
			    }
				
				//If we are currently in state b, this runs to correct drift or lidar malfunctions
				if(autoState == 'b') 
				{
					
					//drift correction
					/*if(gyroAngle <= GYRO_LEFT_ANGLE-1)
					{	
						driftSpeed = "right";
					}
					if(gyroAngle >=  GYRO_LEFT_ANGLE+1)
					{	
						driftSpeed = "left";
					}
					
					//checks for if there is a big jump in the lidar's distance readings
					if(distanceChange >= bigJump || distanceChange <= -1 * bigJump)
					{
						driftSpeed = "slow";
						if ( ) 
						{
							//do not replace with angle
							gyro.getAngle(); 
						}
					}	
					else
					{	
						//sets the current distance to the previous distance
						previousDistance = distance;
					}*/
					
				}
				
			}
			else if (gameData.charAt(0) == 'R')
			{
				//Sets autoState to the second stage if we have rotated the correct amount.
				if(gyro.getAngle() >= GYRO_LEFT_ANGLE -1 && gyro.getAngle() <= GYRO_LEFT_ANGLE +1 && autoState == 'a')
			    {
			    	autoState = 'b';
			    }
				//Add a parameter for angle for scanData (something like scanData.angle)
				else if(distance >= LIDAR_RIGHT_DISTANCE -1.5 && distance <= LIDAR_RIGHT_DISTANCE +1.5 && autoState == 'b')
			    {
			    	autoState = 'c';
			    }
				
				//corrections for during state b
				if(autoState == 'b') {
					
					/* TODO: Get rid of these magic values, things like 19.13 */
					//If the angle given by the gyro is less than what we want, go a little more left
					if(gyroAngle <= 19.13)
					{	
						driftSpeed = "left";
					}
					
					//If the angle given by the gyro is more than what we want, go a little more right
					if(gyroAngle >=  21.13)
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
					}
				}
			}
			
			//changes autoState to the dropping of the cube stage (d) if we have rotated the correct amount 
			//L-> doesn't matter left or right gamedata
			/* TODO More magic values -2, 2 */ 
			if(gyroAngle >= -2 && gyroAngle <= 2 && autoState == 'c') 
			{
		    	autoState = 'd';
		    }
			
			//Call the autoupdate function on the Drive object
			parts[1].autoUpdate(); 
			/* TODO what if we change drive object to some other array index instead of parts[1] */

			//Do ALL of the autonomous updating in here! Nothing outside!
		}
	}
