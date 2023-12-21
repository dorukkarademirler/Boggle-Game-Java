package boggle;

import java.util.*;

/**
 * The BoggleGame class for the first Assignment in CSC207, Fall 2022
 */
public class BoggleGame {

    /**
     * scanner used to interact with the user via console
     */
    public Scanner scanner;
    /**
     * stores game statistics
     */
    private BoggleStats gameStats;

    /**
     * dice used to randomize letter assignments for a small grid
     */
    private final String[] dice_small_grid= //dice specifications, for small and large grids
            {"AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS", "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
                    "DISTTY", "EEGHNW", "EEINSU", "EHRTVW", "EIOSST", "ELRTTY", "HIMNQU", "HLNNRZ"};
    /**
     * dice used to randomize letter assignments for a big grid
     */
    private final String[] dice_big_grid =
            {"AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM", "AEEGMU", "AEGMNN", "AFIRSY",
                    "BJKQXZ", "CCNSTW", "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DDHNOT", "DHHLOR",
                    "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU", "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"};

    /*
     * BoggleGame constructor
     */
    public BoggleGame() {
        this.scanner = new Scanner(System.in);
        this.gameStats = new BoggleStats();
    }

    /*
     * Provide instructions to the user, so they know how to play the game.
     */
    public void giveInstructions()
    {
        System.out.println("The Boggle board contains a grid of letters that are randomly placed.");
        System.out.println("We're both going to try to find words in this grid by joining the letters.");
        System.out.println("You can form a word by connecting adjoining letters on the grid.");
        System.out.println("Two letters adjoin if they are next to each other horizontally, ");
        System.out.println("vertically, or diagonally. The words you find must be at least 4 letters long, ");
        System.out.println("and you can't use a letter twice in any single word. Your points ");
        System.out.println("will be based on word length: a 4-letter word is worth 1 point, 5-letter");
        System.out.println("words earn 2 points, and so on. After you find as many words as you can,");
        System.out.println("I will find all the remaining words.");
        System.out.println("\nHit return when you're ready...");
    }


    /*
     * Gets information from the user to initialize a new Boggle game.
     * It will loop until the user indicates they are done playing.
     */
    public void playGame(){
        int boardSize;
        while(true){
            System.out.println("Enter 1 to play on a big (5x5) grid; 2 to play on a small (4x4) one:");
            String choiceGrid = scanner.nextLine();

            //get grid size preference
            if(choiceGrid == "") break; //end game if user inputs nothing
            while(!choiceGrid.equals("1") && !choiceGrid.equals("2")){
                System.out.println("Please try again.");
                System.out.println("Enter 1 to play on a big (5x5) grid; 2 to play on a small (4x4) one:");
                choiceGrid = scanner.nextLine();
            }

            if(choiceGrid.equals("1")) boardSize = 5;
            else boardSize = 4;

            //get letter choice preference
            System.out.println("Enter 1 to randomly assign letters to the grid; 2 to provide your own.");
            String choiceLetters = scanner.nextLine();

            if(choiceLetters == "") break; //end game if user inputs nothing
            while(!choiceLetters.equals("1") && !choiceLetters.equals("2")){
                System.out.println("Please try again.");
                System.out.println("Enter 1 to randomly assign letters to the grid; 2 to provide your own.");
                choiceLetters = scanner.nextLine();
            }

            if(choiceLetters.equals("1")){
                playRound(boardSize,randomizeLetters(boardSize));
            } else {
                System.out.println("Input a list of " + boardSize*boardSize + " letters:");
                choiceLetters = scanner.nextLine();
                while(!(choiceLetters.length() == boardSize*boardSize)){
                    System.out.println("Sorry, bad input. Please try again.");
                    System.out.println("Input a list of " + boardSize*boardSize + " letters:");
                    choiceLetters = scanner.nextLine();
                }
                playRound(boardSize,choiceLetters.toUpperCase());
            }

            //round is over! So, store the statistics, and end the round.
            this.gameStats.summarizeRound();
            this.gameStats.endRound();

            //Shall we repeat?
            System.out.println("Play again? Type 'Y' or 'N'");
            String choiceRepeat = scanner.nextLine().toUpperCase();

            if(choiceRepeat == "") break; //end game if user inputs nothing
            while(!choiceRepeat.equals("Y") && !choiceRepeat.equals("N")){
                System.out.println("Please try again.");
                System.out.println("Play again? Type 'Y' or 'N'");
                choiceRepeat = scanner.nextLine().toUpperCase();
            }

            if(choiceRepeat == "" || choiceRepeat.equals("N")) break; //end game if user inputs nothing

        }

        //we are done with the game! So, summarize all the play that has transpired and exit.
        this.gameStats.summarizeGame();
        System.out.println("Thanks for playing!");
    }

