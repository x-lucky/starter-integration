package cn.xlucky.framework.mybatis.service;

import java.io.Serializable;

import cn.xlucky.framework.mybatis.mapper.BaseMapper;
import cn.xlucky.framework.mybatis.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 基础service服务
 * @author xlucky
 * @date 2020/6/3
 * @version 1.0.0
 */
@Repository("baseService")
public abstract class BaseServiceImpl<PK extends Serializable, E extends BaseEntity> implements BaseService<PK, E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);
    private BaseMapper<PK, E> baseMapper;

    public BaseServiceImpl() {
    }

    public void setBaseMapper(BaseMapper<PK, E> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public int save(E entity) {
        return this.baseMapper.save(entity);
    }
    @Override
    public int update(E entity) {
        return this.baseMapper.update(entity);
    }
    @Override
    public int remove(PK id) {
        return this.baseMapper.remove(id);
    }
    @Override
    public E get(PK id) {
        return this.baseMapper.get(id);
    }
}
