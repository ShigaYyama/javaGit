package com.jmc.loginTest.controller;					
					
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;					
					
@Controller					
public class LoginController {					
					
    private final MstUserDao dao;					
					
    LoginController(MstUserDao dao) {					
        this.dao = dao;					
    }					
    					
    @GetMapping("/login")					
    String login(Model model) {					
        String id = (String) model.getAttribute("id");					
        ArrayList<String> errorMessage = (ArrayList<String>) model.getAttribute("errorMessage");					
					
        model.addAttribute("id", id);					
        model.addAttribute("errorMessage", errorMessage);					
					
        return "login";					
    }					
    					
    // ログイン時の処理					
    @GetMapping("/login/Submit")					
    String Submit(RedirectAttributes redirectAttributes,					
                  @RequestParam("id") String id,					
                  @RequestParam("password") String password){					
					
        // エラーメッセージ					
        var errorMessage = new ArrayList<String>();					
					
        // 入力チェック					
        if (StringUtils.isEmpty(id)) {					
            errorMessage.add("ユーザーIDを入力してください。");					
        }					
        if (StringUtils.isEmpty(password)) {					
            errorMessage.add("パスワードを入力してください。");					
        }					
					
        if (errorMessage.size() == 0) {					
            // ログインチェック					
            var userItem = dao.checkLogin(id, password);					
					
            // ユーザー情報が空の場合はログイン失敗					
            if (userItem == null) {					
                errorMessage.add("ユーザーIDとパスワードが一致しません。");					
            }					
        }					
					
        // エラーがある場合					
        if (errorMessage.size() > 0) {					
            redirectAttributes.addFlashAttribute("id", id);					
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);					
					
            return "redirect:/login";					
        }					
					
        // ログイン成功時はメニューに遷移					
        return "redirect:/menu";					
    }					
}					
