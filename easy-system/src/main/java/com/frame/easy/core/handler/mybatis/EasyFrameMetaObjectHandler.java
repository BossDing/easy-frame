package com.frame.easy.core.handler.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.frame.easy.modular.sys.model.SysUser;
import com.frame.easy.util.ShiroUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动填充
 * 用于保存/修改的时候自动填充一些属性
 *
 * @author tengchong
 * @date 2019-05-14
 */
@Component
public class EasyFrameMetaObjectHandler implements MetaObjectHandler {
    /**
     * 创建人
     */
    private final String CREATE_USER = "createUser";
    /**
     * 创建时间
     */
    private final String CREATE_DATE = "createDate";
    /**
     * 编辑人
     */
    private final String EDIT_USER = "editUser";
    /**
     * 编辑时间
     */
    private final String EDIT_DATE = "editDate";

    @Override
    public void insertFill(MetaObject metaObject) {
        SysUser sysUser = null;
        try{
            sysUser = ShiroUtil.getCurrentUser();
        }catch (Exception e){
            // 如果获取不到用户则不设置用户类默认值
        }
        if (sysUser != null) {
            // 设置创建人&编辑人
            this.setFieldValByName(CREATE_USER, sysUser.getId(), metaObject);
            this.setFieldValByName(EDIT_USER, sysUser.getId(), metaObject);
        }
        Date now = new Date();
        this.setFieldValByName(CREATE_DATE, now, metaObject);
        this.setFieldValByName(EDIT_DATE, now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        SysUser sysUser = ShiroUtil.getCurrentUser();
        if (sysUser != null) {
            this.setFieldValByName(EDIT_USER, sysUser.getId(), metaObject);
        }
        this.setFieldValByName(EDIT_DATE, new Date(), metaObject);
    }
}
