package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.XboxController;

public class RobotMap 
{
	
	//Drive motor ports ---- See end of each line for which robot uses which ports
	public final static int RIGHT_FRONT_MOTOR = 6; //2018: 6    MOTO: 5
	public final static int LEFT_FRONT_MOTOR = 7;  //2018: 7    MOTO: 6
	public final static int RIGHT_BACK_MOTOR = 5;  //2018: 5    MOTO: 3
	public final static int LEFT_BACK_MOTOR = 8;   //2018: 8    MOTO: 8
	
	//Manipulator motor ports
	// TODO : Get new values for wiring Following value are TBD
	public final static int WINCH_SET = 1; //was 1 this is equal to arm on breaker assignments
	public final static int WINCH_PULL = 2;//was 2
	public final static int CHAIN = 4; // was 3 
	public final static int ARM = 3; // was 4
	
	//Solenoids
	public final static int MANIPULATOR_SOLENOID_IN = 2;
	public final static int MANIPULATOR_SOLENOID_OUT = 3;
	public final static int SHIFTER_SOLENOID_IN = 0;
	public final static int SHIFTER_SOLENOID_OUT = 1;
	
	//Limit switch ports
	public final static int LIMIT_TOP = 1;
	public final static int LIMIT_MIDDLE = 2;
	public final static int LIMIT_BOTTOM = 3;
	public final static int LIMIT_ARM = 4;
	
	//Switch ports
	public final static int AUTO_ENABLE_A = 0;
	public final static int AUTO_ENABLE_B = 1;
	public final static int COMPRESSOR_ENABLE = 2;
	
	
	
}