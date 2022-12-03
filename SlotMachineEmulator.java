package application;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

//////////////////////////////////////////////////////////////////////
//
//	Slot Machine Emulator
//
//	Written by Emily Gross on 11/22/22
//
//	This program simulates a slot machine. The user must enter cash
//	to play, and this amount is displayed in both dollars and credits.
//	The user can also set their preferred bet amount and denomination.
//	When the user wants to quit, their balance will be printed. This
//	program uses the Tumbler, Bank, and Alert classes as well.
//
//////////////////////////////////////////////////////////////////////

public class SlotMachineEmulator extends Application {
	
	//load images/media files
	
	private static File image1 = new File("Cherries.png");
	private static File image2 = new File("Grapes.png");
	private static File image3 = new File("Banana.png");
	private static File image4 = new File("BlankFruit.png");
	private static File image5 = new File("smallBanana.png");
	private static File image6 = new File("smallGrapes.png");
	private static File image7 = new File("smallCherries.png");
	
	//sound effects: mixkit.co
	
	private static File media1 = new File("CoinHandling.wav");
	private static File media2 = new File("win1.wav");
	private static File media3 = new File("win2.wav");
	
	private static File[] files = {image1, image2, image3, image4, image5, image6, image7, media1, media2, media3};
	
	//assign images/media to files
	
	private static Image cherries = new Image(image1.toURI().toString());
	private static Image grapes = new Image(image2.toURI().toString());
	private static Image banana = new Image(image3.toURI().toString());
	private static Image blankFruit = new Image(image4.toURI().toString());
	private static Image smallBanana = new Image(image5.toURI().toString());
	private static Image smallGrapes = new Image(image6.toURI().toString());
	private static Image smallCherries = new Image(image7.toURI().toString());
	
	private static Media coinHandling = new Media(media1.toURI().toString());
	private static Media win1Sound = new Media(media2.toURI().toString());
	private static Media win2Sound = new Media(media3.toURI().toString());
	
	private static MediaPlayer coinHandlingPlayer = new MediaPlayer(coinHandling);
	private static MediaPlayer win1SoundPlayer = new MediaPlayer(win1Sound);
	private static MediaPlayer win2SoundPlayer = new MediaPlayer(win2Sound);
	
	private static MediaPlayer[] mediaPlayers = {coinHandlingPlayer, win1SoundPlayer, win2SoundPlayer};
	
	//default slots to be changed when user spins
	
	private static ImageView slot1 = new ImageView(blankFruit);
	private static ImageView slot2 = new ImageView(blankFruit);
	private static ImageView slot3 = new ImageView(blankFruit);
	private static ImageView[] slotImages = {slot1, slot2, slot3};
	
	private static Tumbler[] slots = new Tumbler[3]; {
		slots[0] = new Tumbler();
		slots[1] = new Tumbler();
		slots[2] = new Tumbler();
	}
	
	//other variables used in various methods
	
	private static final int MAX_BET = 3;
	private static int betMultiplier = 3;
	private static long credits; //long contains bigger numbers than int
	private static double totalWinnings;
	
	private static Bank bankAccount = new Bank();
	
	//gui elements used in methods
	
	private static Label balanceAmountLbl = new Label("$0.00");
	private static Label betMultiplierLbl = new Label("3");
	private static Label messageLbl = new Label("Deposit Cash to Begin");
	private static Label creditsAmountLbl = new Label("0");
	private static Label winningsAmountLbl = new Label("$0.00");
	
	private static TextField depositCashTxtFld = new TextField();
	
	private static RadioButton option1 = new RadioButton("$0.05");
	private static RadioButton option2 = new RadioButton("$0.25");
	private static RadioButton option3 = new RadioButton("$0.50");
	private static RadioButton option4 = new RadioButton("$1.00");
	
	//methods
	
	public static boolean allFilesFound() { //make sure that all image/media files exist - exceptions still thrown for media?
		
		String message = "";
		int numFilesNotFound = 0;
		boolean areAllFilesFound = true;
		
		for (int i = 0; i < files.length; i++) {
			if (!files[i].exists()) {
				message += "The file " + files[i] + " was not found.\n";
				numFilesNotFound++;
			}
		}
		
		if (numFilesNotFound > 0) {
			if (numFilesNotFound == 1) {
				message += "\nIn order for SlotMachineEmulator to run, this file must be added.";
			} else {
				message += "\nIn order for SlotMachineEmulator to run, these files must be added.";
			}
			System.out.println(message); //in case alert class is not found either - console is also where errors will display for classes not found
			areAllFilesFound = false;
		}
		
		return areAllFilesFound;
		
	}
	
