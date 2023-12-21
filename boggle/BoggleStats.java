package boggle;

import java.util.HashSet;
import java.util.Set;

/**
 * The BoggleStats class for the first Assignment in CSC207, Fall 2022
 * The BoggleStats will contain statsitics related to game play Boggle 
 */
public class BoggleStats {

    /**
     * set of words the player finds in a given round 
     */  
    private Set<String> playerWords = new HashSet<String>();  
    /**
     * set of words the computer finds in a given round 
     */  
    private Set<String> computerWords = new HashSet<String>();  
    /**
     * the player's score for the current round
     */  
    private int pScore; 
    /**
     * the computer's score for the current round
     */  
    private int cScore; 
    /**
     * the player's total score across every round
     */  
    private int pScoreTotal; 
    /**
     * the computer's total score across every round
     */  
    private int cScoreTotal; 
    /**
     * the average number of words, per round, found by the player
     */  
    private double pAverageWords; 
    /**
     * the average number of words, per round, found by the computer
     */  
    private double cAverageWords; 
    /**
     * the current round being played
     */  
    private int round; 

    /**
     * enumarable types of players (human or computer)
     */  
    public enum Player {
        Human("Human"),
        Computer("Computer");
        private final String player;
        Player(final String player) {
            this.player = player;
        }
    }

    /* BoggleStats constructor
     * ----------------------
     * Sets round, totals and averages to 0.
     * Initializes word lists (which are sets) for computer and human players.
     */
    public BoggleStats() {
        this.playerWords = new HashSet<String>();
        this.computerWords = new HashSet<String>();
        this.round = 0;
        this.pScore = 0;
        this.cScore = 0;
        this.cScoreTotal = 0;
        this.pScoreTotal = 0;
        this.cAverageWords = 0;
        this.pAverageWords = 0;

    }

    /* 
     * Add a word to a given player's word list for the current round.
     * You will also want to increment the player's score, as words are added.
     *
     * @param word     The word to be added to the list
     * @param player  The player to whom the word was awarded
     */
    public void addWord(String word, Player player) {
        int point = word.length() - 3; // A string of length 4 gives 1 point, then each additional letter adds 1 extra point
        if (player == Player.Computer){
            this.cScore += point;
            this.computerWords.add(word); // The word is added to all the words found by the Player
        }
        else{
            this.pScore += point;
            this.playerWords.add(word);
        }
    }

    /* 
     * End a given round.
     * This will clear out the human and computer word lists, so we can begin again.
     * The function will also update each player's total scores, average scores, and
     * reset the current scores for each player to zero.
     * Finally, increment the current round number by 1.
     */
    public void endRound() {
    // If it is not the first round, the cumulative avg formula is used
        if (!(this.round == 0)){
            int p_total_words_before = (int) ((this.round)* this.pAverageWords);
            int c_total_words_before = (int) ((this.round)* this.cAverageWords);
            this.cAverageWords = (c_total_words_before + cScore) / (this.round + 1);
            this.pAverageWords = (p_total_words_before + pScore) / (this.round + 1);}
        else{ //If it is the first round, the average is all the words that are found
            this.cAverageWords = this.computerWords.size();
            this.pAverageWords = this.playerWords.size();}
        this.playerWords.clear();
        this.computerWords.clear();
        this.pScoreTotal += this.pScore;
        this.cScoreTotal += this.cScore;

        this.pScore = 0;
        this.cScore = 0;
        this.round += 1;

    }

    /* 
     * Summarize one round of boggle.  Print out:
     * The words each player found this round.
     * Each number of words each player found this round.
     * Each player's score this round.
     */
    public void summarizeRound() {
        System.out.println("The number of the words found by you: " + this.playerWords.size());
        System.out.println("The words you have found are: " + this.playerWords);
        System.out.println("Your score: " + this.pScore);

        System.out.println("The number of the words found by the computer: " + this.computerWords.size());
        System.out.println("The words the computer have found are: " + this.computerWords);
        System.out.println("The computer's score: " + this.cScore);

    }

    /* 
     * Summarize the entire boggle game.  Print out:
     * The total number of rounds played.
     * The total score for either player.
     * The average number of words found by each player per round.
     */
    public void summarizeGame() {
        System.out.println("Total number of rounds played: " + this.round);

        System.out.println("The average number of words you have found: " + this.pAverageWords);
        System.out.println("Your total score: " + this.pScoreTotal);

        System.out.println("The average number of words the computer has found: " + this.cAverageWords);
        System.out.println("Computer's total score: " + this.cScoreTotal);

    }

    /* 
     * @return Set<String> The player's word list
     */
    public Set<String> getPlayerWords() {
        return this.playerWords;
    }

    /*
     * @return int The number of rounds played
     */
    public int getRound() { return this.round; }

    /*
    * @return int The current player score
    */
    public int getScore() {
        return this.pScore;
    }

}
