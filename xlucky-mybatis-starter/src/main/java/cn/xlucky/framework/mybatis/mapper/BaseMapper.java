package cn.xlucky.framework.mybatis.mapper;

import java.io.Serializable;

/**
 * 基础mapper
 * @author xlucky
 * @date 2020/6/3
 * @version 1.0.0
 */
public interface BaseMapper<PK extends Serializable, E> {
    /**
     * 保存
     * <p>
     *
     * @param e
     * @return 
     * @author xlucky
     * @date 2020/6/3 16:23
     * @version 1.0.0
     */
    int save(E e);

    /**
     * 修改
     * <p>
     *
     * @param e
     * @return
     * @author xlucky
     * @date 2020/6/3 16:23
     * @version 1.0.0
     */
    int update(E e);

    /**
     * 删除
     * <p>
     *
     * @param pk
     * @return
     * @author xlucky
     * @date 2020/6/3 16:23
     * @version 1.0.0
     */
    int remove(PK pk);

    /**
     * id获取
     * <p>
     *
     * @param pk
     * @return
     * @author xlucky
     * @date 2020/6/3 16:23
     * @version 1.0.0
     */
    E get(PK pk);
}