	public static void depositCash(String userText) { //user adds cash to bank account if the number is valid and variables are updated accordingly
		
		try {
			for (int i = 0; i < userText.length(); i++) {
				if (Character.isAlphabetic(userText.charAt(i))) { //sometimes when there is a letter at the end of an input, parseDouble will not throw NumberFormatException e.g. 10f
					throw new NumberFormatException();
				}
			}
			double cash = Double.parseDouble(userText); //cannot accept string
			if (cash <= 0.0) { //no negative numbers, and 0 would not make sense
				throw new NumberFormatException();
			}
			bankAccount.deposit(cash);
			depositCashTxtFld.clear();
			balanceAmountLbl.setText(String.format("$%.2f", bankAccount.getBalance()));
			credits = bankAccount.getCredits();
			creditsAmountLbl.setText(credits + "");
			coinHandlingPlayer.play();
		}
		catch (NumberFormatException n) {
			depositCashTxtFld.requestFocus(); //set focus when possible (after user clicks out of alert)
			Alert.show("Enter a valid, positive number to deposit cash!");
			messageLbl.setText("Deposit Cash to Begin");
		}
		
	}
	
	public static void betOne() { //bet +1 if user can afford, or if max bet has been selected reset to 1
		
		int possibleBetMultiplier;
		
		if (betMultiplier + 1 <= MAX_BET) { //max bet is 3, otherwise resets to 1
			possibleBetMultiplier = betMultiplier + 1;
		} else {
			possibleBetMultiplier = 1;
		}
		
		if (bankAccount.getBalance() >= bankAccount.getDenomination() * possibleBetMultiplier) { //if user can afford bet
			betMultiplier = possibleBetMultiplier;
			betMultiplierLbl.setText(betMultiplier + "");
		} else {
			depositCashTxtFld.requestFocus();
			Alert.show("You do not have enough money to bet!");
			messageLbl.setText("Deposit Cash to Begin");
		}
		
	}
	
	public static void maxBet() { //bet the maximum amount of credits that user can afford
		
		boolean userCanAfford = false;
		int i = MAX_BET;
		
		while (!userCanAfford && i > 0) {
			if (bankAccount.getBalance() >= bankAccount.getDenomination() * i) {
				betMultiplier = i;
				betMultiplierLbl.setText(betMultiplier + "");
				userCanAfford = true; //exit loop for maximum bet
			} else {
				i--; //if user can't afford max bet 3, try max bet 2, etc.
			}
		}
		
		if (!userCanAfford) { //if the user can't afford to bet anything
			depositCashTxtFld.requestFocus();
			Alert.show("You do not have enough money to bet!");
			messageLbl.setText("Deposit Cash to Begin");
		}
		
	}
	
	public static void spinTumblers() { //spin and display tumblers if user can afford, then call computeWinnings to calculate and display winnings
		
		double betAmount =  bankAccount.getDenomination() * betMultiplier;
		
		if (bankAccount.getBalance() < betAmount) { //if there is not enough money to bet
			depositCashTxtFld.requestFocus();
			Alert.show("You do not have enough money in your account to spin the machine!");
			messageLbl.setText("Deposit Cash to Begin");
		} else {
			bankAccount.withdraw(betAmount); //user bets money
			Tumbler.ValueEnum[] slotResults = new Tumbler.ValueEnum[3]; //results for each slot
			
			for (int i = 0; i < 3; i++) {
				Tumbler.ValueEnum result = slots[i].spin();
				slotResults[i] = result;
				if (result == Tumbler.ValueEnum.CHERRIES) {
					slotImages[i].setImage(cherries);
				} else if (result == Tumbler.ValueEnum.GRAPES) {
					slotImages[i].setImage(grapes);
				} else if (result == Tumbler.ValueEnum.BANANA) {
					slotImages[i].setImage(banana);
				} else {
					slotImages[i].setImage(blankFruit);
				}
			}
			
			computeWinnings(slotResults, betAmount);
			
		}
		
	}
	
