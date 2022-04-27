package egovframework.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.service.TestService;

@Service("TestService")
public class TestServiceImpl extends EgovAbstractServiceImpl implements TestService {
	
	@Resource(name = "TestMapper")
	private TestMapper testMapper;
	
	@Override
	public Map<String, Object> selectUser(Map<String, Object> paramMap) {
		return testMapper.selectUser(paramMap);
	}

}
