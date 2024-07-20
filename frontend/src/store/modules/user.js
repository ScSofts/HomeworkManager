import { createSlice } from '@reduxjs/toolkit'
import {loginAPI, getProfileAPI, registerAPI} from '../../apis/user'
import { setToken as _setToken, getToken, removeToken } from '../../utils'
import {getClassAPI} from "../../apis/class";

// 用户状态管理
const userStore = createSlice({
    name: "user",
    initialState: {
        token: getToken('token_key') || '',
        userInfo: {}
    },
    reducers: {
        setToken(state, action) {
            state.token = action.payload
            _setToken(action.payload)
        },
        setUserInfo(state, action) {
            state.userInfo = action.payload
        },
        clearUserInfo(state) {
            state.token = ''
            state.userInfo = {}
            removeToken()
        }
    }
})

const { setToken, setUserInfo, clearUserInfo } = userStore.actions
const userReducer = userStore.reducer

const fetchLogin = (token) => {
    return async (dispatch) => {
        dispatch(setToken(token))
    }
}

const fetchRegister = (token) => {
    return async (dispatch) => {
        dispatch(setToken(token))
    }
}




export { fetchLogin, fetchRegister, setToken, setUserInfo, clearUserInfo}
export default userReducer