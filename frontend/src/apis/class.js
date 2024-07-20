import {request} from "../utils";

export function getClassAPI(token, username){
    return request({
        url:'/teacher/list_classroom',
        method:'POST',
        data:{
            token,
            username
        }
        //token和用户名

    })
}

export function getStuClassAPI(token,username){
    return request({
        url:'/student/list_classroom',
        method:'POST',
        data:{
            token,
            username
        }
    })
}


export function getClassStatisticsAPI(token,username,classroom_id)
{
    return request({
        url:'/teacher/get_class_statistics',
        method:'POST',
        data:{
            token,
            username,
            classroom_id
        }
    })
}

export function createClassAPI(token,username){
    return request({
        url:'/teacher/create_classroom',
        method:'POST',
        data:{
            token,
            username,
        }
    })
}