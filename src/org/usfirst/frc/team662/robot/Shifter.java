package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

public class Shifter implements Component
{
	//public static final int SOLENOID_PORT = 0;
	DoubleSolenoid solenoid = new DoubleSolenoid(RobotMap.SHIFTER_SOLENOID_IN, RobotMap.SHIFTER_SOLENOID_OUT);
	boolean current = true;
	boolean past = false;
	
	
	public void update()
	{
		current = Robot.driveController.getRawButton(XboxMap.B);
		
		if(current == true && past == false){
			if(solenoid.get() == DoubleSolenoid.Value.kReverse) 
				solenoid.set(DoubleSolenoid.Value.kForward);
			else
				solenoid.set(DoubleSolenoid.Value.kReverse);
		}
		//SmartDashboard.putBoolean("High Gear", !solenoid.get());
		past = current;
	}


	@Override
	public void autoUpdate() 
	{
		
	}


	@Override
	public void disable() 
	{
		
	}
	
	@Override
	public void init()
	{
		
	}
	
	
}