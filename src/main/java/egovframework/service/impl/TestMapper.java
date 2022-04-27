package egovframework.service.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("TestMapper")
public class TestMapper extends EgovAbstractMapper {
	
	public Map<String, Object> selectUser(Map<String, Object> paramMap) {
		return selectOne("selectUser", paramMap);
	}
	
}
