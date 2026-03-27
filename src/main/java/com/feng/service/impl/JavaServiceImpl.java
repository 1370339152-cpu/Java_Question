package com.feng.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.druid.util.StringUtils;
import com.feng.constant.RedisConstant;
import com.feng.mapper.JavaMapper;
import com.feng.pojo.JavaPojo;
import com.feng.pojo.vo.domain.CollectPojo;
import com.feng.pojo.vo.req.ForgetReqVo;
import com.feng.pojo.vo.req.LoginReqVo;
import com.feng.pojo.vo.resp.ForgetRespVo;
import com.feng.pojo.vo.resp.LoginRespVo;
import com.feng.pojo.vo.resp.R;
import com.feng.pojo.vo.resp.ResponseCode;
import com.feng.service.JavaService;
import com.feng.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author feng
 * @function: 项目接口实现类
 */
@Slf4j
@Service
public class JavaServiceImpl implements JavaService {
    @Autowired
    private JavaMapper javaMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;

    Long id;

    /*
     * 获取题目功能
     * */
    @Override
    public List<JavaPojo> getQuestion() {
        List<JavaPojo> question = javaMapper.getQuestion();
        return question;
    }

    /*
     * 根据ID获取题目
     * */
    @Override
    public List<JavaPojo> getQuestionById(Integer questionId) {
        List<JavaPojo> question = javaMapper.getQuestionById(questionId);
        return question;
    }

    /*
     * 添加题目功能
     * */
    @Override
    public void addQuestion(JavaPojo pojo) {
        javaMapper.addQuestion(pojo);
    }

    /*
     * 用户登录功能
     * */
    @Override
    public R<LoginRespVo> getLogin(LoginReqVo vo) {
        if (vo == null ||
                StringUtils.isEmpty(vo.getUsername()) ||
                StringUtils.isEmpty(vo.getPassword()) ||
                StringUtils.isEmpty(vo.getCaptcha())
        ) {
            return R.error(ResponseCode.DATA_ERROR);
        }
        String code = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getSessionId());
        if (code.isEmpty()) {
            return R.error(ResponseCode.CHECK_CODE_EXPIRED);
        }
        if (!code.equalsIgnoreCase(vo.getCaptcha())) {
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }
        LoginRespVo user = javaMapper.getLogin(vo.getUsername());
        id = user.getId();
        log.info("当前账户为:{}", user);
        if (user == null) {
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        LoginRespVo data = new LoginRespVo();
        BeanUtils.copyProperties(user, data);
        return R.ok(data);
    }

    /*
     * 验证码生成功能
     * */
    @Override
    public R<Map> getCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 80, 4, 10);
        String code = captcha.getCode();
        String imageData = captcha.getImageBase64();
        String sessionId = String.valueOf(idWorker.nextId());
        redisTemplate.opsForValue().set(RedisConstant.CHECK_PREFIX + sessionId, code, 3, TimeUnit.MINUTES);
        HashMap<String, String> data = new HashMap<>();
        log.info("当前验证码为:{},会话id为:{}", code, sessionId);
        data.put("imageData", imageData);
        data.put("sessionId", sessionId);
        return R.ok(data);
    }

    /*
     * 注册功能加密密码
     * */
    @Override
    public void getRegister(Long id, String username, String password, String realname) {
        javaMapper.getRegisterReal(id, username, password, realname);
        password = passwordEncoder.encode(password);
        javaMapper.getRegister(id, username, password, realname);
    }

    /*
     * 用户展示功能
     * */
    @Override
    public R<LoginRespVo> showUser() {
        LoginRespVo vo = javaMapper.getShowUser(id);
        return R.ok(vo);
    }

    /*
     * 找回密码功能
     * */
    @Override
    public R<ForgetRespVo> getForget(ForgetReqVo vo) {
        if (vo == null ||
                StringUtils.isEmpty(vo.getUsername()) ||
                StringUtils.isEmpty(vo.getRealname()) ||
                StringUtils.isEmpty(vo.getCode())
        ) {
            return R.error(ResponseCode.DATA_ERROR);
        }
        String code = (String) redisTemplate.opsForValue().get(RedisConstant.CHECK_PREFIX + vo.getSessionId());
        if (code.isEmpty()) {
            return R.error(ResponseCode.CHECK_CODE_EXPIRED);
        }
        if (!code.equalsIgnoreCase(vo.getCode())) {
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }
        ForgetRespVo user = javaMapper.getForgetUser(vo.getUsername());
        if (!user.getRealname().equalsIgnoreCase(vo.getRealname())) {
            return R.error(ResponseCode.REALNAME_FALSE);
        }
        ForgetRespVo data = new ForgetRespVo();
        BeanUtils.copyProperties(user, data);
        log.info("用户验证成功,账号信息为:{}", data);
        return R.ok(data);
    }

    /*
     * 收藏指定题目功能
     * */
    @Override
    public R<HashMap<String, Object>> getCollectAdd(Integer questionId, Long userId) {
        HashMap<String, Object> data = new HashMap<>();
        try {
            javaMapper.getCollectAdd(questionId, userId);
            data.put("success", true);
            data.put("message", "收藏成功");
        } catch (Exception e) {
            data.put("success", false);
            data.put("message", "收藏失败，请检查是否已经收藏");
            throw new RuntimeException(e);
        }
        return R.ok(data);
    }

    /*
     * 取消收藏指定题目功能
     * */
    @Override
    public R<HashMap<String, Object>> getCollectRemove(Integer questionId, Long userId) {
        HashMap<String, Object> data = new HashMap<>();
        try {
            javaMapper.getCollectRemove(questionId, userId);
            data.put("success", true);
            data.put("message", "取消收藏成功");
        } catch (Exception e) {
            data.put("success", false);
            data.put("message", "取消收藏失败，请检查是否已经取消收藏");
            throw new RuntimeException(e);
        }
        return R.ok(data);
    }

    /*
     * 获取用户收藏的题目ID列表
     * */
    @Override
    public R<List<Integer>> getCollectList(Long userId) {
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Integer> data = javaMapper.getCollectSelect(userId);
        return R.ok(data);
    }

    /*
     * 展示收藏功能
     * */
    @Override
    public R<List<CollectPojo>> getCollectAll(Long userId) {
        List<CollectPojo> data = javaMapper.getCollectAll(userId);
        return R.ok(data);
    }
}
