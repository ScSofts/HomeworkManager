package edu.njust.homework_manager.protocol;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Date;

@Data
@Builder
public class ApiResult<T> {
    public int status;

    public Date timestamp;

    @Nullable
    public String error;

    @Nullable
    public String path;

    @Nullable
    public T data;
}
