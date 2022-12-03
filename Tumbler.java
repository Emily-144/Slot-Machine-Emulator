package application;
import java.util.Random;

//////////////////////////////////////////////////////////////////////
//
//	Tumbler Class
//
//	Written by Emily Gross on 11/22/22
//
//	This class represents the values of the tumblers in
//	SlotMachineEmulator. The values are restricted to cherries,
//	grapes, banana, and blank. This class also contains a method
//	spin() that generates a random value for each of the slots.
//
//////////////////////////////////////////////////////////////////////

public class Tumbler {
	
	private static final Random randomNum = new Random();
	
	public static enum ValueEnum {CHERRIES, GRAPES, BANANA, BLANK};
    private static final ValueEnum[] tumblerValueArray = ValueEnum.values();
    
    private ValueEnum value;

    public Tumbler(){
	  	value = ValueEnum.BLANK;
    }
    
    //setter method isn't really necessary since this class is used to get values from enum/array but it is here just in case
    
    public void setTumblerValue(ValueEnum newValue) {
    	value = newValue;
    }

    public ValueEnum getTumblerValue(){
    	return value;
    }
    
    public String toString(){
    	return value.name();
    }
    
    public ValueEnum spin(){ //generates a random value from the enumerator array and returns it
    	value = tumblerValueArray[randomNum.nextInt(tumblerValueArray.length)];
    	return value;
    }
	    
}