	public static void computeWinnings(Tumbler.ValueEnum[] slotResults, double betAmount) { //calculate and display winnings, and update variables
		
		double winnings = 0.0;
		
		int numBlanks = 0;
		for (int i = 0; i < slotResults.length; i++) { //check if there are 2 or 3 blanks
			if (slotResults[i] == Tumbler.ValueEnum.BLANK) {
				numBlanks++;
			}
		}
		
		if (numBlanks < 2) {
			if (slotResults[0] == slotResults[1] && slotResults[1] == slotResults[2]) { //all matching that isn't blanks
				win2SoundPlayer.play();
				if (slotResults[0] == Tumbler.ValueEnum.CHERRIES) { //all cherries
					winnings = betAmount * 100; //100x
					messageLbl.setText("Jackpot!!!! You win " + String.format("$%.2f", winnings) + "!!!!");
				} else if (slotResults[0] == Tumbler.ValueEnum.GRAPES) {
					winnings = betAmount * 50; //50x
					messageLbl.setText("Awesome!! You win " + String.format("$%.2f", winnings) + "!!");
				} else if (slotResults[0] == Tumbler.ValueEnum.BANANA) {
					winnings = betAmount * 10; //10x
					messageLbl.setText("Nice! You win " + String.format("$%.2f", winnings) + "!");
				}
			} else if (slotResults[0] == slotResults[1] || slotResults[1] == slotResults[2] || slotResults[0] == slotResults[2]) { //any matching pair that isn't blank
					winnings = betAmount * 2; //2x
					messageLbl.setText("You win " + String.format("$%.2f", winnings) + "!");
					win1SoundPlayer.play();
			} else {
				winnings = 0.0;
				messageLbl.setText("You did not win anything this time."); //non-matching combinations
			}
		} else {
			winnings = 0.0;
			messageLbl.setText("You did not win anything this time."); //2 or 3 blanks
		}
		
		totalWinnings += winnings;
		bankAccount.deposit(winnings);
		credits = bankAccount.getCredits();
		
		balanceAmountLbl.setText(String.format("$%.2f", bankAccount.getBalance()));
		creditsAmountLbl.setText(credits + "");
		winningsAmountLbl.setText(String.format("$%.2f", totalWinnings));
		
	}
	
	public static void printTicket() { //alert user that ticket has been printed with bank account balance, then reset values
		
		String message = "";
		
		if (bankAccount.getBalance() == 0.0) {
			message = "There is no balance to print!\nResetting Machine..."; //user can use this button to reset machine if they lose all their money
		} else {
			message = "Ticket Printed!\n\n" 
					+ "Amount: " + String.format("$%.2f", bankAccount.withdrawAll()) + "\n\n"
					+ "Thanks for playing!";
		}
		
		depositCashTxtFld.requestFocus();
		Alert.show(message);

		for (int i = 0; i < slotImages.length; i++) {
			slotImages[i].setImage(blankFruit);
		}

		balanceAmountLbl.setText(String.format("$%.2f", bankAccount.getBalance())); //will be 0.0 after withdrawAll
		betMultiplier = 3;
		betMultiplierLbl.setText(betMultiplier + "");
		messageLbl.setText("Deposit Cash to Begin");
		winningsAmountLbl.setText("$0.00");
		totalWinnings = 0.0;
		changeDenomination(0.25);
		option2.setSelected(true);
		
	}
	
	public static void changeDenomination(double newDenomination) { //change denomination and set credits
		bankAccount.setDenomination(newDenomination);
		credits = bankAccount.getCredits();
		creditsAmountLbl.setText(credits + "");
	}
	
	public static void stopSounds() { //stops sound of all media players
		for (int i = 0; i < mediaPlayers.length; i++) {
			mediaPlayers[i].stop();
		}
	}
	
	public static void main(String[] args) {
		if (allFilesFound()) {
			launch(args);
		}
	}
	
