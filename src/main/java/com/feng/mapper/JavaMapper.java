package com.feng.mapper;

import com.feng.pojo.JavaPojo;
import com.feng.pojo.vo.domain.CollectPojo;
import com.feng.pojo.vo.resp.ForgetRespVo;
import com.feng.pojo.vo.resp.LoginRespVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author feng
 * @function: 项目DAO层
 */
@Mapper
public interface JavaMapper {
    /*
    * 获取题目
    * */
    List<JavaPojo> getQuestion();
    /*
    * 根据ID获取题目
    * */
    List<JavaPojo> getQuestionById(@Param("questionId") Integer questionId);
    /*
    * 添加题目功能
    * */
    void addQuestion(JavaPojo pojo);
    /*
    * 根据用户名获取用户信息
    * */
    LoginRespVo getLogin(@Param("username") String username);
    /*
    * 注册功能插入加密密码
    * */
    void getRegister(@Param("id") Long id, @Param("username") String username, @Param("password") String password, @Param("realname") String realname);
    /*
    * 注册功能插入真实密码
    * */
    void getRegisterReal(@Param("id") Long id, @Param("username") String username, @Param("password") String password, @Param("realname") String realname);
    /*
    * 用户展示功能
    * */
    LoginRespVo getShowUser(@Param("id") Long id);
    /*
    * 找回密码功能
    * */
    ForgetRespVo getForgetUser(@Param("username") String username);
    /*
    * 收藏指定题目功能
    * */
    void getCollectAdd(@Param("questionId") Integer questionId, @Param("userId") Long userId);
    /*
    * 取消收藏指定题目功能
    * */
    void getCollectRemove(@Param("questionId") Integer questionId, @Param("userId") Long userId);
    /*
    * 获取用户收藏的题目ID列表
    * */
    List<Integer> getCollectSelect(@Param("userId") Long userId);
    /*
    * 展示收藏功能
    * */
    List<CollectPojo> getCollectAll(@Param("userId") Long userId);
}