    /*
     * Play a round of Boggle.
     * This initializes the main objects: the board, the dictionary, the map of all
     * words on the board, and the set of words found by the user. These objects are
     * passed by reference from here to many other functions.
     */
    public void playRound(int size, String letters){
        //step 1. initialize the grid
        BoggleGrid grid = new BoggleGrid(size);
        grid.initalizeBoard(letters);
        //step 2. initialize the dictionary of legal words
        Dictionary boggleDict = new Dictionary("wordlist.txt"); //you may have to change the path to the wordlist, depending on where you place it.
        //step 3. find all legal words on the board, given the dictionary and grid arrangement.
        Map<String, ArrayList<Position>> allWords = new HashMap<String, ArrayList<Position>>();
        findAllWords(allWords, boggleDict, grid);
        //step 4. allow the user to try to find some words on the grid
        humanMove(grid, allWords);
        //step 5. allow the computer to identify remaining words
        computerMove(allWords);
    }

    /*
     * This method should return a String of letters (length 16 or 25 depending on the size of the grid).
     * There will be one letter per grid position, and they will be organized left to right,
     * top to bottom. A strategy to make this string of letters is as follows:
     * -- Assign a one of the dice to each grid position (i.e. dice_big_grid or dice_small_grid)
     * -- "Shuffle" the positions of the dice to randomize the grid positions they are assigned to
     * -- Randomly select one of the letters on the given die at each grid position to determine
     *    the letter at the given position
     *
     * @return String a String of random letters (length 16 or 25 depending on the size of the grid)
     */
    private String randomizeLetters(int size) {
        String[][] arr = new String[size][size]; // A two-dimensional array representing the grid positions
        Random rand = new Random(); // randomizer is created
        int counter = 0;
        StringBuilder result = new StringBuilder();

        for (int row = 0; row < size; row ++){
            for (int col = 0; col < size; col++){
                if (size == 4){
                    arr[row][col] = dice_small_grid[counter]; // Each dice is assigned to the grids in order
                } else if (size == 5) {
                    arr[row][col] = dice_big_grid[counter];
                }
                counter += 1;}} // Counter advances to go from the nth dice to (n+1), so the same dice isn't assigned again.

        for (int row = 0; row < size; row ++){
            for (int col = 0; col < size; col ++){
                int rand_row = (int) ((Math.random() * ((size-1) - row))+ row); //This randomizer mixes the grid locations
                int rand_col = (int) ((Math.random() * ((size-1) - col))+ col); // of the dices
                String s1 = arr[rand_row][rand_col];
                String s2 = arr[row][col];
                arr[row][col] = s1;
                arr[rand_row][rand_col] = s2;
                int random_int = rand.nextInt(6); // A random number from 0-6 is chosen to pick a random letter from
                result.append(arr[row][col].substring(random_int, random_int + 1)); // a string with length = 6
            }
        }
        return result.toString();
    }


