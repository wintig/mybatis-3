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
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MybatisReflectionDemo {
	

	private SqlSessionFactory sqlSessionFactory;
	
	@Before
	public void init() throws IOException {
		//--------------------第一阶段---------------------------
	    // 1.读取mybatis配置文件创SqlSessionFactory
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		// 1.读取mybatis配置文件创SqlSessionFactory
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		inputStream.close();
	}


	//----------------源码分析之反射工具类的实例---------------------
	@Test
	public void reflectionTest() {

		//反射工具类初始化
		ObjectFactory objectFactory = new DefaultObjectFactory();
		User user = objectFactory.create(User.class);
		ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
		ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
		MetaObject metaObject = MetaObject.forObject(user, objectFactory, objectWrapperFactory, reflectorFactory);

		//使用Reflector读取类元信息
		Reflector findForClass = reflectorFactory.findForClass(User.class);
		Constructor<?> defaultConstructor = findForClass.getDefaultConstructor();
		String[] getGetablePropertyNames = findForClass.getGetablePropertyNames();
		String[] getSetablePropertyNames = findForClass.getSetablePropertyNames();

		System.out.println(defaultConstructor.getName());
		System.out.println(Arrays.toString(getGetablePropertyNames));
		System.out.println(Arrays.toString(getSetablePropertyNames));
	}

	/**
	 * objectWrapper根据属性规则，对对象赋值
	 */
	@Test
	public void objectWrapperTest() {

		//反射工具类初始化
		ObjectFactory objectFactory = new DefaultObjectFactory();
		User user = objectFactory.create(User.class);
		ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
		ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
		MetaObject metaObject = MetaObject.forObject(user, objectFactory, objectWrapperFactory, reflectorFactory);

		//使用ObjectWrapper读取对象信息，并对对象属性进行赋值操作
		User userTemp = new User();
		// 生产的ObjectWrapper就包含元数据和对象本身
		ObjectWrapper wrapperForUser = new BeanWrapper(metaObject, userTemp);

		// 将对象里面的userName设置成wintig
		PropertyTokenizer prop = new PropertyTokenizer("userName");
		wrapperForUser.set(prop, "wintig");
		System.out.println(userTemp);
	}

	/**
	 * mateObject
	 */
	@Test
	public void metaObjectTest() {

		//模拟数据库行数据转化成对象
		ObjectFactory objectFactory = new DefaultObjectFactory();
		User user = objectFactory.create(User.class);
		ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
		ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

		// 生成metaObject
		// MetaObject metaObject = MetaObject.forObject(user, objectFactory, objectWrapperFactory, reflectorFactory);
		MetaObject metaObject = SystemMetaObject.forObject(user);


		//1.模拟从数据库读取数据
		Map<String, Object> dbResult = new HashMap<String, Object>();
		dbResult.put("id", 1);
		dbResult.put("userName", "wintig");

		//2.模拟映射关系
		Map<String, String> mapper = new HashMap<String, String>();
		mapper.put("id", "id");
		mapper.put("userName", "userName");

		//3.使用反射工具类将行数据转换成pojo

		//获取BeanWrapper,既包括类元数据，同时还能对对象的属性赋值
		BeanWrapper objectWrapper = (BeanWrapper) metaObject.getObjectWrapper();

		Set<Entry<String, String>> entrySet = mapper.entrySet();
		//遍历映射关系
		for (Entry<String, String> colInfo : entrySet) {
			String propName = colInfo.getKey();//获得pojo的字段名称
			Object propValue = dbResult.get(colInfo.getValue());//模拟从数据库中加载数据对应列的值
			PropertyTokenizer proTokenizer = new PropertyTokenizer(propName);
			objectWrapper.set(proTokenizer, propValue);//将数据库的值赋值到pojo的字段中
		}
		System.out.println(metaObject.getOriginalObject());
	}



}