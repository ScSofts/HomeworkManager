import { request } from "../utils"
import {useEffect} from "react";

export function submitImageAPI(file, homeworkId, token, username, role) {
    let form = new FormData();
    form.append("file", file);
    return request({
                url: '/files/upload',
                method: 'POST',
                headers: {
                    'Content-Type': 'multipart/form-data',  // 因为我们发送的是二进制数据
                    'Token': token,
                    'Username': username,
                    'Role': role,
                    'Id': homeworkId
                },
                data: form,
            });
}

