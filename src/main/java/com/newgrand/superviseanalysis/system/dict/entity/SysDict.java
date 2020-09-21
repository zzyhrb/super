package com.newgrand.superviseanalysis.system.dict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    @TableId(value = "dict_id",type = IdType.AUTO)
    private Long dictId;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 排序
     */
    private Integer dictOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 字典描述
     */
    private String dictDescript;

    /**
     * 组织ID
     */
    private String organId;

    /**
     * 是否删除{0:正常,-1:删除}
     */
    private Integer isDelete = 0;


}