    /*
     * This should be a recursive function that finds all valid words on the boggle board.
     * Every word should be valid (i.e. in the boggleDict) and of length 4 or more.
     * Words that are found should be entered into the allWords HashMap.  This HashMap
     * will be consulted as we play the game.
     *
     * Note that this function will be a recursive function.  You may want to write
     * a wrapper for your recursion. Note that every legal word on the Boggle grid will correspond to
     * a list of grid positions on the board, and that the Position class can be used to represent these
     * positions. The strategy you will likely want to use when you write your recursion is as follows:
     * -- At every Position on the grid:
     * ---- add the Position of that point to a list of stored positions
     * ---- if your list of stored positions is >= 4, add the corresponding word to the allWords Map
     * ---- recursively search for valid, adjacent grid Positions to add to your list of stored positions.
     * ---- Note that a valid Position to add to your list will be one that is either horizontal, diagonal, or
     *      vertically touching the current Position
     * ---- Note also that a valid Position to add to your list will be one that, in conjunction with those
     *      Positions that precede it, form a legal PREFIX to a word in the Dictionary (this is important!)
     * ---- Use the "isPrefix" method in the Dictionary class to help you out here!!
     * ---- Positions that already exist in your list of stored positions will also be invalid.
     * ---- You'll be finished when you have checked EVERY possible list of Positions on the board, to see
     *      if they can be used to form a valid word in the dictionary.
     * ---- Food for thought: If there are N Positions on the grid, how many possible lists of positions
     *      might we need to evaluate?
     *
     * @param allWords A mutable list of all legal words that can be found, given the boggleGrid grid letters
     * @param boggleDict A dictionary of legal words
     * @param boggleGrid A boggle grid, with a letter at each position on the grid
     */
    private void findAllWords(Map<String,ArrayList<Position>> allWords, Dictionary boggleDict, BoggleGrid boggleGrid) {
        String new_word = "";
        var position_list = new ArrayList<Position>();
        for (int row = 0; row < boggleGrid.numRows(); row++){ //nested for loop is used to make a start from
            for (int col = 0; col < boggleGrid.numCols(); col++){  //every single grid position
                position_list.add(new Position(row, col));
                new_word += String.valueOf(boggleGrid.getCharAt(row, col)); // the grid's letter and position is assigned
                scanTheGrid(allWords, boggleDict, boggleGrid, row, col, new_word, position_list); //helper is called to find all words
                new_word = ""; //parameters are reset to make a fresh start with the new grid
                position_list.clear();

            }
        }}

    private Boolean scanTheGridHelper(ArrayList<Position> position, Position pos) {
        /*
        This function is used to see if two positions' columns and rows match
         */
        for (Position value : position) {
            if (value.row == pos.getRow(0) && value.col == pos.getCol(0)) {
                return true;
            }
        }
        return false;
    }

