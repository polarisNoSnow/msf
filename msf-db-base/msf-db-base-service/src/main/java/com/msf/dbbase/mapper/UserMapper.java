package com.msf.dbbase.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.msf.dbbase.core.YsappMapper;
import com.msf.dbbase.entity.User;

@Mapper
public interface UserMapper extends YsappMapper<User>{

}
