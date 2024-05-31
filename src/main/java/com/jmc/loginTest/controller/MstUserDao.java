package com.jmc.loginTest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

@Service
public class MstUserDao {
    private final JdbcTemplate jdbcTemplate;

    record UserItem(String id, String name, String password) {}

    MstUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ログイン時のチェック処理
    public UserItem checkLogin(String id, String password) {
        // ユーザーマスタを検索
        String query = "SELECT * FROM mstuser " +
                "WHERE id = ? AND password = ?";

        // 結果を取得
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query, id, password);

        // 空の場合nullを返す
        if (result.isEmpty()) {
            return null;
        }

        // 1件目の内容を返す（パスワードは空）
        var row = result.get(0);

        return new UserItem(
                row.get("id").toString(),
                row.get("name").toString(),
                "");
    }

    // 全件取得
    public List<UserItem> findAll() {
        String query = "SELECT * FROM mstuser " + 
                "ORDER BY id";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
        List<UserItem> userItems = result.stream()
                .map((Map<String, Object> row) -> new UserItem(
                        row.get("id").toString(),
                        row.get("name").toString(),
                        ""))
                .toList();

        return userItems;
    }
    
    public UserItem find(String id) {
        String query = "SELECT * FROM mstuser " +
                "WHERE id = ?";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(query, id);

        // 空の場合nullを返す
        if (result.isEmpty()) {
            return null;
        }

        // 1件目の内容を返す
        var row = result.get(0);

        return new UserItem(
                row.get("id").toString(),
                row.get("name").toString(),
                row.get("password").toString());
    }

    // 追加処理
    public int insert(UserItem userItem) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(userItem);
        SimpleJdbcInsert insert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("mstuser");
        return insert.execute(param);
    }

    // 更新処理
    public int update(UserItem userItem) {
        int number = jdbcTemplate.update(
                "UPDATE mstuser SET name = ? WHERE id = ?",
                userItem.name(),
                userItem.id());
        return number;
    }

    // 削除処理
    public int delete(String id) {
        int number = jdbcTemplate.update(
                "DELETE FROM mstuser WHERE id = ?",
                id);
        return number;
    }

    // パスワード変更処理
    public int changePassword(String id, String password) {
        int number = jdbcTemplate.update(
                "UPDATE mstuser SET password = ? WHERE id = ?",
                password,
                id);
        return number;
    }
}
