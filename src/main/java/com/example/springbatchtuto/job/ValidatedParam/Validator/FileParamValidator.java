package com.example.springbatchtuto.job.ValidatedParam.Validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/**
 * 배치 실행 전 파일 이름이 유효한지 검증하는 로직
 * */
public class FileParamValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String fileName = parameters.getString("fileName");

        // csv 확장자가 아니라면
        if(!StringUtils.endsWithIgnoreCase(fileName, "csv")) {
            throw new JobParametersInvalidException("This is not csv file");
        }
    }

}
