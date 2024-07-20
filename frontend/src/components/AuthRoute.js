//封装高阶组件
//核心逻辑：有token 正常跳转 无token 去登录

import {getToken} from '../utils'
import {Navigate, useLocation} from 'react-router-dom'

export function AuthRoute({children}){
    const token =getToken();
    const location = useLocation();
    if(token){
        try {
            const role = JSON.parse(atob(token.split('.')[1])).role;
            if(role === 'STUDENT' && !location.pathname.startsWith('/stuHome')){
                return <Navigate to={'/stuHome/viewAssignment'} replace />
            }else if(role === 'TEACHER' && location.pathname.startsWith('/stuHome')){
                return <Navigate to={'/'} replace />
            }
            return children;
        }
        catch (e) {
            console.log(e);
            return <Navigate to={'/login'} replace />
        }
    }else{
        return <Navigate to={'/login'} replace />
    }
}
