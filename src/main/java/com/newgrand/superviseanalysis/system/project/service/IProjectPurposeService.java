package com.newgrand.superviseanalysis.system.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newgrand.superviseanalysis.system.project.entity.PmProjectPurpose;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 田霖禹
 * @since 2020-04-20
 */
public interface IProjectPurposeService extends IService<PmProjectPurpose> {

    List<Map<String, Object>> getlistByDetailId(String detailId);
}
