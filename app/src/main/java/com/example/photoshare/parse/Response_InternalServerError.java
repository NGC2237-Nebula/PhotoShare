package com.example.photoshare.parse;

/*
{
        timestamp:"2022-08-10 18:34:28"
        status:400
        error:"Bad Request"
        message:"JSON parse error: Cannot deserialize value of type `java.lang.Byte` from String "2w": not a valid Byte value; nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.lang.Byte` from String "2w": not a valid Byte value
                at [Source: (PushbackInputStream); line: 1, column: 116] (through reference chain: com.zhai.controller.photo.form.UserUpdateForm["sex"])"
        path:"/photo/user/update"
}
*/

/*
{
        timestamp:"2022-08-10 18:38:01"
        status:500
        error:"Internal Server Error"
        message:"
            ### Error updating database. Cause: java.sql.SQLException: Incorrect string value: '\xE5\x93\x88\xE5\x93\x88' for column 'introduce' at row 1
            ### The error may exist in com/zhai/mapper/PUserMapper.java (best guess)
            ### The error may involve com.zhai.mapper.PUserMapper.updateById-Inline
            ### The error occurred while setting parameters
            ### SQL: UPDATE p_user SET app_key=?, password=?, sex=?, introduce=?, create_Time=?, last_update_time=? WHERE id=?
            ### Cause: java.sql.SQLException: Incorrect string value: '\xE5\x93\x88\xE5\x93\x88' for column 'introduce' at row 1
            ; uncategorized SQLException; SQL state [HY000]; error code [1366]; Incorrect string value: '\xE5\x93\x88\xE5\x93\x88' for column 'introduce' at row 1; nested exception is java.sql.SQLException: Incorrect string value: '\xE5\x93\x88\xE5\x93\x88' for column 'introduce' at row 1"
        path:"/photo/user/update"
}
*/

/*
{
        timestamp:"2022-09-06 14:21:22"
        status:500
        error:"Internal Server Error"
        message:"Maximum upload size exceeded; nested exception is java.lang.IllegalStateException: org.apache.tomcat.util.http.fileupload.FileUploadBase$FileSizeLimitExceededException: The field fileList exceeds its maximum permitted size of 5242880 bytes."
        path:"/photo/image/upload"
}
 */

import com.google.gson.annotations.SerializedName;

public class Response_InternalServerError {
    @SerializedName("timestamp")
    private String timestamp;
    public String getTimestamp() { return timestamp; }

    @SerializedName("status")
    private int status;
    public int getStatus() { return status; }

    @SerializedName("error")
    private String error;
    public String getError() { return error; }

    @SerializedName("message")
    private String message;
    public String getMessage() { return message; }

    @SerializedName("path")
    private String path;
    public String getPath() { return path; }
}
