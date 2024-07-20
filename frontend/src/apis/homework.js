import {request} from "../utils";

export function createHomeworkAPI(token, username,classroom_id,title){
    return request({
        url:'/teacher/create_homework',
        method:'POST',
        data:{
            token,
            username,
            classroom_id,
            title
        }
        //token和用户名

    })
}

export function updateHomeworkAPI(token,username,homework_id,title,description,deadline){
    return request({
        url:'/teacher/update_homework',
        method:'POST',
        data:{
            token,
            username,
            homework_id,
            title,
            description,
            deadline
        }
    })
}

export function getSubmissionAPI(token,username,classroom_id){
    return request({
        url:'/teacher/list_homework',
        method:'POST',
        data:{
            token,
            username,
            classroom_id
        }
    })
}

//返回作业标题
export function getHomeworktitleAPI(token,username ,homework_id){
    return request({
        url:'/teacher/get_homework_brief',
        method:'POST',
        data:{
            token,
            username,
            homework_id
        }
    })
}

//返回提交的作业
export function getSubmittedHomeworkAPI(token,username,homework_id){
    return request({
        url:'/teacher/list_homework_submission',
        method:'POST',
        data:{
            token,
            username,
            homework_id
        }
    })
}

export function getSubmissionBriefAPI(token,username,submission_id){
    return request({
        url:'/teacher/get_submission_brief',
        method:'POST',
        data:{
            token,
            username,
            submission_id
        }
    })
}

//返回提交的作业详情
export function getSubmissionDetailAPI(token,username,submission_id){
    return request({
        url:'/teacher/get_submission_detail',
        method:'POST',
        data:{
            token,
            username,
            submission_id
        }
    })
}
export function gradeSubmissionAPI(token,username,submission_id,grade,comment) {
    return request({
        url: '/teacher/grade_submission',
        method: 'POST',
        data: {
            token,
            username,
            submission_id,
            grade,
            comment
        }
    })
}



export function getHomeworkAPI(token,username,classroom_id){
    return request({
        url:'/student/list_homework',
        method:'POST',
        data:{
            token,
            username,
            classroom_id
        }
    })
}

export function getHomeworkBriefAPI(token,username,homework_id){
    return request({
        url:'/student/get_homework_brief',
        method:'POST',
        data:{
            token,
            username,
            homework_id
        }
    })
}

export function getHomeworkDetailAPI(token,username,homework_id){
    return request({
        url:'/student/get_homework_detail',
        method:'POST',
        data:{
            token,
            username,
            homework_id
        }
    })
}

export function submitHomeworkAPI(token,username,homework_id,content){
    return request({
        url:'/student/submit_homework',
        method:'POST',
        data:{
            token,
            username,
            homework_id,
            content
        }
    })
}

export function checkSubmissionAPI(token,username,homework_id){
    return request({
        url:'/student/check_submission',
        method:'POST',
        data:{
            token,
            username,
            homework_id
        }
    })
}

export function getSubmissionGradeAPI(token,username,submission_id){
    return request({
        url:'/student/get_submission_grade',
        method:'POST',
        data:{
            token,
            username,
            submission_id
        }
    })
}