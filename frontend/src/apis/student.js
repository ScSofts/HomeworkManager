import {request} from "../utils";

export function getStudentListAPI(token, username, classroom_id) {
    return request({
        url: '/teacher/list_student',
        method: 'POST',
        data: {
            token,
            username,
            classroom_id
        }
    })
}

export function getStuJoinAPI(token, username) {
    return request({
        url: '/student/list_classroom',
        method: 'POST',
        data: {
            token,
            username
        }
    })
}
export function joinStudentAPI(token,username,classroom_id){
    return request({
        url:'/student/join_classroom',
        method:'POST',
        data:{
            token,
            username,
            classroom_id
        }
    })
}