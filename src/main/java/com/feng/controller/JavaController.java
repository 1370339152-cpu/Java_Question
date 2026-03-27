package com.feng.controller;

import com.feng.pojo.JavaPojo;
import com.feng.pojo.vo.domain.CollectPojo;
import com.feng.pojo.vo.req.ForgetReqVo;
import com.feng.pojo.vo.req.LoginReqVo;
import com.feng.pojo.vo.resp.ForgetRespVo;
import com.feng.pojo.vo.resp.LoginRespVo;
import com.feng.pojo.vo.resp.R;
import com.feng.pojo.vo.resp.ResponseCode;
import com.feng.service.JavaService;
import com.feng.service.impl.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author feng
 * @function: 项目控制类
 */
@Slf4j
@Controller
public class JavaController {
    @Autowired
    private JavaService javaService;
    @Autowired
    private AiService aiService;

    /*
     * 跳转页面到前端login
     * */
    @GetMapping("/java/login")
    public String loginThy() {
        return "login";
    }

    /*
     * 用户登录功能
     * */
    @ResponseBody
    @PostMapping("/java/login")
    public R<LoginRespVo> login(@RequestBody LoginReqVo vo, HttpSession session) {
        R<LoginRespVo> loginResult = javaService.getLogin(vo);
        if (loginResult.getCode() == 1 && loginResult.getData() != null) {
            LoginRespVo loginRespVo = loginResult.getData();
            // 存入userId 到 Session（供收藏、权限等功能使用）
            session.setAttribute("userId", loginRespVo.getId());
            // 存入用户名、真实姓名（供前端显示用户信息）
            session.setAttribute("username", loginRespVo.getUsername());
            session.setAttribute("realname", loginRespVo.getRealname());
        }
        return loginResult;
    }

    /*
     * 验证码创建
     * */
    @ResponseBody
    @GetMapping("/hutu/captcha")
    public R<Map> captcha() {
        return javaService.getCaptcha();
    }

    /*
     * 注册页面跳转
     * */
    @GetMapping("/java/register")
    public String showRegister() {
        return "register";
    }

    /*
     * 注册功能
     * */
    @ResponseBody
    @PostMapping("/java/register")
    public R<ResponseCode> register(@RequestBody LoginRespVo vo) {
        HashMap<String, Object> data = new HashMap<>();
        try {
            javaService.getRegister(vo.getId(), vo.getUsername(), vo.getPassword(), vo.getRealname());
        } catch (Exception e) {
            throw new RuntimeException("注册失败，请稍后重试并检查规则");
        }
        return R.ok(ResponseCode.SUCCESS);
    }

    /*
     * 获取所有题目数据
     * */
    @GetMapping("/java/study")
    public String study(Model model) {
        List<JavaPojo> question = javaService.getQuestion();
        model.addAttribute("questions", question);
        model.addAttribute("currentIndex", 0);
        return "study";
    }

    /*
     * 获取答案数据
     * 返回值为前端要求的success: , message: 格式
     * */
    @ResponseBody
    @PostMapping("/java/submitAnswer/{questionId}")
    public CompletableFuture<Map<String, Object>> answer(
            @PathVariable("questionId") Integer questionId,
            @RequestBody Map<String, String> answerBody
    ) {
        //获取答案
        String userAnswer = answerBody.get("userAnswer");
        HashMap<String, Object> result = new HashMap<>();

        try {
            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "请输入答案后再提交");
                return CompletableFuture.completedFuture(result);
            }

            // 获取题目信息JavaPojo格式(id = ?,name = ?这种格式)，通过id获取对应的答案
            List<JavaPojo> questionList = javaService.getQuestionById(questionId);
            //获取其中的问题
            String question = questionList.get(0).getQuestion();
            log.info("传入的题目信息为:{}", question);

            // 调用异步服务获取AI反馈（非阻塞）
            return aiService.getAiFeedback(question, userAnswer)
                    .thenApply(aiResult -> {
                        // 合并结果（保留提交成功的基础信息）
                        result.putAll(aiResult);
                        if (result.get("success").equals(true)) {
                            result.put("message", "提交成功，已生成反馈");
                        }
                        return result;
                    });

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提交失败，检查后端：" + e.getMessage());
            return CompletableFuture.completedFuture(result);
        }
    }

    /*
     * 跳转指定页面
     * */
    @GetMapping("/java/add/question")
    public String add() {
        return "addquestion";
    }

    /*
     * 添加题目功能
     * */
    @ResponseBody
    @PostMapping("/java/add")
    public Map<String, Object> addQuestion(@RequestBody JavaPojo pojo) {
        HashMap<String, Object> data = new HashMap<>();
        try {
            javaService.addQuestion(pojo);
            data.put("success", true);
            data.put("message", "新增题目成功");
        } catch (Exception e) {
            data.put("success", false);
            data.put("message", "新增题目失败，请查看新增规则");
        }
        return data;
    }

    /*
     * 用户展示功能，展示出用户数据
     * */
    @ResponseBody
    @GetMapping("/java/login/user")
    public R<LoginRespVo> showUser() {
        return javaService.showUser();
    }

    /*
     * 退出登录功能
     * */
    @GetMapping("/java/logout")
    public String logout() {
        return "redirect:login";
    }

    /*
     * 跳转页面
     * */
    @GetMapping("/java/login/forget")
    public String forget() {
        return "forget";
    }

    /*
     * 找回密码功能
     * */
    @ResponseBody
    @PostMapping("/java/login/forget")
    public R<ForgetRespVo> forget(@RequestBody ForgetReqVo vo) {
        return javaService.getForget(vo);
    }

    /*
     * 收藏指定题目功能
     * */
    @ResponseBody
    @PostMapping("/java/study/collect/add/{questionId}")
    public R<HashMap<String, Object>> collectAdd(@PathVariable("questionId") Integer questionId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return javaService.getCollectAdd(questionId, userId);
    }

    /*
     * 取消收藏指定题目功能
     * */
    @ResponseBody
    @PostMapping("/java/study/collect/remove/{questionId}")
    public R<HashMap<String, Object>> collectRemove(@PathVariable("questionId") Integer questionId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return javaService.getCollectRemove(questionId, userId);
    }

    /*
     * 获取用户收藏的题目ID列表
     * */
    @ResponseBody
    @GetMapping("/java/study/collect/list")
    public R<List<Integer>> collectList(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return javaService.getCollectList(userId);
    }

    /*
     * 跳转页面到收藏的所有题目
     * */
    @GetMapping("/java/study/collect")
    public String getCollectAll() {
        return "collect";
    }

    /*
     * 展示收藏题目
     * */
    @ResponseBody
    @GetMapping("/java/login/collect")
    public R<List<CollectPojo>> collectAll(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        log.info("当前userId:{}", userId);
        return javaService.getCollectAll(userId);
    }

}
