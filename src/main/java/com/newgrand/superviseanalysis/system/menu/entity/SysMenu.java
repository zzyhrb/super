package com.newgrand.superviseanalysis.system.menu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 王雷
 * @since 2020-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父级菜单ID（一级菜单为0）
     */
    private Integer parentId;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序（从0开始）
     */
    private Integer sort;

    /**
     * 是否删除
     */
    private Integer isDelete = 0;


}
