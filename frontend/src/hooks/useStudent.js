import {useEffect, useState} from "react";
import {useSelector} from "react-redux";
import {getStudentListAPI} from "../apis/student";

function useStudent(classId){

    //1.获取班级列表所有逻辑

    //获取频道列表
    const [studentList, setStudentList] = useState([])

    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getStudentList = async () => {
            const res = await getStudentListAPI(token,username,classId)
            // console.log(res.data)
            setStudentList(res.data)
        }
        //2.调用函数
        if(classId != null)
            getStudentList()
    }, [token,username,classId])

    //2.把组件中要用到的数据return出去
    return {
        studentList
    }
}

// function

export {useStudent}