//组合redux的子模块+导出store实例

import {configureStore} from '@reduxjs/toolkit'
import userReducer from "../store/modules/user";

export default configureStore({
    reducer:{
        user:userReducer
    }
})