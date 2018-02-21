package org.usfirst.frc.team662.robot;

//Import Statements for talons and X-box Controllers (so we can use code for them)
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.XboxController;


public class WinchManip implements Component 
{
	
	//Multiplier constants for motors.
	final double MAX_MULTIPLIER = .7;
	final double DOWN_MULTIPLIER = .3;
	final double MIN_MULTIPLIER = 0.0;
	
	//Create talons for lifting the winch and pulling the winch
	WPI_TalonSRX liftWinchSet = new WPI_TalonSRX(RobotMap.WINCH_SET);
	WPI_TalonSRX pullWinch = new WPI_TalonSRX(RobotMap.WINCH_PULL);
	
	//Create XboxController object for the controller
	//XboxController manipController = new XboxController(1);
	int winchSetSpeed;
	
	@Override
	public void update() 
	{
		//Set the liftWinchSet variable according to how far the joystick is pushed
		winchSetSpeed = Robot.manipController.getPOV(0);
		if(winchSetSpeed == 0 || winchSetSpeed == 45 || winchSetSpeed == 315)
			liftWinchSet.set(MAX_MULTIPLIER);
		else if(winchSetSpeed == 180 || winchSetSpeed == 225 || winchSetSpeed == 135)
			liftWinchSet.set(-DOWN_MULTIPLIER);
		else
			liftWinchSet.set(0);
		/* TODO: Make sure have direction correct */
		//System.out.println("winch speed " + winchSetSpeed);
		//liftWinchSet.set(-winchSetSpeed);
		
		//If trigger is pushed and greater than dead zone, pull winch. Otherwise, don't.
		if (Robot.manipController.getRawAxis(XboxMap.LEFT_TRIGGER)> Robot.DEADZONE)
		{
			/* TODO make sure direction correct */
			pullWinch.set(-Robot.manipController.getRawAxis(XboxMap.LEFT_TRIGGER));
		}
		else if (Robot.manipController.getRawAxis(XboxMap.RIGHT_TRIGGER) > Robot.DEADZONE) 
		{
			pullWinch.set(Robot.manipController.getRawAxis(XboxMap.RIGHT_TRIGGER));
		}
		else {
			pullWinch.set(0);
		}
	}
	
	
	@Override
	public void autoUpdate()
	{
		
	}
	
	
	//This method is designed to run when we are moving the winch one way or the other
	public void testUpdate()
	{
		//If both switches are pressed to be true
		if(Robot.autoSwitchA.get() && Robot.autoSwitchB.get())
		{
			//turn the winch on in one direction
			pullWinch.set(.5);
		}
		//If both switches are the other way (False)
		else if(!Robot.autoSwitchA.get() && !Robot.autoSwitchB.get())
		{
			//turn the winch the other way
			pullWinch.set(-.5);
		}
		//If one switch is flipped
		else
		{
			//turn the motor off
			pullWinch.set(0);
		}
	}
	
	@Override
	public void disable()
	{
		//When disabling, this turns the motors off.
		liftWinchSet.set(0);
		pullWinch.set(0);
	}
	
	@Override
	public void init()
	{
		liftWinchSet.set(0);
		pullWinch.set(0);
	}
}
