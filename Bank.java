package application;

//////////////////////////////////////////////////////////////////////
//
//	Bank Class
//
//	Written by Emily Gross on 11/22/22
//
//	This class keeps track of the user's money and denomination in 
//	SlotMachineEmulator. The denomination values are restricted to
//	0.05, 0.25, 0.50, and 1.00. It also makes sure that any changes
//	to the user's bank account are valid and can convert from dollars
//	to credits.
//
//////////////////////////////////////////////////////////////////////

public class Bank {
	
	private double balance;
	private double denomination;
	
	private static final double[] denominationAmounts = {0.05, 0.25, 0.50, 1.00};

    public Bank(){
    	balance = 0.0;
    	denomination = 0.25;
    }

    public void setBalance(double newBalance){
    	balance = validBalance(newBalance);
    }
    
  //make sure denomination fits array and radiobuttons - this should not be an issue as long as SlotMachineEmulator is unchanged
    public void setDenomination(double newDenomination){
    	boolean validDenomination = false;
    	for (int i = 0; i < denominationAmounts.length; i++) {
    		if (denominationAmounts[i] == newDenomination) {
    			denomination = newDenomination;
    			validDenomination = true;
    		}
    	}
    	if (!validDenomination) {
    		Alert.show("Choose a valid denomination amount!");
    	}
    }

    public double getBalance(){
    	return balance;
    }
    
    public double getDenomination() {
    	return denomination;
    }

    public long getCredits(){
    	return (long)(balance / denomination + .000000001); //prevents roundoff error from subtraction
    }

    public void deposit(double amount){
    	balance = validBalance(balance + amount);
    }
    
    public void withdraw(double withdrawalAmount) {
    	balance = validBalance(balance - withdrawalAmount);
    }
    
    public double withdrawAll() {
    	double withdrawalAmount = balance;
    	balance = 0.0;
    	return withdrawalAmount;
    }
    
    public double validBalance(double amount) { //changes amount to a valid number if it is out of range
    	if (amount < 0.0) { //balance cannot be negative
    		amount = 0.0;
    	} else if (amount >= 9999999999999.99) { //numbers greater than this won't work for the credits box at the lowest denomination
    		amount = 9999999999999.99;
    		Alert.show("You have reached the maximum balance!");
    	}
    	return amount;
    }
    
    public double validWinnings(double amount) { //similar to previous method
    	if (amount < 0.0) { //winnings cannot be negative
    		amount = 0.0;
    	} else if (amount >= 999999999999999.99) { //greater number than previous method since text box is bigger
    		amount = 999999999999999.99;
    		Alert.show("You have reached the maximum amount of winnings!\nCash out and play again if you would like to continue.");
    	}
    	return amount;
    }

}
