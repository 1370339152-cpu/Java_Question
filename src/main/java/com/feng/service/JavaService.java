package com.feng.service;

import com.feng.pojo.JavaPojo;
import com.feng.pojo.vo.domain.CollectPojo;
import com.feng.pojo.vo.req.ForgetReqVo;
import com.feng.pojo.vo.req.LoginReqVo;
import com.feng.pojo.vo.resp.ForgetRespVo;
import com.feng.pojo.vo.resp.LoginRespVo;
import com.feng.pojo.vo.resp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author feng
 * @function: 项目接口
 */
public interface JavaService {
    /*
    * 获取所有题目
    * */
    List<JavaPojo> getQuestion();
    /*
    * 根据ID获取题目
    * */
    List<JavaPojo> getQuestionById(Integer questionId);
    /*
    * 添加题目功能
    * */
    void addQuestion(JavaPojo question);
    /*
    * 用户登录功能
    * */
    R<LoginRespVo> getLogin(LoginReqVo vo);
    /*
    * 验证码生成功能
    * */
    R<Map> getCaptcha();
    /*
    * 注册功能加密密码
    * */
    void getRegister(Long id, String username, String password, String realname);
    /*
    * 用户展示功能
    * */
    R<LoginRespVo> showUser();
    /*
    * 找回密码功能
    * */
    R<ForgetRespVo> getForget(ForgetReqVo vo);
    /*
    * 收藏指定题目功能
    * */
    R<HashMap<String,Object>> getCollectAdd(Integer questionId, Long userId);
    /*
    * 取消收藏指定题目功能
    * */
    R<HashMap<String,Object>> getCollectRemove(Integer questionId, Long userId);
    /*
    * 获取用户收藏的题目ID列表
    * */
    R<List<Integer>> getCollectList(Long userId);
    /*
    * 展示收藏功能
    * */
    R<List<CollectPojo>> getCollectAll(Long userId);
}
