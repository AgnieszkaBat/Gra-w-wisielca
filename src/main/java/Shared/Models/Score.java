package Shared.Models;

public class Score {
    public String name;
    public int attempts;
    public int lives;

    public Score(String name, int attempts, int lives) {
        this.name = name;
        this.attempts = attempts;
        this.lives = lives;
    }
}
