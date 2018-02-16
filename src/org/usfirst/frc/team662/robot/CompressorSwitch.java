package org.usfirst.frc.team662.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Compressor;

public class CompressorSwitch implements Component
{
	DigitalInput compressorInput = new DigitalInput(RobotMap.COMPRESSOR_ENABLE);
	Compressor compressor = new Compressor();
	
	public void update()
	{
		//Turns the compressor on if the switch is on, and off if the switch is off
		
		//System.out.println("compinput" + compressorInput.get());
		
		if(!compressorInput.get())
		{
			compressor.start();
		}
		else
		{
			compressor.stop();
		}
		
	}

	public void autoUpdate() 
	{
		update();
	}
	
	public void disable() 
	{
		compressor.stop();
	}

	public void init()
	{
	
	}
}