package cn.xlucky.framework.web.exception;

import cn.xlucky.framework.web.dto.RestResult;
import cn.xlucky.framework.web.dto.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.util.List;


/**
 * BusinessExceptionHandler
 * @author xlucky
 * @date 2020/3/20
 * @version 1.0.0
 */
@ControllerAdvice
@Order(1)
@Slf4j
public class BusinessExceptionHandler {

    public BusinessExceptionHandler() {
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public RestResult handleException(Exception exception) {
        BusinessException businessException = new BusinessException(ResultCodeEnum.SYSTEM_EXCEPTION.getCode(), exception);
        log.error(businessException.getMessage(), exception);
        RestResult baseResult = new RestResult();
        baseResult.code(businessException.getExCode()).message(businessException.getExDesc());
        return baseResult;
    }


    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseBody
    public RestResult handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = "请求参数错误！";
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (errors.size() > 0) {
            errorMessage = errors.get(0).getDefaultMessage();
        }

        RestResult baseResult = new RestResult();
        baseResult.code(ResultCodeEnum.BAD_REQUEST_PARAMS.getCode()).message(errorMessage);
        return baseResult;
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class, HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public RestResult handleHttpMediaTypeNotSupportedException(Exception ex) {
        BusinessException businessException = new BusinessException(ResultCodeEnum.BAD_REQUEST_MEDIA_TYPE.getCode(), ex);
        RestResult baseResult = new RestResult();
        baseResult.code(businessException.getExCode()).message(businessException.getExDesc());
        log.error(ex.getMessage(), ex);
        return baseResult;
    }

    @ExceptionHandler({MultipartException.class})
    @ResponseBody
    public RestResult handleMultipartException(Exception ex) {
        BusinessException businessException = new BusinessException(ResultCodeEnum.PARAM_BIND_EXCEPTION.getCode(), ex);
        RestResult baseResult = new RestResult();
        baseResult.code(businessException.getExCode()).message(businessException.getExDesc());
        return baseResult;
    }

    @ExceptionHandler({BindException.class})
    @ResponseBody
    public RestResult handleBindException(BindException ex) {
        String errorMesssage = null;
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (errors.size() > 0) {
            errorMesssage = errors.get(0).getDefaultMessage();
        }

        RestResult baseResult = new RestResult();
        if (null != errorMesssage) {
            baseResult.code(ResultCodeEnum.BAD_REQUEST_PARAMS.getCode()).message(errorMesssage);
        } else {
            baseResult.code(ResultCodeEnum.PARAM_BIND_EXCEPTION.getCode()).message(ResultCodeEnum.PARAM_BIND_EXCEPTION.getMessage());
        }

        log.error(ex.getMessage(), ex);
        return baseResult;
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseBody
    public RestResult handleBusinessException(BusinessException ex) {
        if (ex.getExCode() == ResultCodeEnum.SYSTEM_EXCEPTION.getCode()) {
            log.error(ex.getMessage(), ex);
        } else {
            log.warn(ex.getMessage());
        }

        RestResult baseResult = new RestResult();
        baseResult.code(ex.getExCode()).message(ex.getExDesc());
        return baseResult;
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    @ResponseBody
    public RestResult handlerErrorInput(Exception ex) {
        log.warn(ex.getMessage());
        BusinessException businessException = new BusinessException(10005, ex);
        RestResult baseResult = new RestResult();
        baseResult.code(businessException.getExCode()).message(businessException.getExDesc());
        return baseResult;
    }
}