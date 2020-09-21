package com.newgrand.superviseanalysis.system.project.entity;

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
 * @since 2020-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmProjectTree implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目树ID
     */
    @TableId(value = "tree_id")
    private String treeId;

    /**
     * 名称
     */
    private String treeName;

    /**
     * 类型
     */
    private Integer typeId;

    /**
     * 顺序
     */
    private Integer treeOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 是否删除{0:正常,-1:删除,1:停用}
     */
    private Integer isDelete = 0;

    /**
     * 字段描述
     */
    private String columnDescript;

    /**
     * 详细{0:不是,1:是}
     */
    private Integer columnDetail = 0;

    /**
     * 详细{0:不是,1:是}
     */
    private Long dictId;
}
