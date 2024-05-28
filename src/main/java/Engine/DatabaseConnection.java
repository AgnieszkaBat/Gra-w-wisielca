package Engine;

import Shared.Models.Score;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {
    private final String connectionUrl;
    private Connection connection;

    public DatabaseConnection(String connectionUrl) {
        this.connectionUrl = connectionUrl;

        try {
            this.connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getConnectionUrl() {
        return this.connectionUrl;
    }

    public ArrayList<String> getDictionary(int level) {
        ArrayList<String> results = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Dictionary] WHERE [Difficulty] = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setInt(1, level);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public void addNewWordToDictionary(String newWord, int difficulty) {
        String sql = "INSERT INTO master.dbo.Dictionary (Name, Difficulty) VALUES (?, ?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newWord);
            pstmt.setInt(2, difficulty);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("A new word has been inserted successfully!");
            } else {
                System.out.println("No word was inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewRecord(String playerName, int attempts, int lives) {
        String sql = "INSERT INTO master.dbo.Scores\n" +
                "(Player, Attempts, Lives)\n" +
                "VALUES(?, ?, ?);\n";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, playerName);
            pstmt.setInt(2, attempts);
            pstmt.setInt(3, lives);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("A new record has been inserted successfully!");
            } else {
                System.out.println("No records was inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Score> getAllRecords() {
        String sql = "SELECT * FROM master.dbo.Scores";
        ArrayList<Score> scores = new ArrayList<>();

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet results = pstmt.executeQuery();

            while (results.next()) {
                String player = results.getString("Player");
                int attempts = results.getInt("Attempts");
                int lives = results.getInt("Lives");

                Score score = new Score(player, attempts, lives);
                scores.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scores;
    }
}
