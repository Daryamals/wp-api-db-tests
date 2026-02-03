package com.simbirsoft.wordpress.helpers;

import com.simbirsoft.wordpress.models.Comment;
import com.simbirsoft.wordpress.models.Post;
import io.qameta.allure.Step;

import java.sql.*;

public class DbHelper {
    private Connection connection;

    public DbHelper() {
        try {
            connection = DriverManager.getConnection(
                    PropertiesHelper.getProperty("db.url"),
                    PropertiesHelper.getProperty("db.user"),
                    PropertiesHelper.getProperty("db.password")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД", e);
        }
    }

    @Step("БД: Получить пост по ID {postId}")
    public Post getPostById(int postId) {
        String query = "SELECT post_title, post_content FROM wp_posts WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return Post.builder()
                        .title(rs.getString("post_title"))
                        .content(rs.getString("post_content"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Step("БД: Получить комментарий по ID {commentId}")
    public Comment getCommentById(int commentId) {
        String query = "SELECT comment_post_ID, comment_content FROM wp_comments WHERE comment_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commentId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return Comment.builder()
                        .post(rs.getInt("comment_post_ID"))
                        .content(rs.getString("comment_content"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Step("БД: Закрыть соединение")
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}