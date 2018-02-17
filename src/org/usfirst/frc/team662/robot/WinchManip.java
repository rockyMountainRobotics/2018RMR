package org.usfirst.frc.team662.robot;

//Import Statements for talons and X-box Controllers (so we can use code for them)
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.XboxController;


public class WinchManip implements Component 
{
	
	//Multiplier constants for motors.
	final double MAX_MULTIPLIER = .5;
	final double MIN_MULTIPLIER = 0.0;
	
	//Create talons for lifting the winch and pulling the winch
	WPI_TalonSRX liftWinchSet = new WPI_TalonSRX(RobotMap.WINCH_SET);
	WPI_TalonSRX pullWinch = new WPI_TalonSRX(RobotMap.WINCH_PULL);
	
	//Create XboxController object for the controller
	//XboxController manipController = new XboxController(1);
	double winchSetSpeed;
	
	@Override
	public void update() 
	{
		//Set the liftWinchSet variable according to how far the joystick is pushed
		winchSetSpeed = Robot.manipController.getRawAxis(XboxMap.D_PAD_VERT) * MAX_MULTIPLIER;
		/* TODO: Make sure have direction correct */
		liftWinchSet.set(-winchSetSpeed);
		
		//If trigger is pushed and greater than dead zone, pull winch. Otherwise, don't.
		if (Robot.manipController.getRawAxis(XboxMap.LEFT_TRIGGER)>.1)
		{
			/* TODO make sure direction correct */
			pullWinch.set(-Robot.manipController.getRawAxis(XboxMap.LEFT_TRIGGER));
		}
		else
		{
			pullWinch.set(0);
		}
	}
	
	
	@Override
	public void autoUpdate()
	{
		
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
