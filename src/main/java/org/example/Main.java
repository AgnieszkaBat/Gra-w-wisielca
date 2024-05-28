package org.example;

import Engine.DatabaseConnection;
import Engine.Hangman;
import Shared.ActionTypeEnum;
import Shared.Models.Score;
import Shared.Utils;

import java.util.*;

public class Main {
    private static  final String connectionString = "jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=StrongPass1!";
    private static Scanner scan = new Scanner(System.in);
    private static ActionTypeEnum currentAction = ActionTypeEnum.MENU;
    private static DatabaseConnection dbConnection;

    public static void main(String[] args) {
        dbConnection = new DatabaseConnection(connectionString);

        while (currentAction != ActionTypeEnum.EXIT) {
            switch (currentAction) {
                case MENU:
                    currentAction = vMainMenu();
                    break;
                case GAME:
                    currentAction = vGameLoop();
                    break;

                case DICTIONARY:
                    currentAction = vDictionary();
                    break;

                case SCORE:
                    currentAction = printScores();
                    break;

                default:
                    currentAction = vMainMenu();
                    break;
            }
        }
    }

    static private ActionTypeEnum vMainMenu() {
        String input;
        System.out.println("--");
        System.out.println("Gra w wisielca");
        System.out.println("--");
        System.out.println("1. Graj");
        System.out.println("2. Wyniki");
        System.out.println("3. Slownik");
        System.out.println("4. Wyjdz");
        System.out.print("Wybierz funkcje: ");

        input = scan.nextLine().toLowerCase();
        var intValue = Character.getNumericValue(input.charAt(0));

        return switch (intValue) {
            case 1 -> ActionTypeEnum.GAME;
            case 2 -> ActionTypeEnum.SCORE;
            case 3 -> ActionTypeEnum.DICTIONARY;
            case 4 -> ActionTypeEnum.EXIT;
            default -> {
                System.out.println("!! Podano nipoprawny numer !!");
                yield ActionTypeEnum.MENU;
            }
        };
    }

    static private ActionTypeEnum vDictionary() {
        String input = " ";

        while (input.charAt(0) != 'x') {
            System.out.println("Slownik");
            System.out.println("1). Pokaz slowa");
            System.out.println("2). Dodaj slowo");
            System.out.println("X). Cofnij menu");
            System.out.print("Wybierz opcje: ");

            input = scan.nextLine().toLowerCase();

            switch (Character.getNumericValue(input.charAt(0))) {
                case 1:
                    System.out.println("Wybierz poziom trudności:");
                    System.out.println("1).Easy 2).Medium 3).Hard 4).Very Hard");
                    int intLevel = 0;
                    String level = "";
                    boolean validInput = false;

                    while (!validInput) {
                        level = scan.nextLine();
                        validInput = Utils.checkIntInDifficultyEnumRange(Character.getNumericValue(level.charAt(0)));
                    }

                    intLevel = Character.getNumericValue(level.charAt(0));

                    var words = dbConnection.getDictionary(intLevel);
                    System.out.println("------");
                    words.forEach(System.out::println);
                    System.out.println("-------");
                    break;
                case 2:
                    boolean validNewWordInput = false;
                    while (!validNewWordInput) {
                        System.out.print("Wpisz nowe slowo: ");
                        input = scan.nextLine().toLowerCase();

                        validNewWordInput = true;
                        for (char c : input.toCharArray()) {
                            if (!Character.isAlphabetic(c)) validInput = false;
                        }
                    }

                    boolean validNewWordsDifficulty = false;
                    String inputLvl = "";

                    while (!validNewWordsDifficulty) {
                        System.out.print("Podaj poziom trudności: ");
                        inputLvl = scan.nextLine().toLowerCase();

                        validNewWordsDifficulty = Utils.checkIntInDifficultyEnumRange(Character.getNumericValue(inputLvl.charAt(0)));
                    }

                    dbConnection.addNewWordToDictionary(input, Character.getNumericValue(inputLvl.charAt(0)));
                    input = " ";
                    System.out.println("------------------------");
                    break;
                case 'x' | 'X':
                    return ActionTypeEnum.MENU;
            }
        }

        return ActionTypeEnum.MENU;
    }

    static private ActionTypeEnum vGameLoop() {
        String playerName;
        System.out.println("Podaj nazwę gracza");
        playerName = scan.nextLine();

        System.out.println("Podaj poziom trudności: ");
        System.out.println("0).Latwy 1).Sredni 2).Trudny 3). Bardzo trudny: ");
        String lvl = "";
        boolean validInput = false;

        while (!validInput) {
            lvl = scan.nextLine();
            validInput = Utils.checkIntInDifficultyEnumRange(Character.getNumericValue(lvl.charAt(0)));

            if (!validInput) {
                System.err.println("Niepoprawny poziom");
                lvl = "";
            }
        }

        Random random = new Random();
        var dictionary = dbConnection.getDictionary(Character.getNumericValue(lvl.charAt(0)));

        if (dictionary.size() == 0) {
            System.err.println("Brak słów w bazie danych o podanym poziomie trudności");
            return ActionTypeEnum.END;
        }

        int randomIndex = random.nextInt(dictionary.size());
        String randomWord = dictionary.get(randomIndex);

        String input;
        Hangman hangman = new Hangman(randomWord, playerName);
        hangman.drawHangman();

        System.out.print("Zgadnij litere, albo wybierz wyjdz: ");
        input = scan.nextLine().toLowerCase();

        while (!input.equals("x") && !hangman.hasLost() && !hangman.hasWon()) {
            hangman.guessLetter(input.charAt(0));

            hangman.drawHangman();
            if (hangman.hasWon() || hangman.hasLost()) {
                break;
            }

            System.out.print("Zgadnij slowo: ");
            input = scan.nextLine().toLowerCase();
        }

        System.out.println("Przegrales");
        if (hangman.hasWon()) {
            System.out.println("Brawo, wygrałeś!!! Twoje slowo to: " + hangman.getCurrentWord());

            String addScoreToDb;
            System.out.println("Czy chcesz dodać wynik do rankingu? 1). Tak 2). Nie");
            addScoreToDb = scan.nextLine();

            if (addScoreToDb.charAt(0) != '1') {
                return ActionTypeEnum.MENU;
            }

            hangman.addScore(dbConnection);

            return ActionTypeEnum.MENU;
        }

        System.out.println("Przegrales :< Twoje slowo to: " + hangman.getCurrentWord());
        System.out.print("Nacisnij 1). aby kontunuowac; Nacisnij 2). aby wyjsc");

        String isEnding;
        isEnding = scan.nextLine();

        return (isEnding.charAt(0) == '1') ? ActionTypeEnum.GAME : ActionTypeEnum.MENU;
    }

    static ActionTypeEnum printScores() {
        System.out.println("================================");
        System.out.println("Wyniki");
        System.out.println("================================");

        var scores = dbConnection.getAllRecords();

        if (scores.isEmpty()) System.out.println("Nie ma wynikow");
        scores.sort(Comparator.comparingInt(s -> s.attempts));

        for (int recordIndex = 0; recordIndex < scores.size(); recordIndex++) {
            Score score = scores.get(recordIndex);
            System.out.println(recordIndex + ". " + score.name + " Attempts: " + score.attempts + " Lives: " + score.lives);
        }

        return ActionTypeEnum.MENU;
    }
}