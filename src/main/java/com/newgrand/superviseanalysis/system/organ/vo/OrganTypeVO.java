package com.newgrand.superviseanalysis.system.organ.vo;

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
 * @since 2020-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrganTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织id
     */
    @TableId(value = "organ_id")
    private String organId;

    /**
     * 组织名称
     */
    private String organName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 排序
     */
    private Integer organOrder;

    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 组织类别ID
     */
    private Integer typeId;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 是否删除{0:正常,-1:删除,1:停用}
     */
    private Integer isDelete = 0;

    /**
     * 备注信息
     */
    private String organRemark;

    /**
     * 组织类别名称
     */
    private String typeName;



}
