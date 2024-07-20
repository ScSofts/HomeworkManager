//封装获取频道列表逻辑
import {useEffect, useState} from "react";
import {getStuClassAPI} from "../apis/class";
import {useSelector} from "react-redux";

function useStuClass(){
    //1.获取班级列表所有逻辑

    //获取频道列表
    const [classList, setClassList] = useState([])

    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    console.log(username)
    useEffect(() => {
        //1.封装函数 在函数体内调用接口
        const getClassList = async () => {
            const res = await getStuClassAPI(token,username)
            console.log(res.data)
            setClassList(res.data)
        }
        //2.调用函数
        getClassList()
    }, [token,username])

    //2.把组件中要用到的数据return出去
    return {
        classList
    }
}

export {useStuClass}