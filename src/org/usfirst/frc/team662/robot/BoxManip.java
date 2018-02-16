package org.usfirst.frc.team662.robot;

//Import code for Xbox Controller, limit switches, and talons
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class BoxManip implements Component 
{
	DoubleSolenoid manipulatorSolenoid = new DoubleSolenoid(RobotMap.MANIPULATOR_SOLENOID_OUT, RobotMap.MANIPULATOR_SOLENOID_IN);
	
	//Create states for top, middle, and bottom (for arms)
	final static int TOP = 1;
	final static int MIDDLE = 0;
	final static int BOTTOM = -1;
	
	/* TODO better names for these two variables */
	boolean current = true;
	boolean past = false;
	
	//Create a final static double variable for speed and a double variable, also for speed
	//Having two lets us use them as multiples of each other
	/* TODO better names */
	final static double ARM_SPEED = .6;
	final static double ARM_SPEED_DOWN = .4;
	final static double REST_ARM_SPEED = -.1;
	final static double CHAIN_SPEED = .4;
	final static double CHAIN_SPEED_DOWN = .3;
	final static double REST_CHAIN_SPEED = -.2;
	double speed = 0;
	
	//Create the TalonSRX's for the manipulator
	//TODO: which heckin motor is named "manipMotor" and can we rename it
	WPI_TalonSRX liftChain = new WPI_TalonSRX(RobotMap.CHAIN);
	WPI_TalonSRX rotateArms = new WPI_TalonSRX(RobotMap.ARM);
	
	//Create limit switches
	//public DigitalInput limitSwitchTop = new DigitalInput(RobotMap.LIMIT_TOP);
	//public DigitalInput limitSwitchMiddle = new DigitalInput(RobotMap.LIMIT_MIDDLE);
	//public DigitalInput limitSwitchBottom = new DigitalInput(RobotMap.LIMIT_BOTTOM);
	
	
	
	//Create an isTraveling boolean (true/false) variable
	boolean isTraveling = false;
	
	//Create a double variable for currentLocation of arms
	double currentLocation = BOTTOM;
	
	//Update method
	public void update()
	{
		//Make boolean (true/false) variables for each limit switch
		//When switch is pressed, variable is true; when not pressed, it is false
		/* TODO put variable declarations outside of function */
		boolean topLimit = false; // !limitSwitchTop.get();
		boolean middleLimit = false; //!limitSwitchMiddle.get();
		boolean bottomLimit = false; //!limitSwitchBottom.get();
		
		double armSpeed = Robot.manipController.getRawAxis(XboxMap.RIGHT_JOY_VERT);
		double chainSpeed = Robot.manipController.getRawAxis(XboxMap.LEFT_JOY_VERT);
		
		
		//TODO Write Solenoid code!!
		//MOVE ARMS ACCORDINGLY WHEN X, Y, OR A BUTTONS ARE PRESSED.		
		//When the "Y" button is pressed and the arms aren't currently moving, move them to top.
		/*if(Robot.driveController.getRawButton(XboxMap.Y) && currentLocation != TOP && !isTraveling)
		{
			speed = SPEED;
			isTraveling = true;
			currentLocation = TOP;
		}
		
		//When "X" button is pressed and the arms aren't moving, move them to middle.
		if(Robot.driveController.getRawButton(XboxMap.X)  && !isTraveling)
		{
			//If you're currently at the top, move down.
			if(currentLocation == TOP)
			{
				speed = -SPEED;
				isTraveling = true;
				currentLocation = MIDDLE;
			}
					
			//If you're currently at the bottom, move up.
			if(currentLocation == BOTTOM)
			{
				speed = SPEED;
				isTraveling = true;
				currentLocation = MIDDLE;
			}
		}
				
		//When the "A" button is pressed and the arms aren't currently moving, move them to bottom.
		if(Robot.driveController.getRawButton(XboxMap.A) && currentLocation != BOTTOM && !isTraveling)
		{
			speed = -SPEED;
			isTraveling = true;
			currentLocation = BOTTOM;
		}
		*/
//ARMS STOP MOVING WHEN THEY REACH THEIR DESTINATION
		
//		if(isTraveling)
//		{
			// TODO: if we ever hit the topLimit or bottomLimit we should stop ? set speed to 0 
			// TODO if hit topLimit AND speed > 0 then stop 
			// TODO if hit bottomLimit and speed < 0 then stop 
			// TODO Print out these error conditions so we know we have to fix something 
			
			//If top limit switch is pressed and the arms were going to the top, stop the motors
//			if(topLimit && currentLocation == TOP)
//			{
//				speed = 0;
//				isTraveling = false;
//					
//			}
			
//			//If middle limit switch is pressed and the arms were going to the middle, stop the motors
//			if(middleLimit && currentLocation == MIDDLE)
//			{
//				speed = 0;
//				isTraveling = false;
//			}
			
			//If bottom limit switch is pressed and the arms were going to the bottom, stop the motors
//			if(bottomLimit && currentLocation == BOTTOM)
//			{
//				speed = 0;
//				isTraveling = false;
//			}
//		}
		
		// Check for the input in the right Joystick
		//armSpeed is the value we get from the controller
		System.out.println("armSpeed " + armSpeed );
		if (Math.abs(armSpeed) > Robot.DEADZONE) {
			rotateArms.set(armSpeed * ARM_SPEED);
		}
		
		else {
			rotateArms.set(REST_ARM_SPEED);
		}
		
		//Sets the speed of the motor
		//chainSpeed is the value we get from the controller
		System.out.println("chainSpeed " + chainSpeed );
		if (Math.abs(chainSpeed) > Robot.DEADZONE) {
			liftChain.set(chainSpeed * CHAIN_SPEED);
		}
		else {
			liftChain.set(REST_CHAIN_SPEED);
			//liftChain.
		}
		
		//current = Robot.manipController.getRawButton(XboxMap.B);
		
		System.out.println(current);
		System.out.println(manipulatorSolenoid.get());
	
		/*if(current == true && past == false){
			if(manipulatorSolenoid.get() == DoubleSolenoid.Value.kForward) 
				manipulatorSolenoid.set(DoubleSolenoid.Value.kReverse);
			else
				manipulatorSolenoid.set(DoubleSolenoid.Value.kForward);
		}*/
		
		
		if(Robot.manipController.getRawButton(XboxMap.B))
		{
			manipulatorSolenoid.set(DoubleSolenoid.Value.kForward);
			
		}
		
		else if(Robot.manipController.getRawButton(XboxMap.X))
		{
			manipulatorSolenoid.set(DoubleSolenoid.Value.kReverse);
		}
		//SmartDashboard.putBoolean("High Gear", !solenoid.get());
		past = current;
	}
	
	
	public void autoUpdate()
	{
		switch (Robot.autoState)
		{
		//For first three cases, code is in Drive.
		case 'a':
			break;
		case 'b':
			break;
		case 'c':
			break;
			
		//Case D is the manipulator part, so it goes under BoxManip
		case 'd':
			//Manipulator Motor moves 
			//TODO not correct 
			
			//manipMotor.set(.5);
			manipulatorSolenoid.set(DoubleSolenoid.Value.kReverse);
			break;
		}
	
		//	{ make the arms put the box into the switch. The robot should be lined up with the wall.}
		//autoUpdateS is located in Robot.
	}
	
	public void disable()
	{
		
	}
	
	@Override
	public void init()
	{
		liftChain.set(0);
		rotateArms.set(0);
	}
	
}
