package com.newgrand.superviseanalysis.system.project.entity;

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
 * @since 2020-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    @TableId(value = "project_id",type = IdType.AUTO)
    private Integer projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String projectDescript;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 组织id
     */
    private String organId;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 是否删除{0:正常,-1:删除,1:停用}
     */
    private Integer isDelete = 0;


}
