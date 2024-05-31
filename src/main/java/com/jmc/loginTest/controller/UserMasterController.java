package com.jmc.loginTest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import com.jmc.loginTest.controller.MstUserDao.UserItem;

@Controller
public class UserMasterController {

    private final MstUserDao dao;

    UserMasterController(MstUserDao dao) {
        this.dao = dao;
    }

    // 一覧表示
    @GetMapping("/userMaster")
    String userMaster(Model model) {
        List<UserItem> userItems = dao.findAll();
        model.addAttribute("users", userItems);
        return "userMasterList";
    }

    // 入力画面表示
    @PostMapping("/userMaster/input")
     String input(Model model,
                 @RequestParam(value = "id", defaultValue = "") String id) {
        UserItem userItem = null;

        if (StringUtils.isEmpty(id)) {
            // 新規登録の場合
            userItem = new UserItem("","","");
            model.addAttribute("updateFlag", false);
        } else {
            // 更新の場合
            userItem = dao.find(id);

            if (userItem == null) {
                // 対象が存在しない場合は一覧に戻る
                return "redirect:/userMaster";
            }
            model.addAttribute("updateFlag", true);
        }

        model.addAttribute("user", userItem);

        return "userMasterInput";
    }

    // 削除時
    @PostMapping("/userMaster/delete")
    String delete(Model model,
                 @RequestParam("id") String id) {
        // 削除
        if (dao.delete(id) == 0) {
            // 削除失敗時
            return "redirect:/userMaster";
        }

        // 一覧に戻る
        return "redirect:/userMaster";
    }

    // 入力画面登録時
    @PostMapping(value = "/userMaster/submit", params="register")
    String register(Model model,
                            RedirectAttributes redirectAttributes,
                            @RequestParam(value = "id", defaultValue = "") String id,
                            @RequestParam(value = "name", defaultValue = "") String name,
                            @RequestParam(value = "password", defaultValue = "") String password,
                            @RequestParam(value = "updateFlag", defaultValue = "false") Boolean updateFlag) {

        UserItem userItem = new UserItem(id,name,password);

        // エラーメッセージ
        var errorMessage = new ArrayList<String>();

        // 新規登録時のIDチェック
        if (!updateFlag) {
            if (StringUtils.isEmpty(id)) {
                errorMessage.add("ユーザーIDを入力してください。");
            } else if (id.length() > 20) {
                errorMessage.add("ユーザーIDは20文字以内で入力してください。");
            } else {
                var findUser = dao.find(id);
                if (findUser != null) {
                    errorMessage.add("ユーザーIDはすでに使われています。");
                }
            }
        }

        //　ユーザー名
         if (name.length() > 50) {
            errorMessage.add("ユーザー名は50文字以内で入力してください。");
        }

         // 新規登録時のパスワードチェック
        if (!updateFlag) {
            if (StringUtils.isEmpty(password)) {
                errorMessage.add("パスワードを入力してください。");
            } else if (password.length() < 8) {
                errorMessage.add("パスワードは8文字以上必要です。");
            } else if (password.length() > 32) {
                errorMessage.add("パスワードは32文字以内で入力してください。");
            }
        }

        // エラーなしの場合
        if (errorMessage.size() == 0) {
            if (!updateFlag) {
                // 新規登録の場合
                if (dao.insert(userItem) > 0) {
                    // 登録後、一覧に戻る
                    return "redirect:/userMaster";
                }
                // 失敗時、エラーメッセージを表示
                errorMessage.add("新規登録に失敗しました。");
            } else {
                // 更新の場合
                if (dao.update(userItem) > 0) {
                    // 更新後、一覧に戻る
                    return "redirect:/userMaster";
                }
                // 失敗時、エラーメッセージを表示
                errorMessage.add("更新に失敗しました。");
            }
        }

        // エラー有りの場合
        model.addAttribute("user", userItem);
        model.addAttribute("updateFlag", updateFlag);
        model.addAttribute("errorMessage", errorMessage);

        return "userMasterInput";
    }

    // 入力画面戻るボタン押下時
    @PostMapping(value = "/userMaster/submit", params="back")
    String back(Model model) {
        // 一覧に戻る
        return "redirect:/userMaster";
    }
}
