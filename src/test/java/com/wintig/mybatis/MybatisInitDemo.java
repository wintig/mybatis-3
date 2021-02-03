/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.wintig.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jws.soap.SOAPBinding.Use;

import com.wintig.entity.User;
import com.wintig.mapper.UserMapper;
import org.apache.ibatis.io.Resources;

import org.apache.ibatis.reflection.*;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;


public class MybatisInitDemo {
	

	private SqlSessionFactory sqlSessionFactory;
	
	@Before
	public void init() throws IOException {
		//--------------------第一阶段---------------------------
	    // 1.读取mybatis配置文件创SqlSessionFactory
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		// 2.读取mybatis配置文件创SqlSessionFactory
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		inputStream.close();
	}
	
	@Test
	public void quickStart() throws IOException {
		//--------------------第二阶段---------------------------
		// 3.获取sqlSession，一次会话
		SqlSession sqlSession = sqlSessionFactory.openSession();
		// 4.获取对应mapper
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		
		//--------------------第三阶段---------------------------
		// 5.执行查询语句并返回单条数据
		User user = mapper.selectByPrimaryKey(1);
		System.out.println(user);

		System.out.println("----------------------------------");
	}


	@Test
	// ibatis编程模型 本质分析
	public void originalOperation() {
		// 2.获取sqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		// 3.执行查询语句并返回结果
		User user = sqlSession.selectOne("com.wintig.mapper.UserMapper.selectByPrimaryKey", 1);
		System.out.println(user.toString());
	}

}