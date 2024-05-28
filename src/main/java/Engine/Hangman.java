package Engine;

import Shared.Utils;

public class Hangman {
    private static final char[] ALPHABET = "abcdefghijklmnoprstuvwxyz".toCharArray();
    private static final int DEFAULT_ZYCIA = 8;

    private int zycia;
    private char[] currentAlphabet;
    private String currentWord;
    private char[] playerWord;
    private int guessCount;
    private String playerName;

    public Hangman(String word, String playerName) {
        currentWord = word;
        playerWord = new char[word.length()];
        currentAlphabet = new char[ALPHABET.length];
        zycia = DEFAULT_ZYCIA;
        guessCount = 0;
        System.arraycopy(ALPHABET, 0, currentAlphabet, 0, ALPHABET.length);

        for (int i = 0; i < playerWord.length; i++) {
            playerWord[i] = '-';
        }

        this.playerName = playerName;
    }

    public boolean hasLost() {
        return zycia == 0;
    }

    public boolean hasWon() {
        return currentWord.equals(String.valueOf(playerWord));
    }

    public void guessLetter(char c) {
        for (int i = 0; i < currentAlphabet.length; i++) {
            if (c == currentAlphabet[i]) {
                fillIn(c);
                currentAlphabet[i] = ' ';
                guessCount++;
            }
        }
    }

    int getGuessCount() {
        return guessCount;
    }

    public void drawHangman() {
        int currentImage = 8 - zycia;

        if (zycia != 8) {
            System.out.println("==========WISIELEC==============");
            System.out.println("================================");
        }

        for (int line = 0; line < 6 && zycia < 8; line++) {
            int start = line * 10;
            System.out.println(Utils.image[currentImage].substring(start, start + 10));
        }

        System.out.println("================================");
        System.out.println("Dostepne litery: ");
        System.out.println(currentAlphabet);
        System.out.println("================================");

        System.out.println("Twoje slowo: " + String.valueOf(playerWord));
        System.out.println("Masz " + zycia + " zycia.");

        System.out.println("================================");

        if (zycia == 0) {
            System.out.println("Przegrales :< .");
        }
    }

    private void fillIn(char c) {
        boolean foundLetter = false;

        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == c) {
                playerWord[i] = c;
                foundLetter = true;
            }
        }

        if (!foundLetter) zycia--;
    }

    public void addScore(DatabaseConnection db) {
        db.addNewRecord(playerName, guessCount, zycia);
    }

    public String getCurrentWord() {
        return currentWord;
    }
}