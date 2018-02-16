package org.usfirst.frc.team662.robot;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GearTooth;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drive implements Component
{
	//Multiplier for the motors so we aren't toooo speedy
	final double MULTIPLIER = .6;
	
	//Declare the 4 Talon Motors
    WPI_TalonSRX leftFront;
    WPI_TalonSRX leftBack;
    WPI_TalonSRX rightFront;
    WPI_TalonSRX rightBack;

    //These are speed controllers that I think treat two motors as one
    SpeedControllerGroup leftDrive;
    SpeedControllerGroup rightDrive;
    
    //Declare a differential drive. I don't quite know what this is, but it drives the robot
    DifferentialDrive differentialDrive;
    
    
	//Constructor for the Drive class
	public Drive()
	{
		//Instantiate the motors using the ports we defined in RobotMap class
		leftFront = new WPI_TalonSRX(RobotMap.LEFT_FRONT_MOTOR);
		leftBack = new WPI_TalonSRX(RobotMap.LEFT_BACK_MOTOR);
	    rightFront = new WPI_TalonSRX(RobotMap.RIGHT_FRONT_MOTOR);
	    rightBack = new WPI_TalonSRX(RobotMap.RIGHT_BACK_MOTOR);
	    
	    //Invert the left side so that the robot drives the correct direction
	    leftFront.setInverted(false);
        leftBack.setInverted(true);	
        
        //Inverts the right side so the motors go the right direction
        rightFront.setInverted(true);
        rightBack.setInverted(true);
        
        //Instantiate the speed controller groups
        leftDrive = new SpeedControllerGroup(leftFront, leftBack);
        rightDrive = new SpeedControllerGroup(rightFront, rightBack);

        //Instantiate the differential drive.
        differentialDrive = new DifferentialDrive(leftDrive, rightDrive);
        
        
	}

	//Update method that will be called in teleop
	@Override
	public void update() 
	{
		//Backup tank drive that we hopefully won't have to use
		//differentialDrive.tankDrive(MULTIPLIER *(Robot.driveController.getY(GenericHID.Hand.kLeft)), MULTIPLIER*( Robot.driveController.getY(GenericHID.Hand.kLeft)));
		
		//Arcade drive that was very conveniently written for us
		differentialDrive.arcadeDrive(MULTIPLIER *(Robot.driveController.getY(GenericHID.Hand.kLeft)), MULTIPLIER*( -Robot.driveController.getX(GenericHID.Hand.kLeft)));
	}

	
	@Override
	public void autoUpdate() 
	{
		//Create the string gameData
		String gameData;
		
		//Gets the game data from the game server, then stores it in gameData.
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		//These are all used for corrections -> they were commented out for testing and may need to be un-commented-out
		/*if (Robot.driftSpeed.equals("stop") )
			{
			rightFront.set(.0);
			leftFront.set(.0);
			rightBack.set(.0);
			leftBack.set(.0);
			}
		if (Robot.driftSpeed.equals("slow") )
			{
			rightFront.set(.3);
			leftFront.set(.3);
			rightBack.set(.3);
			leftBack.set(.3);
			}
		if (Robot.driftSpeed.equals("crusingspeed") )
			{
			rightFront.set(.5);
			leftFront.set(.5);
			rightBack.set(.5);
			leftBack.set(.5);
			}
		if (Robot.driftSpeed.equals("fullspeedahead") )
		{
			rightFront.set(.9);
			leftFront.set(.9);
			rightBack.set(.9);
			leftBack.set(.9);
		}
		if (Robot.driftSpeed.equals("left") )
		{
			rightFront.set(.4);
			leftFront.set(.5);
			rightBack.set(.4);
			leftBack.set(.5);
		}	
		if (Robot.driftSpeed.equals("right") )
		{
			rightFront.set(.5);
			leftFront.set(.4);
			rightBack.set(.5);
			leftBack.set(.4);
		}*/	
		//Checks whether gameData is left or right, and sets the motors accordingly
		
		//Debugging - prints out motor values
		System.out.println("AutoUpdate1 : Speed Right: " + rightFront.get() + ", " + rightBack.get() + " Speed Left: " + leftFront.get() + ", " + leftBack.get());
		
		//If our color is on the left of the switch
		if (gameData.charAt(0) == 'L')
		{
			//Switch based on the state we are in
			switch(Robot.autoState)
			{
			//Robot turning
			case 'a':
				
				//Turn on right motors so robot turns left; turn off left motors
				/* TODO Get rid of magic values */
				rightDrive.set(.5);
				leftDrive.set(0);
				//rightFront.set(.5);
				//rightBack.set(.5);
				//leftFront.set(0);
				//leftBack.set(0);
				break;
				
			//Robot moving forward	
			case 'b':
				//Turn on all motors so robot goes forward
				rightDrive.set(.5);
				leftDrive.set(-.5);
				//rightFront.set(.5);
				//leftFront.set(.5);
				//rightBack.set(.5);
				//leftBack.set(.5);
				break;
				
			case 'c':
				//Turn on left motors so robot turns left; turn off right motors
				rightDrive.set(0);
				leftDrive.set(.5);
				//leftFront.set(.5);
				//leftBack.set(.5);
		        //rightFront.set(0);
		        //rightBack.set(0);
				break;
				
			case 'd':
				//Turn off all motors
				rightDrive.set(0);
				leftDrive.set(0);
		        //rightFront.set(0);
		        //rightBack.set(0);
		        //leftFront.set(0);
		        //leftBack.set(0);
				//Manip Code
				break;
				
			}
		} 
		
		//If our color is on the right of the switch
		else 
		{
			//Switch based on the robot's state
			switch(Robot.autoState)
			{
			//Robot turning
			case 'a':
				//Turn on left motors so robot turns right; turn off left motors
				rightDrive.set(0);
				leftDrive.set(0);
				//leftFront.set(.5);
				//leftBack.set(.5);
				//rightFront.set(0);
				//rightBack.set(0);
				break;
				
			//Robot moving forward
			case 'b':
				//If the lidar actually works, then do the scan thing.
				if(Robot.ifScannerConnected) 
				{
				//Turn on all motors so robot moves forward
				rightDrive.set(.5);
				leftDrive.set(.5);
				//rightFront.set(.5);
				//leftFront.set(.5);
				//rightBack.set(.5);
				//leftBack.set(.5);
				}
				else
				{
					//Lauren make it move forward for an amount of time. LUAREN
				}
				break;
				
			//Robot turning the other way
			case 'c':
				//Turn on right motors so robot turns left; turn off right motors
				rightDrive.set(.5);
				leftDrive.set(0);
				//rightFront.set(.5);
				//rightBack.set(.5);
				//leftFront.set(0);
				//leftBack.set(0);
				break;
			//Robot placing cube
			case 'd':
				//Turn all motors off
				rightDrive.set(0);
				leftDrive.set(0);
				//rightFront.set(0);
				//rightBack.set(0);
				//leftFront.set(0);
				//leftBack.set(0);
				//Manip Code
				break;
			}
		}
		
	
	}

	@Override
	public void disable() 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void init()
	{
		leftFront.set(0);
		leftBack.set(0);
		rightFront.set(0);
		rightBack.set(0);
	}
}
