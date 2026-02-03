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

    @Step("БД: Создать пост напрямую через SQL")
    public int insertPost(Post post) {
        String query = "INSERT INTO wp_posts (post_title, post_content, post_status, post_author, post_name, post_date, post_date_gmt, post_modified, post_modified_gmt, post_excerpt, to_ping, pinged, post_content_filtered) " +
                "VALUES (?, ?, ?, 1, 'test-slug', NOW(), NOW(), NOW(), NOW(), '', '', '', '')";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setString(3, post.getStatus() == null ? "publish" : post.getStatus());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при вставке поста в БД", e);
        }
        return -1;
    }

    @Step("БД: Создать комментарий напрямую через SQL")
    public int insertComment(Comment comment) {
        String query = "INSERT INTO wp_comments (comment_post_ID, comment_content, comment_author, comment_date, comment_date_gmt) " +
                "VALUES (?, ?, 'Admin', NOW(), NOW())";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, comment.getPost());
            statement.setString(2, comment.getContent());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при вставке комментария в БД", e);
        }
        return -1;
    }

    @Step("БД: Удалить пост по ID {postId}")
    public void deletePostById(int postId) {
        String query = "DELETE FROM wp_posts WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, postId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Step("БД: Удалить комментарий по ID {commentId}")
    public void deleteCommentById(int commentId) {
        String query = "DELETE FROM wp_comments WHERE comment_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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