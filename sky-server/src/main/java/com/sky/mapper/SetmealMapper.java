package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotaion.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SetmealMapper {
    /**
     * 查询 id 绑定的套参数
     *
     * @param id
     * @return
     */
    int queryById(Long id);

    /**
     * 查询当前菜品是否关联了套餐
     *
     * @param dishIds
     * @return
     */
    List<SetmealDish> queryByIds(List<Long> dishIds);

    /**
     * 新增套餐信息
     *
     * @param setMeal
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int insetSetMeal(Setmeal setMeal);

    /**
     * 分页查询
     *
     * @param pageQueryDTO
     * @return
     */
    Page<SetmealVO> queryPage(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     * @AutoFill(OperationType.UPDATE) 不能更新时间
     *      因为形参列表的第一个参数 必须为 单体 entity类型
     */
    int startOrStop(Integer status, Long id);

    /**
     * 根据setmealId查询套餐信息 套餐id
     * @param setmealId
     * @return
     */
    SetmealVO queryBySetmealId(Long setmealId);

    /**
     * 修改套餐的基本信息 (sql语句里必须也要有修改时间的语句)
     * @param setmeal
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    int updateSetmealBaseInfo(Setmeal setmeal);

    /**
     * 批量删除套餐信息
     * @param ids
     * @return
     */
    int deleteBatchById(List<Long> ids);

    /**
     * 根据setmeaId 和 status = 1两种状态查询 查询起售中的套餐信息
     * @param ids
     * @return
     */
    List<SetmealDish> queryBySetmealIdAndStatus(List<Long> ids);

    /**
     * 根据分类id查询套餐信息
     * @param categoryId
     * @return
     */
    List<Setmeal> queryByCategoryId(Long categoryId);

    /**
     * 根据setmealId查询套餐信息
     * @param setmealId
     * @return
     */
    Setmeal queryOneById(Long setmealId);

    @Select("select count(*) from setmeal where status = #{status}")
    Integer getSetmealStatusCount(int status);
}
