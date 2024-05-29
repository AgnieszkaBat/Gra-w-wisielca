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

        var createScoresTableSql = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Scores]') AND type in (N'U'))\n" +
                "BEGIN\n" +
                "    CREATE TABLE Scores (\n" +
                "        ID INT IDENTITY(1,1) PRIMARY KEY,\n" +
                "        Player VARCHAR(50),\n" +
                "        Attempts INT,\n" +
                "        Lives INT\n" +
                "    );\n" +
                "END";

        var createDictionaryTableSql  ="IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Dictionary')\n" +
                "BEGIN\n" +
                "    CREATE TABLE Dictionary (\n" +
                "        Id INT IDENTITY(1,1) PRIMARY KEY,\n" +
                "        Name NVARCHAR(255) NOT NULL,\n" +
                "        Difficulty INT NOT NULL\n" +
                "    );\n" +
                "END";

        var seedSql = "INSERT INTO Dictionary (Name, Difficulty) VALUES\n" +
                "('ekologia', 2),\n" +
                "('katastroficzny', 3),\n" +
                "('losowy', 1),\n" +
                "('deska', 1),\n" +
                "('niebieski', 3),\n" +
                "('ale', 1),\n" +
                "('miara', 2),\n" +
                "('kanapka', 1),\n" +
                "('gra', 1),\n" +
                "('dach', 1),\n" +
                "('widok', 1),\n" +
                "('introwertyczny', 3),\n" +
                "('lustrzany', 1),\n" +
                "('lampa', 1),\n" +
                "('dalekowzroczny', 3),\n" +
                "('flora', 1),\n" +
                "('ekstrapolacja', 3),\n" +
                "('oko', 1),\n" +
                "('interpunkcja', 3),\n" +
                "('telekomunikacja', 3),\n" +
                "('kwas', 1),\n" +
                "('tak', 1),\n" +
                "('kot', 1),\n" +
                "('ramka', 1),\n" +
                "('trawa', 2);";
        try {
            PreparedStatement pstmt = connection.prepareStatement(createScoresTableSql);
            pstmt.execute();

            pstmt =  connection.prepareStatement(createDictionaryTableSql);
            pstmt.execute();

            pstmt =  connection.prepareStatement(seedSql);
            pstmt.execute();
        }catch (SQLException e) {
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
