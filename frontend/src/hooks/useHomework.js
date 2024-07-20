//封装获取频道列表逻辑
import {useEffect, useState} from "react";
import {
    getHomeworkAPI,
    getHomeworkBriefAPI,
    getHomeworkDetailAPI,
    getHomeworktitleAPI,
    getSubmissionAPI,
    getSubmittedHomeworkAPI,
    getSubmissionDetailAPI,
    getSubmissionBriefAPI,
    checkSubmissionAPI,
    getSubmissionGradeAPI
} from "../apis/homework";
import {useSelector} from "react-redux";


function useHomework(classId){

    //1.获取班级列表所有逻辑

    //获取频道列表
    const [homeworkList, setHomeworkList] = useState([])

    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getHomeworkList = async () => {
            const res = await getHomeworkAPI(token,username,classId)
            // console.log(res.data)
            setHomeworkList(res.data)
        }
        //2.调用函数
        if(classId != null)
            getHomeworkList()
    }, [token,username,classId])

    //2.把组件中要用到的数据return出去
    return {
            homeworkList
    }
}

function useHomeworkBrief(homeworkId, setTimestamp){
    //1.获取班级列表所有逻辑
    // const [timestamp, setTimestamp] = useState(null)
    const [title, setTitle] = useState(null)
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    console.log(username)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getHomeworkBrief = async () => {
            const res = await getHomeworkBriefAPI(token, username, homeworkId)
            // console.log(res.data)
            setTimestamp(res.data.created_at)
            // console.log(res.data.created_at)
            setTitle(res.data.title)
        }
        //2.调用函数
        if (homeworkId != null)
            getHomeworkBrief()
    }, [token, username, homeworkId,setTimestamp])

    //2.把组件中要用到的数据return出去
    return {
        // timestamp,
        title
    }
}

function useHomeworkDetail(homeworkId){
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    const [content, setContent] = useState(null)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getHomeworkDetail = async () => {
            const res = await getHomeworkDetailAPI(token, username, homeworkId)
            setContent(res.data.description)
        }
        //2.调用函数
        if (homeworkId != null)
            getHomeworkDetail()
    }, [token, username, homeworkId])

    //2.把组件中要用到的数据return出去
    return {
        content
    }
}

function useSubmission(classId){
    //1.获取班级列表所有逻辑

    //获取频道列表
    const [homeworkList, setHomeworkList] = useState([])

    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    console.log(username)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getHomeworkList = async () => {
            const res = await getSubmissionAPI(token,username,classId)
            // console.log(res.data)
            setHomeworkList(res.data)
        }
        //2.调用函数
        if(classId != null)
            getHomeworkList()
    }, [token,username,classId])

    //2.把组件中要用到的数据return出去
    return {
        homeworkList
    }
}

function useHomeworkTitle(homeworkIds, setTitles, titles){
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    console.log(username)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getHomeworkTitle = async () => {
            for(const homeworkId of homeworkIds) {
                const res = await getHomeworktitleAPI(token, username, homeworkId)
                setTitles(titles => ({...titles, [homeworkId]: res.data.title})) // titles.push(res.data.title)
            }
        }
        //2.调用函数
        if (homeworkIds != null)
            getHomeworkTitle()
    }, [token, username, homeworkIds])

    //2.把组件中要用到的数据return出去
    return {

    }
}
function useSubmittedHomework(homeworkId){
    //1.获取班级列表所有逻辑

    //获取频道列表
    const [submissionList, setSubmissionList] = useState([])

    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    console.log(username)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getHomeworkList = async () => {
            const res = await getSubmittedHomeworkAPI(token,username,homeworkId)
            // console.log(res.data)
            setSubmissionList(res.data)
        }
        //2.调用函数
        if(homeworkId != null)
            getHomeworkList()
    }, [token,username,homeworkId])

    //2.把组件中要用到的数据return出去
    return {
        submissionList
    }
}

function useSubmissionBrief(submissionId){
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    const [status, setStatus] = useState(null)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getSubmissionBrief = async () => {
            const res = await getSubmissionBriefAPI(token, username, submissionId)
            setStatus(res.data.status)
        }
        //2.调用函数
        if (submissionId != null)
            getSubmissionBrief()
    }, [token, username, submissionId])

    //2.把组件中要用到的数据return出去
    return {
        status,
    }
}

function useSubmissionDetail(submissionId){
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    const [content, setContent] = useState(null)
    const [name,setName]=useState(null)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getSubmissionDetail = async () => {
            const res = await getSubmissionDetailAPI(token, username, submissionId)
            setContent(res.data.content)
            setName(res.data.username)
        }
        //2.调用函数
        if (submissionId != null)
            getSubmissionDetail()
    }, [token, username, submissionId])

    //2.把组件中要用到的数据return出去
    return {

        name,
        content
    }
}

function  useCheckSubmission(homeworkId){
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    const [content, setContent] = useState(null)
    const [submissionId, setSubmissionId] = useState(null)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const checkSubmission = async () => {
            const res = await checkSubmissionAPI(token, username, homeworkId)
            setContent(res.data.content)
            setSubmissionId(res.data.submission_id)
        }
        //2.调用函数
        if (homeworkId != null)
            checkSubmission()
    }, [token, username, homeworkId])

    //2.把组件中要用到的数据return出去
    return {
        submissionId,
        content
    }

}

function useGetSubmissionGrade(submissionId){
    const token = useSelector(state => state.user.token)
    const username = JSON.parse(window.atob(token.split('.')[1])).username
    const [comment, setComment] = useState(null)
    const [score, setScore] = useState(null)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getSubmissionGrade = async () => {
            const res = await getSubmissionGradeAPI(token, username, submissionId)
            setComment(res.data.comment)
            setScore(res.data.score)
        }
        //2.调用函数
        if (submissionId != null)
            getSubmissionGrade()
    }, [token, username, submissionId])

    //2.把组件中要用到的数据return出去
    return {
        comment,
        score
    }
}


        export {useHomework,useHomeworkBrief,useHomeworkDetail,useSubmission,useSubmittedHomework,useSubmissionDetail,useHomeworkTitle,useSubmissionBrief,useCheckSubmission,useGetSubmissionGrade}
