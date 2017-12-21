/**
 * Copyright 2012-2017 Kaixindai Financing Services Jiangsu Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kxd.framework.utils;

import com.kxd.framework.lang.AppException;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 请输入功能描述
 * 
 * @author 老白 2014年10月17日
 */
public class BeanUtil {
	public static final void copyProperties(Object source, Object target) {
		if (source == null || target == null)
			throw new AppException("can not copy beans");
		BeanCopier copier = BeanCopier.create(source.getClass(),
				target.getClass(), false);
		copier.copy(source, target, null);
	}

	public static final List<Field> getFields(Class clazz, Class endSuperClazz,boolean forceAccess) {
		List<Field> fieldList = new ArrayList<Field>();
		if (clazz == null) {
			throw new IllegalArgumentException("The class must not be null");
		}

		for (Class<?> acls = clazz; acls != null; acls = acls.getSuperclass()) {
				Field[] fields = acls.getDeclaredFields();
				//if the fields is empty,continue,next one;
				if(ArrayUtils.isEmpty(fields)) continue;
				// getDeclaredField checks for non-public scopes as well
				// and it returns accurate results
				for(Field field:fields){
					//do not handle static fields
					if(Modifier.isStatic(field.getModifiers())) continue;
					if (!Modifier.isPublic(field.getModifiers())) {
						if (forceAccess) {
							field.setAccessible(true);
						} else {
							continue;
						}
					}
					fieldList.add(field);
				}
				//if the super class equals the end super clazz,breaks
				if(acls.equals(endSuperClazz)) break;
		}
		return fieldList;
	}
}
