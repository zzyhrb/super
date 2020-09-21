package com.newgrand.superviseanalysis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.superviseanalysis.system.menu.entity.SysMenu;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtils {

    private static List<Map<String, Object>> treeDataList = new ArrayList<>();

    /**
     * @throws
     * @title dtree加载
     * @description
     * @author admin
     * @updateTime 2020/4/18 12:43
     */
    public static JSONArray treeRecursionDataList(List<Map<String, Object>> treeList, BigInteger parentId, String id, String title, String parentIdName, List<String> param) {
        JSONArray childMenu = new JSONArray();
        Map<String, Object> treeMap = new HashMap<String, Object>();
        for (Map<String, Object> map : treeList) {
            map.put("title", map.get(title));
            map.put("id", map.get(id));
            if (param != null) {
                Map<String, Object> basicDataMap = new HashMap<>();
                for (String str : param) {
                    basicDataMap.put(str, map.get(str));
                }
                map.put("basicData", basicDataMap);
            }
            map.put("parentId", map.get(parentIdName));
            JSONObject jsonMenu = JSONObject.parseObject(JSON.toJSONString(map));
            BigInteger menuId = new BigInteger(map.get(id).toString().trim());
            int pid = jsonMenu.getIntValue("parentId");
            if (parentId.compareTo(BigInteger.valueOf(pid)) == 0) {
                JSONArray c_node = treeRecursionDataList(treeList, menuId, id, title, parentIdName, param);
                jsonMenu.put("children", c_node);
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }

    /**
     * @throws
     * @title 动态菜单
     * @description
     * @author admin
     * @updateTime 2020/4/18 11:21
     */
    public static JSONArray treeMenuList(List<SysMenu> treeList, Integer parentId) {
        JSONArray childMenu = new JSONArray();
        Map<String, Object> treeMap = new HashMap<String, Object>();
        for (SysMenu menu : treeList) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", menu.getTitle());
            map.put("id", menu.getId());
            map.put("href", menu.getUrl());
            map.put("fontFamily", "layui-icon");
            map.put("icon", menu.getIcon());
            map.put("spread", "控制台".equals(menu.getTitle()));
            map.put("isCheck", "控制台".equals(menu.getTitle()));
            map.put("parentId", menu.getParentId());
            JSONObject jsonMenu = JSONObject.parseObject(JSON.toJSONString(map));
            Integer menuId = Integer.parseInt(menu.getId().toString().trim());
            int pid = jsonMenu.getIntValue("parentId");
            if (parentId == pid) {
                JSONArray c_node = treeMenuList(treeList, menuId);
                jsonMenu.put("children", c_node);
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }
}
