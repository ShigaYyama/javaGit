package com.jmc.loginTest.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

@Controller
public class ChangePasswordController {

    private final MstUserDao dao;

    ChangePasswordController(MstUserDao dao) {
        this.dao = dao;
    }

    // 画面表示
    @PostMapping("/changePassword")
    String changePassword(Model model,
                          @RequestParam("id") String id) {
        MstUserDao.UserItem userItem = dao.find(id);

        if (userItem == null) {
            // 対象が存在しない場合は一覧に戻る
            return "redirect:/userMaster";
        }

        model.addAttribute("user", userItem);

        return "changePassword";
    }

    // 変更ボタン押下時
    @PostMapping(value = "/changePassword/submit", params="update")
    String update(Model model,
                  RedirectAttributes redirectAttributes,
                  @RequestParam(value = "id", defaultValue = "") String id,
                  @RequestParam(value = "oldPassword", defaultValue = "") String oldPassword,
                  @RequestParam(value = "newPassword", defaultValue = "") String newPassword,
                  @RequestParam(value = "checkPassword", defaultValue = "") String checkPassword) {

        // エラーメッセージ
        var errorMessage = new ArrayList<String>();

        // 更新対象の取得
        MstUserDao.UserItem userItem = dao.find(id);

        if (userItem == null) {
            // 対象が存在しない場合
            errorMessage.add("対象のユーザーが存在しません。");
        }

        // 現在のパスワードのチェック
        if (errorMessage.size() == 0) {
            if (StringUtils.isEmpty(oldPassword)) {
                errorMessage.add("現在のパスワードを入力してください。");
            }
            if (!oldPassword.equals(userItem.password())) {
                errorMessage.add("現在のパスワードが一致しません。");
            }
        }

        // 新しいパスワードのチェック
        if (errorMessage.size() == 0) {
            if (StringUtils.isEmpty(newPassword)) {
                errorMessage.add("新しいパスワードを入力してください。");
            } else if (newPassword.length() < 8) {
                errorMessage.add("新しいパスワードは8文字以上必要です。");
            } else if (newPassword.length() > 32) {
                errorMessage.add("新しいパスワードは32文字以内で入力してください。");
            }

            if (newPassword.equals(oldPassword)) {
                errorMessage.add("現在と同じパスワードは使用できません。");
            }
        }

        // 確認用パスワードのチェック
        if (errorMessage.size() == 0) {
            if (!checkPassword.equals(newPassword)) {
                errorMessage.add("新しいパスワードと確認用パスワードが一致しません。[" + newPassword + "][" + checkPassword + "]");
            }
        }

        // エラーなしの場合
        if (errorMessage.size() == 0) {
            // パスワードの更新
            if (dao.changePassword(id, newPassword) > 0) {
                // 更新できた場合一覧に戻る
                return "redirect:/userMaster";
            }
            // 失敗時、エラーメッセージを表示
            errorMessage.add("更新に失敗しました。");
        }

        // エラー有りの場合
        model.addAttribute("user", userItem);
        model.addAttribute("errorMessage", errorMessage);

        return "changePassword";
    }

    // 入力画面戻るボタン押下時
    @PostMapping(value = "/changePassword/submit", params="back")
    String back(Model model) {
        // 一覧に戻る
        return "redirect:/userMaster";
    }
}