    private void scanTheGrid(Map<String,ArrayList<Position>> allWords, Dictionary boggleDict, BoggleGrid boggleGrid, int row, int col, String new_word, ArrayList<Position> position_list) {
        if (boggleDict.containsWord(new_word) && new_word.length() >= 4){ //necessary conditions for the word to be added
            allWords.put(new_word.toUpperCase(), position_list);
        }

        if (boggleDict.isPrefix(new_word)) { //is Prefix shortens the runtime since unnecessary words don't enter the recursion
            if (row + 1 < boggleGrid.numRows()) { //checks if the row can be advanced
                if (!scanTheGridHelper(position_list, new Position(row + 1 , col))){ //Checking if the new position is already in the list,
                    // If the position is in the list already, the function shouldn't advance more since it would mean going back to the same direction.
                    position_list.add(new Position(row + 1, col));
                    new_word += String.valueOf(boggleGrid.getCharAt(row + 1, col)); // new grid value is added
                    scanTheGrid(allWords, boggleDict, boggleGrid, row + 1, col, new_word, position_list); //recursive function is called with the new word and position
                    position_list.remove(position_list.size() - 1); // position is reset to avoid any duplicates
                    new_word = new_word.substring(0, new_word.length() - 1); //the new_word is reset to advance to other grids.
                }}

        //The same steps are used 7 more times as there are 8 different possible directions to goo from a grid.


            if (col + 1 < boggleGrid.numCols()){
                if (!scanTheGridHelper(position_list, new Position(row , col + 1))) {
                    position_list.add(new Position(row, col + 1));
                    new_word += String.valueOf(boggleGrid.getCharAt(row, col + 1));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row, col + 1, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                    new_word = new_word.substring(0, new_word.length() - 1);
                }}

            if (((row + 1) < boggleGrid.numRows()) && ((col + 1) < boggleGrid.numCols())) {
                if (!scanTheGridHelper(position_list, new Position(row + 1 , col + 1))){
                    position_list.add(new Position(row + 1, col + 1));
                    new_word += String.valueOf(boggleGrid.getCharAt(row + 1, col + 1));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row + 1, col + 1, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                    new_word = new_word.substring(0, new_word.length() - 1);
                }
            }

            if (((row - 1) >= 0) && ((col + 1 < boggleGrid.numCols()))) {
                if (!scanTheGridHelper(position_list, new Position(row - 1 , col + 1))){
                    position_list.add(new Position(row - 1, col + 1));
                    new_word += String.valueOf(boggleGrid.getCharAt(row - 1, col + 1));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row - 1, col + 1, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                    new_word = new_word.substring(0, new_word.length() - 1);
                }}

            if ((row - 1) >= 0) {
                if (!scanTheGridHelper(position_list, new Position(row - 1 , col))) {
                    position_list.add(new Position(row - 1, col));
                    new_word += String.valueOf(boggleGrid.getCharAt(row - 1, col));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row - 1, col, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                    new_word = new_word.substring(0, new_word.length() - 1);

                }
            }
            if (((row - 1) >= 0) && ((col - 1) >= 0)) {
                if (!scanTheGridHelper(position_list, new Position(row - 1 , col - 1))) {
                    position_list.add(new Position(row - 1, col - 1));
                    new_word += String.valueOf(boggleGrid.getCharAt(row - 1, col - 1));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row - 1, col - 1, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                    new_word = new_word.substring(0, new_word.length() - 1);
                }
            }

            if (col - 1 >= 0) {
                if (!scanTheGridHelper(position_list, new Position(row , col - 1))){
                    position_list.add(new Position(row, col - 1));
                    new_word += String.valueOf(boggleGrid.getCharAt(row, col - 1));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row, col - 1, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                    new_word = new_word.substring(0, new_word.length() - 1);
                }
            }

            if (((row + 1) < boggleGrid.numRows()) && ((col - 1 >= 0))) {
                if (!scanTheGridHelper(position_list, new Position(row + 1 , col - 1))) {
                    position_list.add(new Position(row + 1, col - 1));
                    new_word += String.valueOf(boggleGrid.getCharAt(row + 1, col - 1));
                    scanTheGrid(allWords, boggleDict, boggleGrid, row + 1, col - 1, new_word, position_list);
                    position_list.remove(position_list.size() - 1);
                }
            }
            }
    }





    /*
     * Gets words from the user.  As words are input, check to see that they are valid.
     * If yes, add the word to the player's word list (in boggleStats) and increment
     * the player's score (in boggleStats).
     * End the turn once the user hits return (with no word).
     *
     * @param board The boggle board
     * @param allWords A mutable list of all legal words that can be found, given the boggleGrid grid letters
     */
    private void humanMove(BoggleGrid board, Map<String,ArrayList<Position>> allWords){

        System.out.println("It's your turn to find some words!");
        System.out.println(board.toString());
        while(true) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter your word!");
            String answer = scan.nextLine(); //input is taken
            if (allWords.containsKey(answer.toUpperCase()) && (!gameStats.getPlayerWords().contains(answer.toUpperCase()))){
                //checked if the answer is in playerWords, if it is, the answer can't be counted
                this.gameStats.addWord(answer.toUpperCase(), BoggleStats.Player.Human);
                System.out.println("You found a word, your turn again!");
                 }
            else{
                System.out.println("Wrong answer, try again!");
            }
            if (answer.equals("")){ //If the user hits return, the turn ends
                break;
            }
        }
    }


    /*
     * Gets words from the computer.  The computer should find words that are
     * both valid and not in the player's word list.  For each word that the computer
     * finds, update the computer's word list and increment the
     * computer's score (stored in boggleStats).
     *
     * @param allWords A mutable list of all legal words that can be found, given the boggleGrid grid letters
     */
    private void computerMove(Map<String,ArrayList<Position>> all_words){
        var player_words = gameStats.getPlayerWords();
        for (String word : all_words.keySet()) { // for loop over all the words in all_words
            if (!player_words.contains(word)) { // If the player couldn't find the word, the computer picks the word
                this.gameStats.addWord(word, BoggleStats.Player.Computer);
            }
        }
    }

}