	@Override
	public void start(Stage stage) {
		
		//left area - balance
		
		Label balanceLbl = new Label("Current Balance:");
		balanceLbl.getStyleClass().add("headerLbl");
		balanceAmountLbl.getStyleClass().add("amountLbl");
		
		GridPane balanceGridPane = new GridPane(); //could be a VBox but GridPane for consistency in CSS - group header label and amount label
		balanceGridPane.addRow(0, balanceLbl);
		balanceGridPane.addRow(1, balanceAmountLbl);
		balanceGridPane.getStyleClass().add("GridPane");
		
		//left area - deposit cash
		
		Label depositCashLbl = new Label("Deposit Cash:");
		depositCashLbl.getStyleClass().add("headerLbl");
		
		Button depositCashBtn = new Button("Add");
		depositCashBtn.setDefaultButton(true); //user can press enter to activate button
		depositCashBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				stopSounds();
				depositCash(depositCashTxtFld.getText());
			}
		});
		
		GridPane depositCashGridPane = new GridPane(); //group header label, textfield, and add cash button
		depositCashGridPane.addRow(0, depositCashLbl);
		depositCashGridPane.addRow(1, depositCashTxtFld, depositCashBtn);
		depositCashGridPane.getStyleClass().add("GridPane");
		depositCashGridPane.setHgap(5); //not an option in css since gridpane does not have a default css class
		
		//left area - bet
		
		Label betLbl = new Label("Bet Amount:");
		betLbl.getStyleClass().add("headerLbl");
		betMultiplierLbl.getStyleClass().add("amountLbl");
		betMultiplierLbl.setId("betMultiplierLbl"); //a different value for length is required for this due to the button next to it
		
		Button betOneBtn = new Button("Bet");
		betOneBtn.setFocusTraversable(false); //prevents automatic focus of button so that enter key only activates depositCashBtn
		betOneBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				betOne();
			}
		});
		
		GridPane betGridPane = new GridPane(); //group header label, amount label, and bet button
		betGridPane.addRow(0, betLbl);
		betGridPane.addRow(1, betMultiplierLbl, betOneBtn);
		betGridPane.getStyleClass().add("GridPane");
		betGridPane.setHgap(5);
		
		//left area - credits
		
		Label creditsLbl = new Label("Credits:");
		creditsLbl.getStyleClass().add("headerLbl");
		creditsAmountLbl.getStyleClass().add("amountLbl");
		
		GridPane creditsGridPane = new GridPane(); //group header label and amount label
		creditsGridPane.addRow(0, creditsLbl);
		creditsGridPane.addRow(1, creditsAmountLbl);
		creditsGridPane.getStyleClass().add("GridPane");
		
		//middle area - top labels
		
		Label slotsTitle = new Label("SLOTS");
		slotsTitle.setId("slotsTitle");
		
		messageLbl.setId("messageLbl");
		//messageLbl.setLineSpacing(-8); //no css property for this - use if multiple lines
		
		VBox topLbls = new VBox(); //to set different spacing/padding than other elements in middleArea
		topLbls.getChildren().addAll(slotsTitle, messageLbl);
		topLbls.setId("topLbls");
		
		//middle area - slots
		
		HBox slotsHBox = new HBox(); //group slots
		slotsHBox.getChildren().addAll(slot1, slot2, slot3);
		slotsHBox.setId("slots");
		
		//middle area - main buttons
		
		Button maxBetBtn = new Button("Max\nBet");
		maxBetBtn.setId("maxBetBtn");
		maxBetBtn.setFocusTraversable(false);
		maxBetBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				maxBet();
			}
		});
		
		Button spinBtn = new Button("SPIN");
		spinBtn.setId("spinBtn");
		spinBtn.setFocusTraversable(false);
		spinBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				stopSounds();
				spinTumblers();
			}
		});
		
		Button cashOutBtn = new Button("Cash\nOut"); //take remaining money from machine
		cashOutBtn.setId("cashOutBtn");
		cashOutBtn.setFocusTraversable(false);
		cashOutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				stopSounds();
				printTicket();
			}
		});
		
		HBox mainButtons = new HBox(); //group main/big buttons
		mainButtons.getChildren().addAll(maxBetBtn, spinBtn, cashOutBtn);
		mainButtons.setId("mainButtons");
		
		//right area - winnings
		
		Label winningsLbl = new Label("Total Winnings:");
		winningsLbl.getStyleClass().addAll("headerLbl", "rightAreaLbl");
		winningsAmountLbl.getStyleClass().addAll("amountLbl", "rightAreaLbl");
		
		GridPane winningsGridPane = new GridPane(); //group header label and amount label
		winningsGridPane.addRow(0, winningsLbl);
		winningsGridPane.addRow(1, winningsAmountLbl);
		winningsGridPane.getStyleClass().add("GridPane");
		
		//right area - payout menu/gridpane - lets user know the winning percent value of bet based on slot results
		
		Label slotComboLbl = new Label("Slot Combo:");
		slotComboLbl.getStyleClass().addAll("payoutHeader", "payoutLbls");
		Label pairLbl = new Label("Any pair"); //besides a blank fruit
		pairLbl.getStyleClass().add("payoutLbls");
		
		//imageviews can have same image but must have separate names
		
		ImageView banana1 = new ImageView(smallBanana);
		ImageView banana2 = new ImageView(smallBanana);
		ImageView banana3 = new ImageView(smallBanana);
		
		ImageView grapes1 = new ImageView(smallGrapes);
		ImageView grapes2 = new ImageView(smallGrapes);
		ImageView grapes3 = new ImageView(smallGrapes);
		
		ImageView cherries1 = new ImageView(smallCherries);
		ImageView cherries2 = new ImageView(smallCherries);
		ImageView cherries3 = new ImageView(smallCherries);
		
		//group imageviews
		HBox threeBananas = new HBox();
		threeBananas.getChildren().addAll(banana1, banana2, banana3);
		HBox threeGrapes = new HBox();
		threeGrapes.getChildren().addAll(grapes1, grapes2, grapes3);
		HBox threeCherries = new HBox();
		threeCherries.getChildren().addAll(cherries1, cherries2, cherries3);
		
		Label payoutLbl = new Label("Payout:"); //might change values in programming project 2
		payoutLbl.getStyleClass().addAll("payoutHeader", "payoutAmounts");
		Label payout1 = new Label("2x");
		payout1.getStyleClass().add("payoutAmounts");
		Label payout2 = new Label("10x");
		payout2.getStyleClass().add("payoutAmounts");
		Label payout3 = new Label("50x");
		payout3.getStyleClass().add("payoutAmounts");
		Label payout4 = new Label("100x");
		payout4.getStyleClass().add("payoutAmounts");
		
		GridPane payoutGridPane = new GridPane(); //payout chart
		payoutGridPane.addRow(0, slotComboLbl, payoutLbl);
		payoutGridPane.addRow(1, pairLbl, payout1);
		payoutGridPane.addRow(2, threeBananas, payout2);
		payoutGridPane.addRow(3, threeGrapes, payout3);
		payoutGridPane.addRow(4, threeCherries, payout4);
		payoutGridPane.getStyleClass().add("GridPane");
		payoutGridPane.setHgap(5);
		payoutGridPane.setVgap(5);
		
		//right area - denomination
		
		Label denominationLbl = new Label("Denomination: ");
		denominationLbl.getStyleClass().addAll("headerLbl", "rightAreaLbl");
		option1.setFocusTraversable(false);
		option1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				changeDenomination(0.05);
			}
		});
		
		option2.setSelected(true); //default selection
		option2.setFocusTraversable(false);
		option2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				changeDenomination(0.25);
			}
		});
		
		option3.setFocusTraversable(false);
		option3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				changeDenomination(0.50);
			}
		});
		
		option4.setFocusTraversable(false);
		option4.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				changeDenomination(1.00);
			}
		});
		
		ToggleGroup denominationToggleGroup = new ToggleGroup(); //group radio buttons so only one may be selected at a time
		option1.setToggleGroup(denominationToggleGroup);
		option2.setToggleGroup(denominationToggleGroup);
		option3.setToggleGroup(denominationToggleGroup);
		option4.setToggleGroup(denominationToggleGroup);
		
		GridPane radioButtonsGridPane = new GridPane(); //group radio buttons
		radioButtonsGridPane.addRow(0, option1, option2);
		radioButtonsGridPane.addRow(1, option3, option4);
		radioButtonsGridPane.getStyleClass().add("GridPane");
		radioButtonsGridPane.setHgap(10);
		radioButtonsGridPane.setVgap(5);
		
		GridPane denominationGridPane = new GridPane(); //group label and radio button gridpane
		denominationGridPane.addRow(0,denominationLbl);
		denominationGridPane.addRow(1, radioButtonsGridPane);
		denominationGridPane.getStyleClass().add("GridPane");
		
		//GUI layout/setup
		
		VBox leftArea = new VBox();
		leftArea.getChildren().addAll(balanceGridPane, depositCashGridPane, betGridPane, creditsGridPane);
		leftArea.setId("leftArea");
		
		VBox middleArea = new VBox();
		middleArea.getChildren().addAll(topLbls, slotsHBox, mainButtons);
		middleArea.setId("middleArea");
		
		VBox rightArea = new VBox();
		rightArea.getChildren().addAll(winningsGridPane, payoutGridPane, denominationGridPane);
		rightArea.setId("rightArea");
		
		HBox root = new HBox();
		root.getChildren().addAll(leftArea, middleArea, rightArea);
		root.setStyle("-fx-background-color: #fffbe5"); //set background color of application
		Scene scene = new Scene(root, 920, 500); //around half fullscreen size
		scene.getStylesheets().add(getClass().getResource("SlotMachineEmulatorCSS.css").toExternalForm()); //add css
		stage.setScene(scene);
		stage.setResizable(false); //prevents window from being resized and contents being distorted
		stage.setTitle("Slot Machine Emulator");
		stage.show();
		
	}
	
}
